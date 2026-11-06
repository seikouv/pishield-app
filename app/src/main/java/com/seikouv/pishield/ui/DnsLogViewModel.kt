package com.seikouv.pishield.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seikouv.pishield.api.RetrofitClient
import com.seikouv.pishield.data.DnsLogEntry
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class DnsLogFilter(val label: String) {
    ALL("All"),
    BLOCKED("Blocked"),
    ALLOWED("Allowed"),
    ADS("Ads"),
    TRACKING("Tracking"),
    AI("AI")
}

class DnsLogViewModel : ViewModel() {

    private val _entries = MutableStateFlow<List<DnsLogEntry>>(emptyList())
    val entries: StateFlow<List<DnsLogEntry>> = _entries.asStateFlow()

    private val _filter = MutableStateFlow(DnsLogFilter.ALL)
    val filter: StateFlow<DnsLogFilter> = _filter.asStateFlow()

    private val _isConnected = MutableStateFlow(true)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _isActive = MutableStateFlow(false)

    // Newest-first list, capped so the feed doesn't grow unbounded
    private val seenIds = LinkedHashSet<Long>()
    private val maxEntries = 300

    private val pollIntervalMs = 5000L
    private val fetchLimit = 50

    init {
        startPolling()
    }

    fun setActive(active: Boolean) {
        val wasInactive = !_isActive.value
        _isActive.value = active
        if (active && wasInactive) {
            viewModelScope.launch { fetchOnce() }
        }
    }

    fun setFilter(newFilter: DnsLogFilter) {
        if (_filter.value == newFilter) return
        _filter.value = newFilter
        // Clear and refetch immediately under the new filter so the
        // terminal feed reflects the selection right away.
        _entries.value = emptyList()
        seenIds.clear()
        viewModelScope.launch { fetchOnce() }
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (true) {
                if (_isActive.value) {
                    fetchOnce()
                }
                delay(pollIntervalMs)
            }
        }
    }

    private suspend fun fetchOnce() {
        Log.d("DnsLogViewModel", "fetchOnce() called - isActive: ${_isActive.value}")
        try {
            val currentFilter = _filter.value
            val blockedParam = when (currentFilter) {
                DnsLogFilter.BLOCKED -> true
                DnsLogFilter.ALLOWED -> false
                else -> null
            }
            val categoryParam = when (currentFilter) {
                DnsLogFilter.ADS -> "ads"
                DnsLogFilter.TRACKING -> "tracking"
                DnsLogFilter.AI -> "ai"
                else -> null
            }

            val result = RetrofitClient.api.getDnsLogs(
                limit = fetchLimit,
                offset = 0,
                blocked = blockedParam,
                category = categoryParam
            )

            // result is newest-first (API orders by id DESC).
            // Only append entries we haven't already shown, preserving
            // the existing list on transient failures.
            val newOnes = result.filter { it.id !in seenIds }
            if (newOnes.isNotEmpty()) {
                newOnes.forEach { seenIds.add(it.id) }
                // Prepend new entries (newest-first), then cap total size
                val merged = (newOnes + _entries.value).take(maxEntries)
                _entries.value = merged

                // Trim seenIds so it doesn't grow forever either
                if (seenIds.size > maxEntries) {
                    val keep = merged.map { it.id }.toSet()
                    seenIds.retainAll(keep)
                }
            }

            _isConnected.value = true
        } catch (e: Exception) {
            // Don't wipe existing entries on a transient network hiccup
            _isConnected.value = false
        }
    }
}
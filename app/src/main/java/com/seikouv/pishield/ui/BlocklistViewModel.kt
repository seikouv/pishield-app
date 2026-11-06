package com.seikouv.pishield

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seikouv.pishield.Validators
import com.seikouv.pishield.api.RetrofitClient
import com.seikouv.pishield.data.Blocklist
import com.seikouv.pishield.data.BlocklistHistoryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class BlocklistUiState(
    val blocklists: List<Blocklist> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val toggledCategory: String? = null,
    val toggleError: String? = null,
    val whitelist: List<String> = emptyList(),
    val blacklist: List<String> = emptyList(),
    val listActionError: String? = null,
    val isListActionInProgress: Boolean = false
)

class BlocklistViewModel : ViewModel() {
    private val api = RetrofitClient.api
    private val _uiState = MutableStateFlow(BlocklistUiState())
    val uiState: StateFlow<BlocklistUiState> = _uiState

    // History lives in its own StateFlow now. The chart is the only thing
    // that reads this, so the 5s poll no longer recomposes the whole screen
    // (domain lists, blocklist cards, etc).
    private val _history = MutableStateFlow<List<BlocklistHistoryPoint>>(emptyList())
    val history: StateFlow<List<BlocklistHistoryPoint>> = _history

    private val maxHistoryPoints = 15
    private var pollingStarted = false
    private val _isActive = MutableStateFlow(false)

    init {
        startStatsPolling()
    }

    fun setActive(active: Boolean) {
        val wasInactive = !_isActive.value
        _isActive.value = active
        if (active && wasInactive) {
            fetchBlocklists()
            fetchWhitelist()
            fetchBlacklist()
            viewModelScope.launch { fetchStatsOnce() }
        }
    }

    fun fetchBlocklists() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val blocklists = api.getBlocklists()
                _uiState.value = _uiState.value.copy(
                    blocklists = blocklists,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load blocklists: ${e.message}"
                )
            }
        }
    }

    private fun startStatsPolling() {
        if (pollingStarted) return
        pollingStarted = true

        viewModelScope.launch {
            while (true) {
                if (_isActive.value) {
                    fetchStatsOnce()
                }
                delay(5000)
            }
        }
    }

    private suspend fun fetchStatsOnce() {
        Log.d("BlocklistViewModel", "fetchStatsOnce() called - isActive: ${_isActive.value}")
        try {
            val stats = api.getBlocklistStats(windowSeconds = 10)
            val newPoint = BlocklistHistoryPoint(
                timestamp = System.currentTimeMillis(),
                adsCount = stats.ads,
                trackingCount = stats.tracking,
                aiCount = stats.ai
            )
            _history.value = (_history.value + newPoint).takeLast(maxHistoryPoints)

            // Only clear a stale error here, don't touch anything else
            // in uiState so this poll can't trigger a full-screen recompose.
            if (_uiState.value.error != null) {
                _uiState.value = _uiState.value.copy(error = null)
            }
        } catch (e: Exception) {
            // Don't wipe existing history/graph on a transient failure,
            // just surface the error.
            _uiState.value = _uiState.value.copy(
                error = "Failed to refresh stats: ${e.message}"
            )
        }
    }

    fun toggleBlocklistCategory(category: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(toggledCategory = category, toggleError = null)
            try {
                api.toggleBlocklistCategory(category)
                fetchBlocklists()
                _uiState.value = _uiState.value.copy(toggledCategory = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    toggledCategory = null,
                    toggleError = "Failed to toggle $category: ${e.message}"
                )
            }
        }
    }
    fun fetchWhitelist() {
        viewModelScope.launch {
            try {
                val list = api.getWhitelist()
                _uiState.value = _uiState.value.copy(whitelist = list)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(listActionError = "Failed to load whitelist: ${e.message}")
            }
        }
    }

    fun fetchBlacklist() {
        viewModelScope.launch {
            try {
                val list = api.getCustomBlocklist()
                _uiState.value = _uiState.value.copy(blacklist = list)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(listActionError = "Failed to load blacklist: ${e.message}")
            }
        }
    }

    fun addToWhitelist(domain: String) {
        if (!Validators.isValidDomain(domain)) {
            _uiState.value = _uiState.value.copy(listActionError = "Invalid domain syntax: $domain")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isListActionInProgress = true, listActionError = null)
            try {
                api.addWhitelist(mapOf("domain" to domain))
                fetchWhitelist()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(listActionError = "Failed to add $domain: ${e.message}")
            } finally {
                _uiState.value = _uiState.value.copy(isListActionInProgress = false)
            }
        }
    }

    fun removeFromWhitelist(domain: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isListActionInProgress = true, listActionError = null)
            try {
                api.deleteWhitelist(domain)
                fetchWhitelist()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(listActionError = "Failed to remove $domain: ${e.message}")
            } finally {
                _uiState.value = _uiState.value.copy(isListActionInProgress = false)
            }
        }
    }

    fun addToBlacklist(domain: String) {
        if (!Validators.isValidDomain(domain)) {
            _uiState.value = _uiState.value.copy(listActionError = "Invalid domain syntax: $domain")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isListActionInProgress = true, listActionError = null)
            try {
                api.addCustomBlocklist(mapOf("domain" to domain))
                fetchBlacklist()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(listActionError = "Failed to add $domain: ${e.message}")
            } finally {
                _uiState.value = _uiState.value.copy(isListActionInProgress = false)
            }
        }
    }

    fun removeFromBlacklist(domain: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isListActionInProgress = true, listActionError = null)
            try {
                api.deleteCustomBlocklist(domain)
                fetchBlacklist()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(listActionError = "Failed to remove $domain: ${e.message}")
            } finally {
                _uiState.value = _uiState.value.copy(isListActionInProgress = false)
            }
        }
    }
}
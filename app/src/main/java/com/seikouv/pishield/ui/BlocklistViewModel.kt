package com.seikouv.pishield

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seikouv.pishield.api.RetrofitClient
import com.seikouv.pishield.data.Blocklist
import com.seikouv.pishield.data.BlocklistHistoryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class BlocklistUiState(
    val blocklists: List<Blocklist> = emptyList(),
    val history: List<BlocklistHistoryPoint> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val toggledCategory: String? = null,
    val toggleError: String? = null
)

class BlocklistViewModel : ViewModel() {
    private val api = RetrofitClient.api
    private val _uiState = MutableStateFlow(BlocklistUiState())
    val uiState: StateFlow<BlocklistUiState> = _uiState

    private val maxHistoryPoints = 60
    private var pollingStarted = false

    init {
        fetchBlocklists()
        startStatsPolling()
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
                try {
                    val stats = api.getBlocklistStats(windowSeconds = 60)
                    val newPoint = BlocklistHistoryPoint(
                        timestamp = System.currentTimeMillis(),
                        adsCount = stats.ads,
                        trackingCount = stats.tracking,
                        aiCount = stats.ai
                    )
                    val updatedHistory = (_uiState.value.history + newPoint)
                        .takeLast(maxHistoryPoints)

                    _uiState.value = _uiState.value.copy(
                        history = updatedHistory,
                        error = null
                    )
                } catch (e: Exception) {
                    // Don't wipe existing history/graph on a transient failure,
                    // just surface the error.
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to refresh stats: ${e.message}"
                    )
                }
                delay(5000)
            }
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
}
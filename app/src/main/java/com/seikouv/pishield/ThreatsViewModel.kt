package com.seikouv.pishield

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seikouv.pishield.api.RetrofitClient
import com.seikouv.pishield.data.Threat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ThreatsUiState(
    val threats: List<Threat> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val deletingId: Int? = null,
    val isClearingAll: Boolean = false
)

class ThreatsViewModel : ViewModel() {
    private val api = RetrofitClient.api
    private val _uiState = MutableStateFlow(ThreatsUiState())
    val uiState: StateFlow<ThreatsUiState> = _uiState

    init {
        fetchThreats()
    }

    fun fetchThreats(isPullToRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = !isPullToRefresh,
                isRefreshing = isPullToRefresh,
                error = null
            )
            try {
                val threats = api.getThreats(limit = 100)
                _uiState.value = _uiState.value.copy(
                    threats = threats,
                    isLoading = false,
                    isRefreshing = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    error = "Failed to load threats: ${e.message}"
                )
            }
        }
    }

    fun deleteThreat(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(deletingId = id)
            try {
                api.deleteThreat(id)
                _uiState.value = _uiState.value.copy(
                    threats = _uiState.value.threats.filter { it.id != id },
                    deletingId = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    deletingId = null,
                    error = "Failed to delete threat: ${e.message}"
                )
            }
        }
    }

    fun clearAllThreats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isClearingAll = true, error = null)
            try {
                api.clearAllThreats()
                _uiState.value = _uiState.value.copy(threats = emptyList())
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to clear threats: ${e.message}"
                )
            } finally {
                _uiState.value = _uiState.value.copy(isClearingAll = false)
            }
        }
    }
}
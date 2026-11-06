package com.seikouv.pishield.ui

import android.util.Log
import com.seikouv.pishield.data.SystemStats
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seikouv.pishield.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(val data: SystemStats) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

class DashboardViewModel : ViewModel() {
    private val _dashboardState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()

    private val _isActive = MutableStateFlow(false)

    init {
        startLiveRefresh()
    }

    fun setActive(active: Boolean) {
        val wasInactive = !_isActive.value
        _isActive.value = active
        if (active && wasInactive) {
            fetchDashboard()
        }
    }

    private fun startLiveRefresh() {
        viewModelScope.launch {
            while (true) {
                if (_isActive.value) {
                    fetchDashboard()
                }
                delay(3000) // Refresh every 3 seconds
            }
        }
    }

    fun fetchDashboard() {
        Log.d("DashboardViewModel", "fetchDashboard() called - isActive: ${_isActive.value}")
        viewModelScope.launch {
            try {
                val data = RetrofitClient.api.getSystemStats()
                _dashboardState.value = DashboardState.Success(data)
            } catch (e: Exception) {
                _dashboardState.value = DashboardState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
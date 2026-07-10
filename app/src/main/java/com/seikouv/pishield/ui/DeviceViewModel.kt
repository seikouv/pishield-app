package com.seikouv.pishield.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seikouv.pishield.api.RetrofitClient
import com.seikouv.pishield.data.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DeviceState {
    object Loading : DeviceState()
    data class Success(val devices: List<Device>) : DeviceState()
    data class Error(val message: String) : DeviceState()
}

class DeviceViewModel : ViewModel() {
    private val _deviceState = MutableStateFlow<DeviceState>(DeviceState.Loading)
    val deviceState: StateFlow<DeviceState> = _deviceState.asStateFlow()

    init {
        fetchDevices()
    }

    fun fetchDevices() {
        viewModelScope.launch {
            _deviceState.value = DeviceState.Loading
            try {
                val devices = RetrofitClient.api.getDevices()
                _deviceState.value = DeviceState.Success(devices)
            } catch (e: Exception) {
                _deviceState.value = DeviceState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun blockDevice(deviceId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.blockDevice(deviceId)
                if (response.success) {
                    fetchDevices()
                }
            } catch (e: Exception) {
                _deviceState.value = DeviceState.Error(e.message ?: "Failed to block device")
            }
        }
    }

    fun unblockDevice(deviceId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.unblockDevice(deviceId)
                if (response.success) {
                    fetchDevices()
                }
            } catch (e: Exception) {
                _deviceState.value = DeviceState.Error(e.message ?: "Failed to unblock device")
            }
        }
    }
}

package com.seikouv.pishield.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seikouv.pishield.data.Device
import com.seikouv.pishield.ui.DeviceState
import com.seikouv.pishield.ui.DeviceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceScreen(viewModel: DeviceViewModel = viewModel(), modifier: Modifier = Modifier) {
    val state = viewModel.deviceState.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Devices") },
                actions = {
                    IconButton(onClick = { viewModel.fetchDevices() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        when (state) {
            is DeviceState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is DeviceState.Success -> {
                // DEBUG: print all devices and their lastSeen times
                state.devices.forEach { device ->
                    Log.d("DeviceScreen", "Device: ${device.hostname}, lastSeen: ${device.lastSeen}, isActive: ${isDeviceActive(device.lastSeen)}")
                }

                if (state.devices.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No devices found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        items(state.devices.filter { isDeviceActive(it.lastSeen) }) { device ->
                            DeviceCard(
                                device = device,
                                onBlock = { viewModel.blockDevice(device.mac) },
                                onUnblock = { viewModel.unblockDevice(device.mac) }
                            )
                        }
                    }
                }
            }
            is DeviceState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun DeviceCard(
    device: Device,
    onBlock: () -> Unit,
    onUnblock: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(device.hostname ?: device.mac, style = MaterialTheme.typography.titleMedium)
            Text(device.mac, style = MaterialTheme.typography.bodySmall)
            Text(device.ip, style = MaterialTheme.typography.bodySmall)
            Text("Last seen: ${device.lastSeen}", style = MaterialTheme.typography.labelSmall)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (device.blocked) {
                    Button(onClick = onUnblock, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Unblock")
                    }
                } else {
                    Button(onClick = onBlock, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                        Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Block")
                    }
                }
            }
        }
    }
}

fun isDeviceActive(lastSeen: String): Boolean {
    return try {
        val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val lastSeenTime = java.time.LocalDateTime.parse(lastSeen, formatter)
        val now = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Kolkata")).toLocalDateTime()
        val secondsAgo = java.time.temporal.ChronoUnit.SECONDS.between(lastSeenTime, now)
        secondsAgo < 3600  // Active if seen in last 1 hour
    } catch (e: Exception) {
        Log.e("DeviceScreen", "Failed to parse timestamp: $lastSeen", e)
        false
    }
}
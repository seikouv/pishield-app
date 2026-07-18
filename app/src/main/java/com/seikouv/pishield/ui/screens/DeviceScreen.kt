package com.seikouv.pishield.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seikouv.pishield.data.Device
import com.seikouv.pishield.ui.DeviceState
import com.seikouv.pishield.ui.DeviceViewModel
import com.seikouv.pishield.ui.components.PiShieldLoader
import com.seikouv.pishield.ui.theme.DarkColors
import com.seikouv.pishield.ui.theme.LightColors
import com.seikouv.pishield.ui.theme.LiveIndicator
import com.seikouv.pishield.ui.theme.ThemeState

private val CardBg = Color(0xFF14110A)
private val MutedText = Color(0xFF9CA3AF)

@Composable
fun DeviceScreen(viewModel: DeviceViewModel = viewModel(), modifier: Modifier = Modifier) {
    val state = viewModel.deviceState.collectAsState().value
    val isDark by ThemeState.isDark.collectAsState()
    val accentColor by ThemeState.accentColor.collectAsState()

    val bg = if (isDark) DarkColors.TerminalBg else LightColors.TerminalBg
    val amber = accentColor
    val amberDim = accentColor.copy(alpha = 0.6f)
    val cardBg = if (isDark) CardBg else LightColors.GlassFill
    val mutedText = if (isDark) MutedText else LightColors.TextSecondary

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "DEVICES",
                color = amber,
                fontFamily = FontFamily.Monospace,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                LiveIndicator(isConnected = state !is DeviceState.Error, isDark = isDark)
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { viewModel.fetchDevices() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = amber)
                }
            }
        }

        when (state) {
            is DeviceState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    PiShieldLoader(color = amber)
                }
            }
            is DeviceState.Success -> {
                val activeDevices = state.devices.filter { it.lastSeen >= cutoffTimestamp(state.devices) }

                if (activeDevices.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No devices found", color = mutedText)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(activeDevices, key = { it.mac }) { device ->
                            DeviceCard(
                                device = device,
                                onBlock = { viewModel.blockDevice(device.mac) },
                                onUnblock = { viewModel.unblockDevice(device.mac) },
                                isDark = isDark,
                                cardBg = cardBg,
                                amber = amber,
                                amberDim = amberDim,
                                mutedText = mutedText
                            )
                        }
                    }
                }
            }
            is DeviceState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${state.message}", color = DarkColors.AlertRed)
                }
            }
        }
    }
}

@Composable
fun DeviceCard(
    device: Device,
    onBlock: () -> Unit,
    onUnblock: () -> Unit,
    isDark: Boolean,
    cardBg: Color,
    amber: Color,
    amberDim: Color,
    mutedText: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(cardBg)
            .border(1.dp, amberDim, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            device.hostname ?: device.mac,
            style = MaterialTheme.typography.titleMedium,
            color = amber
        )
        Text(device.mac, style = MaterialTheme.typography.bodySmall, color = mutedText)
        Text(device.ip, style = MaterialTheme.typography.bodySmall, color = mutedText)
        Text("Last seen: ${device.lastSeen}", style = MaterialTheme.typography.labelSmall, color = mutedText)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (device.blocked) {
                Button(
                    onClick = onUnblock,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = amber)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = if (isDark) Color.Black else Color.White, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Unblock", color = if (isDark) Color.Black else Color.White)
                }
            } else {
                Button(
                    onClick = onBlock,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkColors.AlertRed)
                ) {
                    Icon(Icons.Default.Block, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Block", color = Color.White)
                }
            }
        }
    }
}

fun cutoffTimestamp(devices: List<Device>): String {
    if (devices.isEmpty()) return ""
    val newest = devices.maxOf { it.lastSeen }
    return try {
        val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val newestTime = java.time.LocalDateTime.parse(newest.trim(), formatter)
        val cutoff = newestTime.minusHours(1)
        cutoff.format(formatter)
    } catch (e: Exception) {
        ""  // fail-safe: cutoff of "" means everything passes the >= check
    }
}
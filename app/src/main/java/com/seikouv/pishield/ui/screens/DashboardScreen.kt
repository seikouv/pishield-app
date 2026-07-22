package com.seikouv.pishield.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
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
import com.seikouv.pishield.ui.DashboardViewModel
import com.seikouv.pishield.ui.DashboardState
import com.seikouv.pishield.ui.components.PiShieldLoader
import com.seikouv.pishield.ui.theme.DarkColors
import com.seikouv.pishield.ui.theme.LightColors
import com.seikouv.pishield.ui.theme.LiveIndicator
import com.seikouv.pishield.ui.theme.ThemeState

private val CardBg = Color(0xFF0F1E3D)

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel(), modifier: Modifier = Modifier) {
    val state = viewModel.dashboardState.collectAsState().value
    val isDark by ThemeState.isDark.collectAsState()
    val accentColor by ThemeState.accentColor.collectAsState()

    val bg = if (isDark) DarkColors.TerminalBg else LightColors.TerminalBg
    val accent = accentColor
    val accentDim = accentColor.copy(alpha = 0.6f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "PISHIELD",
                color = accent,
                fontFamily = FontFamily.Monospace,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                LiveIndicator(isConnected = state !is DashboardState.Error, isDark = isDark)
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(onClick = { viewModel.fetchDashboard() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = accent)
                }
            }
        }

        when (state) {
            is DashboardState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    PiShieldLoader(color = accent)
                }
            }
            is DashboardState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Active Threats",
                        value = state.data.threat_count.toString(),
                        isAlert = state.data.threat_count > 0,
                        isDark = isDark,
                        accentColor = accentColor
                    )
                    StatCard(
                        title = "Threats (24h)",
                        value = state.data.threat_count_24h.toString(),
                        isDark = isDark
                    )
                    StatCard(
                        title = "Memory Usage",
                        value = "%.1f%%".format(state.data.memory_percent),
                        isDark = isDark
                    )
                    StatCard(
                        title = "Active Connections",
                        value = state.data.active_connections.toString(),
                        isDark = isDark
                    )
                    StatCard(
                        title = "Uptime",
                        value = formatUptime(state.data.uptime_seconds),
                        isDark = isDark,
                        accentColor = accentColor
                    )
                }
            }
            is DashboardState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${state.message}", color = DarkColors.AlertRed)
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, isAlert: Boolean = false, isDark: Boolean = true, accentColor: Color = DarkColors.AccentBright) {
    val accent = if (isAlert) DarkColors.AlertRed else accentColor
    val cardBg = if (isDark) CardBg else LightColors.GlassFill
    val border = accentColor.copy(alpha = 0.4f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(cardBg)
            .border(1.dp, border, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(title, style = MaterialTheme.typography.labelMedium, color = accentColor.copy(alpha = 0.7f))
        Text(value, style = MaterialTheme.typography.headlineMedium, color = accent, fontWeight = FontWeight.Bold)
    }
}

private fun formatUptime(seconds: Long): String {
    val days = seconds / 86400
    val hours = (seconds % 86400) / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return when {
        days > 0 -> "$days days, $hours:${String.format("%02d", minutes)}:${String.format("%02d", secs)}"
        hours > 0 -> "$hours:${String.format("%02d", minutes)}:${String.format("%02d", secs)}"
        else -> "$minutes:${String.format("%02d", secs)}"
    }
}
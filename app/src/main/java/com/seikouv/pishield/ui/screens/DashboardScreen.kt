package com.seikouv.pishield.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seikouv.pishield.ui.DashboardViewModel
import com.seikouv.pishield.ui.DashboardState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel(), modifier: Modifier = Modifier) {
    val state = viewModel.dashboardState.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PiShield Dashboard") },
                actions = {
                    IconButton(onClick = { viewModel.fetchDashboard() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        when (state) {
            is DashboardState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is DashboardState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        title = "Active Threats",
                        value = state.data.threat_count.toString(),
                        color = if (state.data.threat_count > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                    StatCard(
                        title = "Threats (24h)",
                        value = state.data.threat_count_24h.toString(),
                        color = MaterialTheme.colorScheme.primary
                    )
                    StatCard(
                        title = "Memory Usage",
                        value = "%.1f%%".format(state.data.memory_percent),
                        color = MaterialTheme.colorScheme.primary
                    )
                    StatCard(
                        title = "Active Connections",
                        value = state.data.active_connections.toString(),
                        color = MaterialTheme.colorScheme.primary
                    )
                    StatCard(
                        title = "Uptime",
                        value = formatUptime(state.data.uptime_seconds),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            is DashboardState.Error -> {
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
fun StatCard(title: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.headlineMedium, color = color)
        }
    }
}
fun formatUptime(seconds: Long): String {
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
package com.seikouv.pishield.ui.screens
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seikouv.pishield.ThreatsViewModel
import com.seikouv.pishield.data.Threat

@Composable
fun ThreatsScreen(
    modifier: Modifier = Modifier,
    viewModel: ThreatsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header row: title + refresh + clear all
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Threats",
                style = MaterialTheme.typography.headlineMedium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.fetchThreats(isPullToRefresh = true) }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
                if (state.threats.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearAllThreats() }) {
                        Text("Clear All")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (state.error != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF5F2C2C))
            ) {
                Text(
                    state.error ?: "",
                    modifier = Modifier.padding(12.dp),
                    color = Color(0xFFFFCDD2),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (state.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.threats.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "No threats detected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF9CA3AF)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.threats, key = { it.id }) { threat ->
                    ThreatCard(
                        threat = threat,
                        isDeleting = state.deletingId == threat.id,
                        onDelete = { viewModel.deleteThreat(threat.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ThreatCard(
    threat: Threat,
    isDeleting: Boolean,
    onDelete: () -> Unit
) {
    val severityColor = when (threat.threatType) {
        "PORT_SCAN", "SYN_FLOOD", "ICMP_FLOOD" -> Color(0xFFFF4500) // high - red
        "NEW_DEVICE" -> Color(0xFFF59E0B) // medium - amber
        else -> Color(0xFF9CA3AF) // low - gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111827))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .background(severityColor, shape = androidx.compose.foundation.shape.CircleShape)
                        .height(10.dp)
                        .width(10.dp)
                )

                Column {
                    Text(
                        threat.threatType.replace("_", " "),
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White
                    )
                    Text(
                        threat.detail,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9CA3AF)
                    )
                    Text(
                        "${threat.srcIp} → ${threat.dstIp}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B7280)
                    )
                    Text(
                        threat.timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B7280)
                    )
                    if (threat.blocked) {
                        Text(
                            "Blocked",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }

            if (isDeleting) {
                CircularProgressIndicator(modifier = Modifier.height(20.dp).width(20.dp))
            } else {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF6B6B))
                }
            }
        }
    }
}
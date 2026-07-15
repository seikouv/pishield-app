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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seikouv.pishield.ThreatsViewModel
import com.seikouv.pishield.data.Threat
import com.seikouv.pishield.ui.components.PiShieldLoader
import com.seikouv.pishield.ui.theme.DarkColors
import com.seikouv.pishield.ui.theme.LightColors
import com.seikouv.pishield.ui.theme.LiveIndicator
import com.seikouv.pishield.ui.theme.ThemeState

private val CardBg = Color(0xFF14110A)
private val ErrorBg = Color(0xFF5F2C2C)
private val ErrorText = Color(0xFFFFCDD2)
private val MutedText = Color(0xFF9CA3AF)
private val DimText = Color(0xFF6B7280)
private val BlockedGreen = Color(0xFF10B981)

@Composable
fun ThreatsScreen(
    modifier: Modifier = Modifier,
    viewModel: ThreatsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val isDark by ThemeState.isDark.collectAsState()
    val accentColor by ThemeState.accentColor.collectAsState()

    val bg = if (isDark) DarkColors.TerminalBg else LightColors.TerminalBg
    val amber = accentColor
    val amberDim = accentColor.copy(alpha = 0.6f)
    val cardBg = if (isDark) CardBg else LightColors.GlassFill

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
    ) {
        // Header row: title + live indicator + refresh + clear all
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "THREATS",
                color = amber,
                fontFamily = FontFamily.Monospace,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                LiveIndicator(isConnected = state.error == null, isDark = isDark)
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { viewModel.fetchThreats(isPullToRefresh = true) }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = amber)
                }
                if (state.threats.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearAllThreats() }) {
                        Text("Clear All", color = amberDim)
                    }
                }
            }
        }

        if (state.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ErrorBg)
                    .padding(12.dp)
            ) {
                Text(state.error ?: "", color = ErrorText, style = MaterialTheme.typography.bodySmall)
            }
        }

        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    PiShieldLoader(color = amber)
                }
            }
            state.threats.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No threats detected", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.threats, key = { it.id }) { threat ->
                        ThreatCard(
                            threat = threat,
                            isDeleting = state.deletingId == threat.id,
                            onDelete = { viewModel.deleteThreat(threat.id) },
                            cardBg = cardBg,
                            amber = amber,
                            amberDim = amberDim
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ThreatCard(
    threat: Threat,
    isDeleting: Boolean,
    onDelete: () -> Unit,
    cardBg: Color,
    amber: Color,
    amberDim: Color
) {
    val severityColor = when (threat.threatType) {
        "PORT_SCAN", "SYN_FLOOD", "ICMP_FLOOD" -> DarkColors.AlertRed
        "NEW_DEVICE" -> Color(0xFFF59E0B)
        else -> MutedText
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(cardBg)
            .border(1.dp, amberDim, RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .background(severityColor, shape = CircleShape)
                    .height(10.dp)
                    .width(10.dp)
            )

            Column {
                Text(
                    threat.threatType.replace("_", " "),
                    style = MaterialTheme.typography.titleSmall,
                    color = amber
                )
                Text(threat.detail, style = MaterialTheme.typography.bodySmall, color = MutedText)
                Text(
                    "${threat.srcIp} \u2192 ${threat.dstIp}",
                    style = MaterialTheme.typography.bodySmall,
                    color = DimText
                )
                Text(threat.timestamp, style = MaterialTheme.typography.bodySmall, color = DimText)
                if (threat.blocked) {
                    Text("Blocked", style = MaterialTheme.typography.bodySmall, color = BlockedGreen)
                }
            }
        }

        if (isDeleting) {
            PiShieldLoader(
                color = amber
            )
        } else {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DarkColors.AlertRed)
            }
        }
    }
}
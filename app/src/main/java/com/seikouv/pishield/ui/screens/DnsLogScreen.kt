package com.seikouv.pishield.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seikouv.pishield.data.DnsLogEntry
import com.seikouv.pishield.ui.DnsLogFilter
import com.seikouv.pishield.ui.DnsLogViewModel
import com.seikouv.pishield.ui.theme.DarkColors
import com.seikouv.pishield.ui.theme.LightColors
import com.seikouv.pishield.ui.theme.LiveIndicator
import com.seikouv.pishield.ui.theme.ThemeState

// Flat colors, no gradient/glass
private val LogBg = Color.Black

@Composable
fun DnsLogScreen(modifier: Modifier = Modifier, viewModel: DnsLogViewModel = viewModel()) {
    val entries by viewModel.entries.collectAsState()
    val currentFilter by viewModel.filter.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val isDark by ThemeState.isDark.collectAsState()
    val accentColor by ThemeState.accentColor.collectAsState()

    val bg = if (isDark) DarkColors.TerminalBg else LightColors.TerminalBg
    val accent = accentColor
    val accentDim = accentColor.copy(alpha = 0.6f)
    val logBg = if (isDark) Color.Black else LightColors.GlassFill

    val listState = rememberLazyListState()

    LaunchedEffect(entries.size) {
        if (entries.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

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
                text = "DNS_LOGS.LOG",
                color = accent,
                fontFamily = FontFamily.Monospace,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            LiveIndicator(isConnected = isConnected, isDark = isDark)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DnsLogFilter.values().forEach { filterOption ->
                FilterChipItem(
                    label = filterOption.label,
                    selected = currentFilter == filterOption,
                    onClick = { viewModel.setFilter(filterOption) },
                    accent = accent,
                    accentDim = accentDim,
                    isDark = isDark
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(logBg)
                .border(1.dp, accentDim, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            if (entries.isEmpty()) {
                Text(
                    text = if (isConnected) "waiting for traffic..." else "connection lost. retrying...",
                    color = accentDim,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    state = listState,
                    reverseLayout = true,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(entries, key = { it.id }) { entry ->
                        TerminalLogLine(entry, accent, accentDim)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun TerminalLogLine(entry: DnsLogEntry, accent: Color, accentDim: Color) {
    // No per-row fade/expand animation - just render immediately.
    // With up to 300 live-updating entries, animating each new row was
    // the main source of lag.
    val timeLabel = remember(entry.timestamp) {
        entry.timestamp.substringAfterLast(" ")
    }

    val statusText = if (entry.blocked) {
        "BLOCKED" + (entry.category?.let { " [$it]" } ?: "")
    } else {
        "allowed"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "[$timeLabel]",
            color = accentDim,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = entry.domain,
            color = if (entry.blocked) accent else accentDim,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = statusText,
            color = if (entry.blocked) DarkColors.AlertRed else accentDim,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = if (entry.blocked) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun FilterChipItem(label: String, selected: Boolean, onClick: () -> Unit, accent: Color, accentDim: Color, isDark: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (selected) accent else (if (isDark) Color(0xFF0F1E3D) else Color(0xFFE5E7EB)))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (selected) (if (isDark) Color.Black else Color.White) else accentDim,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
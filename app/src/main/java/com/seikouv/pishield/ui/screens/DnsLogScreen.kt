package com.seikouv.pishield.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
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

// Amber retro-terminal palette
private val TerminalBg = Color(0xFF0A0A0A)
private val AmberBright = Color(0xFFFFB000)
private val AmberDim = Color(0xFF8A5E00)
private val AmberMuted = Color(0xFF5C4400)
private val ChipBg = Color(0xFF1A1400)
private val AlertRed = Color(0xFFFF4500)

@Composable
fun DnsLogScreen(modifier: Modifier = Modifier, viewModel: DnsLogViewModel = viewModel()) {
    val entries by viewModel.entries.collectAsState()
    val currentFilter by viewModel.filter.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()

    val listState = rememberLazyListState()

    // Auto-scroll to the newest entry (index 0, since list is newest-first
    // and reverseLayout puts index 0 at the bottom) whenever new data lands.
    LaunchedEffect(entries.size) {
        if (entries.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBg)
    ) {
        // Header with connection indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "DNS_LOGS.log",
                color = AmberBright,
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isConnected) AmberBright else Color(0xFF8A0000))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isConnected) "LIVE" else "OFFLINE",
                    color = if (isConnected) AmberDim else Color(0xFF8A0000),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            }
        }

        // Filter chips
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
                    onClick = { viewModel.setFilter(filterOption) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Terminal feed
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black)
                .padding(12.dp)
        ) {
            if (entries.isEmpty()) {
                Text(
                    text = if (isConnected) "waiting for traffic..." else "connection lost. retrying...",
                    color = AmberMuted,
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
                        TerminalLogLine(entry)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun TerminalLogLine(entry: DnsLogEntry) {
    var visible by remember(entry.id) { mutableStateOf(false) }
    LaunchedEffect(entry.id) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically()
    ) {
        val timeLabel = remember(entry.timestamp) {
            // "Jul 11 21:52:48" -> "21:52:48"
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
                color = AmberMuted,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = entry.domain,
                color = if (entry.blocked) AmberBright else AmberDim,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = statusText,
                color = if (entry.blocked) AlertRed else AmberDim,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = if (entry.blocked) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun FilterChipItem(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (selected) AmberBright else ChipBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (selected) Color.Black else AmberDim,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
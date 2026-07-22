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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SwitchDefaults
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.seikouv.pishield.BlocklistViewModel
import com.seikouv.pishield.data.BlocklistHistoryPoint
import com.seikouv.pishield.ui.components.PiShieldLoader
import com.seikouv.pishield.ui.theme.DarkColors
import com.seikouv.pishield.ui.theme.LightColors
import com.seikouv.pishield.ui.theme.ThemeState
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Flat blue palette, no alpha/blur/shadow
private val CardBg = Color(0xFF0F1E3D)
private val ErrorBg = Color(0xFF5F2C2C)
private val ErrorText = Color(0xFFFFCDD2)
private val MutedText = Color(0xFF9CA3AF)

@Composable
fun BlocklistScreen(
    modifier: Modifier = Modifier,
    viewModel: BlocklistViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val history by viewModel.history.collectAsState()
    val isDark by ThemeState.isDark.collectAsState()
    val accentColor by ThemeState.accentColor.collectAsState()

    val bg = if (isDark) DarkColors.TerminalBg else LightColors.TerminalBg
    val accent = accentColor
    val accentDim = accentColor.copy(alpha = 0.6f)
    val cardBg = if (isDark) Color(0xFF0F1E3D) else LightColors.GlassFill
    val mutedText = if (isDark) Color(0xFF9CA3AF) else LightColors.TextSecondary

    var whitelistInput by remember { mutableStateOf("") }
    var blacklistInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
            .padding(horizontal = 16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                "BLOCKLISTS",
                color = accent,
                fontFamily = FontFamily.Monospace,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (state.error != null) {
            item { ErrorBanner(state.error ?: "") }
        }

        if (state.isLoading) {
            item {
                PiShieldLoader(
                    color = accent,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        item {
            Text(
                "Live Blocking Activity",
                style = MaterialTheme.typography.titleMedium,
                color = accent,
                modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
            )
        }

        item { BlocklistLineChart(history = history, isDark = isDark, cardBg = cardBg, accentDim = accentDim, accent = accent) }
        item { LegendRow(mutedText = mutedText, accent = accent) }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // Whitelist section header + input
        item {
            DomainListHeader(
                title = "Whitelist",
                inputText = whitelistInput,
                onInputChange = { whitelistInput = it },
                onAdd = {
                    if (whitelistInput.isNotBlank()) {
                        viewModel.addToWhitelist(whitelistInput.trim())
                        whitelistInput = ""
                    }
                },
                accent = accent,
                isDark = isDark
            )
        }
        if (state.whitelist.isEmpty()) {
            item { EmptyListText() }
        } else {
            items(state.whitelist, key = { "wl_$it" }) { domain ->
                DomainRow(domain = domain, onRemove = { viewModel.removeFromWhitelist(domain) }, cardBg = cardBg, accent = accent, accentDim = accentDim)
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Blacklist section header + input
        item {
            DomainListHeader(
                title = "Blacklist",
                inputText = blacklistInput,
                onInputChange = { blacklistInput = it },
                onAdd = {
                    if (blacklistInput.isNotBlank()) {
                        viewModel.addToBlacklist(blacklistInput.trim())
                        blacklistInput = ""
                    }
                },
                accent = accent,
                isDark = isDark
            )
        }
        if (state.blacklist.isEmpty()) {
            item { EmptyListText() }
        } else {
            items(state.blacklist, key = { "bl_$it" }) { domain ->
                DomainRow(domain = domain, onRemove = { viewModel.removeFromBlacklist(domain) }, cardBg = cardBg, accent = accent, accentDim = accentDim)
            }
        }

        if (state.listActionError != null) {
            item { ErrorBanner(state.listActionError ?: "", topPadding = true) }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        if (state.blocklists.isNotEmpty()) {
            item {
                Text(
                    "Active Blocklists",
                    style = MaterialTheme.typography.titleMedium,
                    color = accent,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            items(state.blocklists, key = { it.category }) { blocklist ->
                BlocklistCard(
                    blocklist = blocklist,
                    isToggling = state.toggledCategory == blocklist.category,
                    onToggle = { viewModel.toggleBlocklistCategory(blocklist.category) },
                    cardBg = cardBg,
                    accent = accent,
                    accentDim = accentDim,
                    mutedText = mutedText
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (state.toggleError != null) {
                item { ErrorBanner(state.toggleError ?: "", topPadding = true) }
            }
        }
    }
}

@Composable
private fun ErrorBanner(message: String, topPadding: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (topPadding) 8.dp else 0.dp, bottom = if (topPadding) 0.dp else 16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(ErrorBg)
            .padding(12.dp)
    ) {
        Text(message, color = ErrorText, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun EmptyListText(mutedText: Color = MutedText) {
    Text(
        "No domains added",
        style = MaterialTheme.typography.bodySmall,
        color = mutedText,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun BlocklistLineChart(history: List<BlocklistHistoryPoint>, isDark: Boolean, cardBg: Color, accentDim: Color, accent: Color) {
    val adsEntries = remember(history) { history.mapIndexed { i, p -> FloatEntry(i.toFloat(), p.adsCount.toFloat()) } }
    val trackingEntries = remember(history) { history.mapIndexed { i, p -> FloatEntry(i.toFloat(), p.trackingCount.toFloat()) } }
    val aiEntries = remember(history) { history.mapIndexed { i, p -> FloatEntry(i.toFloat(), p.aiCount.toFloat()) } }

    val chartEntryModelProducer = remember { ChartEntryModelProducer() }

    LaunchedEffect(history) {
        if (history.size >= 2) {
            chartEntryModelProducer.setEntries(adsEntries, trackingEntries, aiEntries)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(cardBg)
            .border(1.dp, accentDim, RoundedCornerShape(8.dp))
    ) {
        if (history.size < 2) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Waiting for data...", color = if (isDark) MutedText else LightColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            ProvideChartStyle(chartStyle = m3ChartStyle()) {
                Chart(
                    chart = lineChart(
                        lines = listOf(
                            lineSpec(lineColor = Color(0xFF3B82F6)), // ads - blue
                            lineSpec(lineColor = Color(0xFF10B981)), // tracking - green
                            lineSpec(lineColor = accent)  // ai - dynamic
                        )
                    ),
                    chartModelProducer = chartEntryModelProducer,
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(
                        guideline = null,
                        valueFormatter = { _, _ -> "" }
                    ),
                    chartScrollSpec = rememberChartScrollSpec(isScrollEnabled = false),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .height(250.dp)
                )
            }
        }
    }
}

@Composable
fun LegendRow(mutedText: Color, accent: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LegendItem(color = Color(0xFF3B82F6), label = "Ads", mutedText = mutedText)
        LegendItem(color = Color(0xFF10B981), label = "Tracking", mutedText = mutedText)
        LegendItem(color = accent, label = "AI", mutedText = mutedText)
    }
}

@Composable
fun LegendItem(color: Color, label: String, mutedText: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).background(color, shape = CircleShape))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = mutedText)
    }
}

@Composable
fun BlocklistCard(
    blocklist: com.seikouv.pishield.data.Blocklist,
    isToggling: Boolean,
    onToggle: () -> Unit,
    cardBg: Color,
    accent: Color,
    accentDim: Color,
    mutedText: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(cardBg)
            .border(1.dp, accentDim, RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                blocklist.category.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleSmall,
                color = accent
            )
            Text("${blocklist.count} domains", style = MaterialTheme.typography.bodySmall, color = mutedText)
        }
        Switch(
            checked = blocklist.enabled,
            onCheckedChange = { onToggle() },
            enabled = false, // Disabled until backend permission issue is fixed
            modifier = Modifier.width(50.dp),
            colors = SwitchDefaults.colors(
                checkedThumbColor = accent,
                checkedTrackColor = accentDim,
                disabledCheckedThumbColor = accent.copy(alpha = 0.5f),
                disabledCheckedTrackColor = accentDim.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
private fun DomainListHeader(
    title: String,
    inputText: String,
    onInputChange: (String) -> Unit,
    onAdd: () -> Unit,
    accent: Color,
    isDark: Boolean
) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            color = accent,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("e.g. example.com", color = if (isDark) MutedText else LightColors.TextSecondary) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accent,
                    unfocusedBorderColor = if (isDark) DarkColors.AccentDim else LightColors.AccentDim,
                    focusedTextColor = if (isDark) Color.White else Color.Black,
                    unfocusedTextColor = if (isDark) Color.White else Color.Black
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(
                onClick = onAdd,
                colors = ButtonDefaults.textButtonColors(contentColor = accent)
            ) {
                Text("ADD", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun DomainRow(domain: String, onRemove: () -> Unit, cardBg: Color, accent: Color, accentDim: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(cardBg)
            .border(1.dp, accentDim, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(domain, color = accent, style = MaterialTheme.typography.bodyMedium)
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = DarkColors.AlertRed)
        }
    }
}

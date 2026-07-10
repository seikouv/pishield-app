package com.seikouv.pishield.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.seikouv.pishield.BlocklistViewModel
import com.seikouv.pishield.data.BlocklistHistoryPoint

@Composable
fun BlocklistScreen(
    modifier: Modifier = Modifier,
    viewModel: BlocklistViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Title
        Text(
            "Blocklists",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Error message
        if (state.error != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
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

        // Loading indicator (only for the initial blocklist categories fetch)
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        // Live line chart
        Text(
            "Live Blocking Activity",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        BlocklistLineChart(history = state.history)

        LegendRow()

        Spacer(modifier = Modifier.height(24.dp))

        if (state.blocklists.isNotEmpty()) {
            // Blocklist Items
            Text(
                "Active Blocklists",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            state.blocklists.forEach { blocklist ->
                BlocklistCard(
                    blocklist = blocklist,
                    isToggling = state.toggledCategory == blocklist.category,
                    onToggle = { viewModel.toggleBlocklistCategory(blocklist.category) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Toggle error
            if (state.toggleError != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF5F2C2C))
                ) {
                    Text(
                        state.toggleError ?: "",
                        modifier = Modifier.padding(12.dp),
                        color = Color(0xFFFFCDD2),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun BlocklistLineChart(history: List<BlocklistHistoryPoint>) {
    val adsEntries = history.mapIndexed { i, p -> FloatEntry(i.toFloat(), p.adsCount.toFloat()) }
    val trackingEntries = history.mapIndexed { i, p -> FloatEntry(i.toFloat(), p.trackingCount.toFloat()) }
    val aiEntries = history.mapIndexed { i, p -> FloatEntry(i.toFloat(), p.aiCount.toFloat()) }

    val chartEntryModelProducer = remember { ChartEntryModelProducer() }

    LaunchedEffect(history) {
        if (history.size >= 2) {
            chartEntryModelProducer.setEntries(adsEntries, trackingEntries, aiEntries)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937))
    ) {
        if (history.size < 2) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Waiting for data...",
                    color = Color(0xFF9CA3AF),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            ProvideChartStyle(chartStyle = m3ChartStyle()) {
                Chart(
                    chart = lineChart(
                        lines = listOf(
                            lineSpec(lineColor = Color(0xFF3B82F6)), // ads - blue
                            lineSpec(lineColor = Color(0xFF10B981)), // tracking - green
                            lineSpec(lineColor = Color(0xFFF59E0B))  // ai - amber
                        )
                    ),
                    chartModelProducer = chartEntryModelProducer,
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(
                        guideline = null,
                        valueFormatter = { _, _ -> "" } // timestamps aren't meaningful per-point, hide labels
                    ),
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
fun LegendRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LegendItem(color = Color(0xFF3B82F6), label = "Ads")
        LegendItem(color = Color(0xFF10B981), label = "Tracking")
        LegendItem(color = Color(0xFFF59E0B), label = "AI")
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, shape = androidx.compose.foundation.shape.CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF9CA3AF))
    }
}

@Composable
fun BlocklistCard(
    blocklist: com.seikouv.pishield.data.Blocklist,
    isToggling: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111827))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    blocklist.category.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
                Text(
                    "${blocklist.count} domains",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9CA3AF)
                )
            }

            // Toggle disabled due to permission issues on Pi
            Switch(
                checked = blocklist.enabled,
                onCheckedChange = { onToggle() },
                enabled = false, // Disabled until backend permission issue is fixed
                modifier = Modifier.width(50.dp)
            )
        }
    }
}
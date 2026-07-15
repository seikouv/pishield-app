package com.seikouv.pishield.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seikouv.pishield.ui.DashboardState
import com.seikouv.pishield.ui.DashboardViewModel
import com.seikouv.pishield.ui.components.PiShieldLoader
import com.seikouv.pishield.ui.theme.DarkColors
import com.seikouv.pishield.ui.theme.LightColors
import com.seikouv.pishield.ui.theme.ThemeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

private val CardBgDark = Color(0xFF14110A)
private val MutedTextDark = Color(0xFF9CA3AF)
private const val GITHUB_URL = "https://github.com/seikouv/pishield"
private const val SECRET_TAP_COUNT = 7
private const val SECRET_TAP_WINDOW_MS = 3000L

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    val isDark by ThemeState.isDark.collectAsState()
    val accentColor by ThemeState.accentColor.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Sync is explicit only
    var syncState by remember { mutableStateOf<DashboardState?>(null) }
    var isSyncing by remember { mutableStateOf(false) }

    // Secret game trigger
    var tapCount by remember { mutableIntStateOf(0) }
    var firstTapTime by remember { mutableLongStateOf(0L) }
    var showSecretGame by remember { mutableStateOf(false) }

    val bg = if (isDark) DarkColors.TerminalBg else LightColors.TerminalBg
    val amber = accentColor
    val amberDim = accentColor.copy(alpha = 0.6f)
    val cardBg = if (isDark) CardBgDark else LightColors.GlassFill
    val mutedText = if (isDark) MutedTextDark else LightColors.TextSecondary

    if (showSecretGame) {
        ReactionTapGame(onExit = { showSecretGame = false }, isDark = isDark, accentColor = accentColor)
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "SETTINGS",
            color = amber,
            fontFamily = FontFamily.Monospace,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // PiShield configuration info
        InfoCard(title = "PiShield Configuration", amber = amber, cardBg = cardBg, border = amberDim) {
            InfoRow("Pi IP", "192.168.4.1:8000", mutedText, amber)
            InfoRow("Hostname", "sike", mutedText, amber)
            InfoRow("SSH", "ssh yas@192.168.4.1", mutedText, amber)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "View on GitHub",
                color = amber,
                style = MaterialTheme.typography.bodySmall,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL)))
                }
            )
        }

        // Appearance toggle
        InfoCard(title = "Appearance", amber = amber, cardBg = cardBg, border = amberDim) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (isDark) "Dark mode" else "Light mode",
                    color = mutedText,
                    style = MaterialTheme.typography.bodySmall
                )
                Switch(
                    checked = isDark,
                    onCheckedChange = { ThemeState.setDark(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = amber,
                        checkedTrackColor = amberDim
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("UI Accent Color", color = mutedText, style = MaterialTheme.typography.labelSmall)

            Slider(
                value = remember(accentColor) {
                    val hsv = FloatArray(3)
                    android.graphics.Color.colorToHSV(
                        android.graphics.Color.argb(
                            (accentColor.alpha * 255).toInt(),
                            (accentColor.red * 255).toInt(),
                            (accentColor.green * 255).toInt(),
                            (accentColor.blue * 255).toInt()
                        ),
                        hsv
                    )
                    hsv[0] / 360f
                },
                onValueChange = { h ->
                    val color = Color.hsv(h * 360f, 1f, 1f)
                    ThemeState.setAccentColor(color)
                },
                colors = SliderDefaults.colors(
                    thumbColor = amber,
                    activeTrackColor = amberDim
                )
            )
        }

        // Manual sync - only hits the Pi when tapped
        InfoCard(title = "Raspberry Pi Health", amber = amber, cardBg = cardBg, border = amberDim) {
            when (val s = syncState) {
                null -> {
                    Text(
                        "Not synced yet",
                        color = mutedText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                is DashboardState.Success -> {
                    InfoRow("Memory Usage", "%.1f%%".format(s.data.memory_percent), mutedText, amber)
                    InfoRow("Active Connections", s.data.active_connections.toString(), mutedText, amber)
                    InfoRow("Uptime", formatUptime(s.data.uptime_seconds), mutedText, amber)
                    InfoRow("Threats (24h)", s.data.threat_count_24h.toString(), mutedText, amber)
                }
                is DashboardState.Error -> {
                    Text(
                        "Sync failed: ${s.message}",
                        color = DarkColors.AlertRed,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                is DashboardState.Loading -> {
                    PiShieldLoader(color = amber, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }

        Button(
            onClick = {
                isSyncing = true
                scope.launch {
                    dashboardViewModel.fetchDashboard()
                    syncState = dashboardViewModel.dashboardState.value
                    isSyncing = false
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = amber)
        ) {
            Text(if (isSyncing) "Syncing..." else "Sync with Pi", color = Color.Black)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer credit tag - tap it 7x within 3s to open the secret game
        Text(
            text = "Powered by LapMaster Game",
            color = amberDim,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .clickable {
                    val now = System.currentTimeMillis()
                    if (now - firstTapTime > SECRET_TAP_WINDOW_MS) {
                        firstTapTime = now
                        tapCount = 1
                    } else {
                        tapCount++
                    }
                    if (tapCount >= SECRET_TAP_COUNT) {
                        tapCount = 0
                        showSecretGame = true
                    }
                }
        )
    }
}

@Composable
private fun InfoCard(
    title: String,
    amber: Color,
    cardBg: Color,
    border: Color,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(cardBg)
            .border(1.dp, border, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = amber)
        content()
    }
}

@Composable
private fun InfoRow(label: String, value: String, mutedText: Color, amber: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = mutedText)
        Text(value, style = MaterialTheme.typography.bodySmall, color = amber)
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

@Composable
fun ReactionTapGame(onExit: () -> Unit, isDark: Boolean, accentColor: Color) {
    var gameState by remember { mutableStateOf("READY") } // READY, WAITING, TAP, RESULT
    var startTime by remember { mutableLongStateOf(0L) }
    var resultTime by remember { mutableLongStateOf(0L) }
    val scope = rememberCoroutineScope()

    val bgColor = if (gameState == "TAP") accentColor else (if (isDark) Color.Black else Color.White)
    val textColor = if (gameState == "TAP") Color.Black else (if (isDark) Color.White else Color.Black)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .clickable {
                when (gameState) {
                    "READY" -> {
                        gameState = "WAITING"
                        scope.launch {
                            delay(Random.nextLong(1000, 4000))
                            if (gameState == "WAITING") {
                                gameState = "TAP"
                                startTime = System.currentTimeMillis()
                            }
                        }
                    }
                    "WAITING" -> {
                        gameState = "TOO_EARLY"
                        scope.launch {
                            delay(1500)
                            gameState = "READY"
                        }
                    }
                    "TAP" -> {
                        resultTime = System.currentTimeMillis() - startTime
                        gameState = "RESULT"
                    }
                    "RESULT", "TOO_EARLY" -> {
                        gameState = "READY"
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = when (gameState) {
                    "READY" -> "REACTION TEST\n\nTap to start"
                    "WAITING" -> "Wait for ${accentColor.toHex()}..."
                    "TAP" -> "TAP NOW!"
                    "RESULT" -> "Time: ${resultTime}ms\n\nTap to retry"
                    "TOO_EARLY" -> "Too early!\n\nTap to restart"
                    else -> ""
                },
                color = textColor,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Monospace,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onExit,
                colors = ButtonDefaults.buttonColors(containerColor = if (gameState == "TAP") Color.Black else accentColor)
            ) {
                Text("EXIT GAME", color = if (gameState == "TAP") accentColor else (if (isDark) Color.Black else Color.White))
            }
        }
    }
}

fun Color.toHex(): String {
    return String.format("#%02X%02X%02X", (red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt())
}

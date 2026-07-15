package com.seikouv.pishield.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

/**
 * Shared live/offline connection indicator (dot + label), pulled from the
 * DNS Logs screen so every screen can show the same thing consistently.
 *
 * Usage:
 *   LiveIndicator(isConnected = isConnected)
 */
@Composable
fun LiveIndicator(
    isConnected: Boolean,
    isDark: Boolean = true,
    modifier: Modifier = Modifier
) {
    val accentColor by ThemeState.accentColor.collectAsState()
    val liveColor = accentColor
    val offlineColor = Color(0xFF8A0000)
    val dimColor = accentColor.copy(alpha = 0.6f)

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (isConnected) liveColor else offlineColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (isConnected) "LIVE" else "OFFLINE",
            color = if (isConnected) dimColor else offlineColor,
            fontFamily = FontFamily.Monospace,
            fontSize = TextSizes.label
        )
    }
}
package com.seikouv.pishield.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Unique terminal-style loading animation for PiShield.
 * Consists of 4 square blocks that pulse in a scanning pattern.
 */
@Composable
fun PiShieldLoader(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF4FA8E8)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "PiShieldLoader")
    
    val animationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ScanningProgress"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(4) { index ->
            // Logic to make blocks "pulse" in sequence based on progress
            val blockProgress = (animationProgress * 5f - index).coerceIn(0f, 1f)
            val alpha = if (blockProgress < 0.5f) {
                0.2f + (blockProgress * 1.6f) // Fade in
            } else {
                1.0f - ((blockProgress - 0.5f) * 1.6f) // Fade out
            }

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color.copy(alpha = alpha.coerceIn(0.2f, 1.0f)))
            )
        }
    }
}

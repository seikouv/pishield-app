package com.seikouv.pishield.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Pulsing glass placeholder shown while data loads.
 * Same shape/border language as GlassCard so it blends into the UI
 * instead of looking like a generic spinner.
 *
 * Usage:
 *   if (isLoading) {
 *       ShimmerGlass(modifier = Modifier.fillMaxWidth().height(80.dp))
 *   } else {
 *       GlassCard { ... real content ... }
 *   }
 */
@Composable
fun ShimmerGlass(
    isDark: Boolean = true,
    modifier: Modifier = Modifier,
    height: Dp = 80.dp
) {
    val fillColor = if (isDark) DarkColors.GlassFill else LightColors.GlassFill
    val borderColor = if (isDark) DarkColors.GlassBorder else LightColors.GlassBorder

    val transition = rememberInfiniteTransition(label = "shimmer")
    val pulseAlpha by transition.animateFloat(
        initialValue = GlassTokens.FillAlpha * 0.4f,
        targetValue = GlassTokens.FillAlpha * 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(GlassTokens.CornerRadius))
            .background(fillColor.copy(alpha = pulseAlpha))
            .border(
                width = GlassTokens.BorderWidth,
                color = borderColor.copy(alpha = GlassTokens.BorderAlpha * 0.7f),
                shape = RoundedCornerShape(GlassTokens.CornerRadius)
            )
    )
}
package com.seikouv.pishield.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.seikouv.pishield.ui.theme.DarkColors
import com.seikouv.pishield.ui.theme.LightColors
import com.seikouv.pishield.ui.theme.ThemeState

/**
 * A reusable shimmer component that renders a skeleton placeholder
 * with a terminal-blue aesthetic sweep animation.
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier
) {
    val isDark by ThemeState.isDark.collectAsState()
    val accentColor by ThemeState.accentColor.collectAsState()
    
    val baseColor = if (isDark) {
        DarkColors.AccentDim.copy(alpha = 0.15f)
    } else {
        LightColors.AccentDim.copy(alpha = 0.15f)
    }
    
    val highlightColor = accentColor.copy(alpha = 0.3f)
    
    val infiniteTransition = rememberInfiniteTransition(label = "ShimmerTransition")
    
    val translateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerTranslation"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            baseColor,
            highlightColor,
            baseColor,
        ),
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(brush)
    )
}

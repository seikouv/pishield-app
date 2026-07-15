package com.seikouv.pishield.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush

/**
 * Full-screen gradient background wrapper.
 * Wrap each screen's root content in this so GlassCards have a
 * gradient to show through instead of flat black/white.
 *
 * Usage:
 *   GradientBackground(isDark = true) {
 *       // screen content here
 *   }
 */
@Composable
fun GradientBackground(
    isDark: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val startColor = if (isDark) DarkColors.GradientStart else LightColors.GradientStart
    val endColor = if (isDark) DarkColors.GradientEnd else LightColors.GradientEnd

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(startColor, endColor)
                )
            )
    ) {
        content()
    }
}
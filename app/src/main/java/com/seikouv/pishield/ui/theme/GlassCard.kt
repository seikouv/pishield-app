package com.seikouv.pishield.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Reusable "glass" style card used across all PiShield screens.
 * Faux-glass: semi-transparent fill + subtle amber border + soft shadow.
 * No real backdrop blur (would need an extra library) — can upgrade later.
 *
 * @param isDark whether to use dark or light token set
 * @param modifier standard modifier, apply size/fillMaxWidth etc from caller
 * @param content card content
 */
@Composable
fun GlassCard(
    isDark: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val fillColor = if (isDark) DarkColors.GlassFill else LightColors.GlassFill
    val borderColor = if (isDark) DarkColors.GlassBorder else LightColors.GlassBorder
    val textColor = if (isDark) DarkColors.TextPrimary else LightColors.TextPrimary

    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(GlassTokens.CornerRadius),
                ambientColor = Color.Black.copy(alpha = GlassTokens.ShadowAlpha),
                spotColor = Color.Black.copy(alpha = GlassTokens.ShadowAlpha)
            )
            .clip(RoundedCornerShape(GlassTokens.CornerRadius))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        fillColor.copy(alpha = GlassTokens.FillAlpha),
                        fillColor.copy(alpha = GlassTokens.FillAlpha * 0.6f)
                    )
                )
            )
            .border(
                width = GlassTokens.BorderWidth,
                color = borderColor.copy(alpha = GlassTokens.BorderAlpha),
                shape = RoundedCornerShape(GlassTokens.CornerRadius)
            )
            .padding(Spacing.md)
    ) {
        androidx.compose.runtime.CompositionLocalProvider(
            LocalContentColor provides textColor
        ) {
            content()
        }
    }
}
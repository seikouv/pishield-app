package com.seikouv.pishield.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * PiShield Design Tokens
 * Single source of truth for colors, spacing, glass effect values, and typography.
 * Screens should reference these instead of hardcoding values.
 */

// ---------- DARK THEME (amber terminal, default) ----------
object DarkColors {
    val TerminalBg = Color(0xFF0A0A0A)
    val AmberBright = Color(0xFFFFB000)
    val AmberDim = Color(0xFFB37D00)
    val AlertRed = Color(0xFFFF4500)

    // Gradient background (subtle amber-tinted dark gradient)
    val GradientStart = Color(0xFF0A0A0A)
    val GradientEnd = Color(0xFF1A1206)

    // Glass card tint + border
    val GlassFill = Color(0xFF1A1A1A) // used with alpha below
    val GlassBorder = Color(0xFFFFB000) // used with low alpha for subtle edge

    val TextPrimary = Color(0xFFFFB000)
    val TextSecondary = Color(0xFF8A6A2E)
}

// ---------- LIGHT THEME (placeholder, refine later) ----------
object LightColors {
    val TerminalBg = Color(0xFFF5F0E6)
    val AmberBright = Color(0xFFB37200)
    val AmberDim = Color(0xFFD9A441)
    val AlertRed = Color(0xFFD1360F)

    val GradientStart = Color(0xFFFDF8EE)
    val GradientEnd = Color(0xFFEFE2C4)

    val GlassFill = Color(0xFFFFFFFF)
    val GlassBorder = Color(0xFFB37200)

    val TextPrimary = Color(0xFF3A2A00)
    val TextSecondary = Color(0xFF7A6440)
}

// ---------- GLASS EFFECT VALUES ----------
object GlassTokens {
    val CornerRadius = 16.dp
    val BorderWidth = 1.dp
    val BlurRadius = 20.dp

    const val FillAlpha = 0.35f      // "little bit transparency"
    const val BorderAlpha = 0.25f
    const val ShadowAlpha = 0.15f
}

// ---------- SPACING ----------
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
}

// ---------- TYPOGRAPHY SIZES (terminal-ish monospace feel) ----------
object TextSizes {
    val label = 12.sp
    val body = 14.sp
    val title = 18.sp
    val header = 24.sp
}
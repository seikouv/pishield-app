package com.seikouv.pishield.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * PiShield Design Tokens
 * Single source of truth for colors, spacing, and typography.
 * Screens should reference these instead of hardcoding values.
 */

// ---------- DARK THEME (blue terminal, default) ----------
object DarkColors {
    val TerminalBg = Color(0xFF060D1A)
    val AccentBright = Color(0xFF38BDF8)
    val AccentDim = Color(0xFF2E6FA3)
    val AlertRed = Color(0xFFFF4500)

    // Gradient background (subtle blue-tinted dark gradient)
    val GradientStart = Color(0xFF060D1A)
    val GradientEnd = Color(0xFF0F1E3D)

    val TextPrimary = Color(0xFF38BDF8)
    val TextSecondary = Color(0xFF6B8CAE)
}

// ---------- LIGHT THEME ----------
object LightColors {
    val TerminalBg = Color(0xFFEAF2FB)
    val AccentBright = Color(0xFF1B5FA8)
    val AccentDim = Color(0xFF5B8FC7)
    val AlertRed = Color(0xFFD1360F)

    val GradientStart = Color(0xFFEAF2FB)
    val GradientEnd = Color(0xFFD1E3F6)

    val GlassFill = Color(0xFFFFFFFF)

    val TextPrimary = Color(0xFF12335C)
    val TextSecondary = Color(0xFF4A6B8C)
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
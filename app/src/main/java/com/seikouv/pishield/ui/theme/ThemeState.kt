package com.seikouv.pishield.ui.theme

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ThemeState {
    private val _isDark = MutableStateFlow(true)
    val isDark: StateFlow<Boolean> = _isDark

    private val _accentColor = MutableStateFlow(Color(0xFFFFB000)) // Default Amber
    val accentColor: StateFlow<Color> = _accentColor

    fun toggle() {
        _isDark.value = !_isDark.value
    }

    fun setDark(value: Boolean) {
        _isDark.value = value
    }

    fun setAccentColor(color: Color) {
        _accentColor.value = color
    }
}

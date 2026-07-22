package com.seikouv.pishield.ui.theme

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ThemeState {
    private val _isDark = MutableStateFlow(true)
    val isDark: StateFlow<Boolean> = _isDark

    private val _accentColor = MutableStateFlow(DarkColors.AccentBright)
    val accentColor: StateFlow<Color> = _accentColor

    fun setDark(value: Boolean) {
        if (_isDark.value != value) {
            _isDark.value = value
            _accentColor.value = if (value) DarkColors.AccentBright else LightColors.AccentBright
        }
    }
}
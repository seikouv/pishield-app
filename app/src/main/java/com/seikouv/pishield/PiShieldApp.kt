package com.seikouv.pishield

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.seikouv.pishield.ui.screens.BlocklistScreen
import com.seikouv.pishield.ui.screens.DashboardScreen
import com.seikouv.pishield.ui.screens.DeviceScreen
import com.seikouv.pishield.ui.screens.DnsLogScreen
import com.seikouv.pishield.ui.screens.SettingsScreen
import com.seikouv.pishield.ui.screens.ThreatsScreen
import com.seikouv.pishield.ui.theme.DarkColors
import com.seikouv.pishield.ui.theme.LightColors
import com.seikouv.pishield.ui.theme.ThemeState

private data class NavItem(val icon: ImageVector, val label: String)

private val navItems = listOf(
    NavItem(Icons.Default.Dashboard, "Dashboard"),
    NavItem(Icons.Default.Security, "Blocklists"),
    NavItem(Icons.Default.Terminal, "DNS Logs"),
    NavItem(Icons.Default.Warning, "Threats"),
    NavItem(Icons.Default.Devices, "Devices"),
    NavItem(Icons.Default.Settings, "Settings")
)

/**
 * Root app composable. Simple centered icon-only bottom bar, new screen order:
 * Dashboard -> Blocklists -> DNS Logs -> Threats -> Devices -> Settings
 */
@Composable
fun PiShieldApp() {
    val selectedTab = remember { mutableIntStateOf(0) }
    val isDark by ThemeState.isDark.collectAsState()
    val accentColor by ThemeState.accentColor.collectAsState()

    val navBg = if (isDark) DarkColors.TerminalBg else LightColors.TerminalBg
    val selectedTint = accentColor
    val unselectedTint = accentColor.copy(alpha = 0.4f)

    Scaffold(
        bottomBar = {
            Surface(color = navBg) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    navItems.forEachIndexed { index, item ->
                        val isSelected = selectedTab.intValue == index
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) selectedTint else unselectedTint,
                            modifier = Modifier
                                .size(if (isSelected) 26.dp else 22.dp)
                                .clickable { selectedTab.intValue = index }
                        )
                    }
                }
            }
        }
    ) { padding ->
        when (selectedTab.intValue) {
            0 -> DashboardScreen(modifier = Modifier.padding(padding))
            1 -> BlocklistScreen(modifier = Modifier.padding(padding))
            2 -> DnsLogScreen(modifier = Modifier.padding(padding))
            3 -> ThreatsScreen(modifier = Modifier.padding(padding))
            4 -> DeviceScreen(modifier = Modifier.padding(padding))
            5 -> SettingsScreen(modifier = Modifier.padding(padding))
        }
    }
}
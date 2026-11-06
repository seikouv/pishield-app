package com.seikouv.pishield

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    NavItem(Icons.Default.Dashboard, "Home"),
    NavItem(Icons.Default.Security, "Block"),
    NavItem(Icons.Default.Terminal, "Logs"),
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
    val systemIsDark = isSystemInDarkTheme()
    
    LaunchedEffect(systemIsDark) {
        ThemeState.setDark(systemIsDark)
    }

    val selectedTab = remember { mutableIntStateOf(0) }
    val isDark by ThemeState.isDark.collectAsState()
    val accentColor by ThemeState.accentColor.collectAsState()

    val navBg = if (isDark) DarkColors.TerminalBg else LightColors.TerminalBg
    val borderColor = accentColor.copy(alpha = 0.15f)

    Scaffold(
        bottomBar = {
            Surface(
                color = navBg,
                modifier = Modifier
                    .drawBehind {
                        drawLine(
                            color = borderColor,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    navItems.forEachIndexed { index, item ->
                        val isSelected = selectedTab.intValue == index
                        val contentColor = if (isSelected) accentColor else accentColor.copy(alpha = 0.4f)
                        
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab.intValue = index },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 48.dp, height = 30.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) accentColor.copy(alpha = 0.15f) else Color.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = contentColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.label,
                                color = contentColor,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        val currentTab = selectedTab.intValue
        
        // Polling screens stay in composition to manage their isActive state in ViewModels
        DashboardScreen(isActive = currentTab == 0, modifier = Modifier.padding(padding))
        BlocklistScreen(isActive = currentTab == 1, modifier = Modifier.padding(padding))
        DnsLogScreen(isActive = currentTab == 2, modifier = Modifier.padding(padding))

        when (currentTab) {
            3 -> ThreatsScreen(modifier = Modifier.padding(padding))
            4 -> DeviceScreen(modifier = Modifier.padding(padding))
            5 -> SettingsScreen(modifier = Modifier.padding(padding))
        }
    }
}

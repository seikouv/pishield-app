package com.seikouv.pishield
import com.seikouv.pishield.ui.screens.DnsLogScreen
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.seikouv.pishield.ui.screens.DashboardScreen
import com.seikouv.pishield.ui.screens.DeviceScreen
import com.seikouv.pishield.ui.screens.BlocklistScreen
import com.seikouv.pishield.ui.screens.SettingsScreen

@Composable
fun PiShieldApp() {
    val selectedTab = remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                    label = { Text("Dashboard") },
                    selected = selectedTab.intValue == 0,
                    onClick = { selectedTab.intValue = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Devices, contentDescription = null) },
                    label = { Text("Devices") },
                    selected = selectedTab.intValue == 1,
                    onClick = { selectedTab.intValue = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Security, contentDescription = null) },
                    label = { Text("Blocklists") },
                    selected = selectedTab.intValue == 2,
                    onClick = { selectedTab.intValue = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") },
                    selected = selectedTab.intValue == 3,
                    onClick = { selectedTab.intValue = 3 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Terminal, contentDescription = null) },
                    label = { Text("DNS Logs") },
                    selected = selectedTab.intValue == 4,
                    onClick = { selectedTab.intValue = 4 }
                )
            }
        }
    ) { padding ->
        when (selectedTab.intValue) {
            0 -> DashboardScreen(modifier = Modifier.padding(padding))
            1 -> DeviceScreen(modifier = Modifier.padding(padding))
            2 -> BlocklistScreen(modifier = Modifier.padding(padding))
            3 -> SettingsScreen(modifier = Modifier.padding(padding))
            4 -> DnsLogScreen(modifier = Modifier.padding(padding))
        }
    }
}
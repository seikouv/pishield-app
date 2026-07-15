package com.seikouv.pishield.ui.nav

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.seikouv.pishield.ui.theme.DarkColors
import com.seikouv.pishield.ui.theme.GlassTokens
import com.seikouv.pishield.ui.theme.Spacing
import kotlinx.coroutines.launch

/**
 * Definition of each screen in the pager: icon + label + composable content.
 * Order matches the requested sequence:
 * Dashboard -> Blocklists -> DNS Logs -> Threats -> Devices -> Settings
 */
data class PishieldScreen(
    val label: String,
    val icon: ImageVector,
    val content: @Composable () -> Unit
)

/**
 * Root swipeable shell. Wraps all screens in a HorizontalPager and shows
 * an icon-only bottom indicator strip. The current page's icon "lands"
 * as a minimized glass pill; other icons stay small/dim.
 *
 * Usage:
 *   PishieldPagerShell(
 *       screens = listOf(
 *           PishieldScreen("Dashboard", Icons.Default.Dashboard) { DashboardScreen() },
 *           PishieldScreen("Blocklists", Icons.Default.Shield) { BlocklistScreen() },
 *           PishieldScreen("DNS Logs", Icons.Default.Terminal) { DnsLogsScreen() },
 *           PishieldScreen("Threats", Icons.Default.Warning) { ThreatsScreen() },
 *           PishieldScreen("Devices", Icons.Default.Devices) { DevicesScreen() },
 *           PishieldScreen("Settings", Icons.Default.Settings) { SettingsScreen() }
 *       )
 *   )
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PishieldPagerShell(
    screens: List<PishieldScreen>,
    isDark: Boolean = true
) {
    val pagerState = rememberPagerState(pageCount = { screens.size })
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            screens[page].content()
        }

        PagerBottomIndicator(
            screens = screens,
            currentPage = pagerState.currentPage,
            isDark = isDark,
            onIconClick = { index ->
                scope.launch {
                    pagerState.animateScrollToPage(index)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = Spacing.lg)
        )
    }
}

@Composable
private fun PagerBottomIndicator(
    screens: List<PishieldScreen>,
    currentPage: Int,
    isDark: Boolean,
    onIconClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val fillColor = if (isDark) DarkColors.GlassFill else DarkColors.GlassFill
    val borderColor = if (isDark) DarkColors.GlassBorder else DarkColors.GlassBorder

    Row(
        modifier = modifier
            .background(
                color = fillColor.copy(alpha = GlassTokens.FillAlpha + 0.15f),
                shape = RoundedCornerShape(50)
            )
            .border(
                width = GlassTokens.BorderWidth,
                color = borderColor.copy(alpha = GlassTokens.BorderAlpha),
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        screens.forEachIndexed { index, screen ->
            PagerIcon(
                screen = screen,
                isActive = index == currentPage,
                onClick = { onIconClick(index) }
            )
        }
    }
}

@Composable
private fun RowScope.PagerIcon(
    screen: PishieldScreen,
    isActive: Boolean,
    onClick: () -> Unit
) {
    // Minimize-on-land: active icon sits inside a small glass pill and is
    // slightly larger + brighter; inactive icons are dim and smaller.
    val iconSize by animateDpAsState(
        targetValue = if (isActive) 26.dp else 20.dp,
        animationSpec = tween(durationMillis = 250),
        label = "iconSize"
    )
    val iconAlpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.5f,
        animationSpec = tween(durationMillis = 250),
        label = "iconAlpha"
    )
    val pillPadding by animateDpAsState(
        targetValue = if (isActive) 8.dp else 0.dp,
        animationSpec = tween(durationMillis = 250),
        label = "pillPadding"
    )

    Box(
        modifier = Modifier
            .background(
                color = if (isActive) {
                    DarkColors.AmberBright.copy(alpha = GlassTokens.FillAlpha)
                } else {
                    androidx.compose.ui.graphics.Color.Transparent
                },
                shape = RoundedCornerShape(50)
            )
            .padding(pillPadding),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = screen.icon,
            contentDescription = screen.label,
            tint = DarkColors.AmberBright.copy(alpha = iconAlpha),
            modifier = Modifier
                .size(iconSize)
                .clickable(onClick = onClick)
        )
    }
}
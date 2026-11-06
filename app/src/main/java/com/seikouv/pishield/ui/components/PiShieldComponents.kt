package com.seikouv.pishield.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Custom terminal-styled switch with squared-off track and thumb.
 */
@Composable
fun PiShieldSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    accentColor: Color
) {
    val trackColor = if (checked) accentColor.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.3f)
    val thumbColor = if (checked) accentColor else Color.Gray
    val opacity = if (enabled) 1f else 0.5f

    Box(
        modifier = modifier
            .size(width = 44.dp, height = 22.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(trackColor.copy(alpha = trackColor.alpha * opacity))
            .clickable(enabled = enabled) { onCheckedChange(!checked) },
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .size(14.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(thumbColor.copy(alpha = thumbColor.alpha * opacity))
        )
    }
}

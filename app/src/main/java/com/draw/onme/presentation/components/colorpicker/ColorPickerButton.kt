package com.draw.onme.presentation.components.colorpicker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Cheerful, rainbow-accented button to open the Universal Color Picker.
 */
@Composable
fun ColorPickerButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 30.dp,
    iconSize: Dp = 18.dp
) {
    val rainbowColors = listOf(
        Color(0xFFFF5252), // Red
        Color(0xFFFFB74D), // Orange
        Color(0xFFFFEE58), // Yellow
        Color(0xFF66BB6A), // Green
        Color(0xFF29B6F6), // Cyan
        Color(0xFF7E57C2), // Purple
        Color(0xFFFF4081)  // Pink
    )

    val rainbowBrush = Brush.sweepGradient(rainbowColors)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .border(
                width = 2.5.dp,
                brush = rainbowBrush,
                shape = CircleShape
            )
            .clickable(
                onClick = onClick,
                role = Role.Button
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Palette,
            contentDescription = "Open Color Picker",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(iconSize)
        )
    }
}

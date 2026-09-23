package com.draw.onme.presentation.scribble.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.draw.onme.presentation.scribble.model.CanvasTransformState
import kotlin.math.roundToInt

/**
 * Excalidraw-style floating zoom & pan HUD overlay.
 * Displays zoom percentage, zoom in/out triggers, and tap-to-reset.
 */
@Composable
fun ZoomControlsHud(
    transformState: CanvasTransformState,
    modifier: Modifier = Modifier
) {
    val zoomPercent = (transformState.zoom * 100f).roundToInt()
    val isDefaultView = transformState.zoom == 1.0f && transformState.pan.getDistanceSquared() < 1f

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
        tonalElevation = 4.dp,
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Zoom Out (-) button
            IconButton(
                onClick = { transformState.zoomOut() },
                modifier = Modifier.size(32.dp),
                enabled = transformState.zoom > CanvasTransformState.MIN_ZOOM
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Zoom Out",
                    modifier = Modifier.size(18.dp)
                )
            }

            // Zoom Percentage / Reset Chip
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { transformState.reset() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$zoomPercent%",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!isDefaultView) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "Reset Canvas View",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Zoom In (+) button
            IconButton(
                onClick = { transformState.zoomIn() },
                modifier = Modifier.size(32.dp),
                enabled = transformState.zoom < CanvasTransformState.MAX_ZOOM
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Zoom In",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

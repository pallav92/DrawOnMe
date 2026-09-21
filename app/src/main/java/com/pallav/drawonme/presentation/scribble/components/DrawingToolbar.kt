package com.pallav.drawonme.presentation.scribble.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pallav.drawonme.domain.model.DrawingTool
import com.pallav.drawonme.domain.model.StrokeColor
import com.pallav.drawonme.presentation.scribble.ScribbleAction
import com.pallav.drawonme.presentation.scribble.ScribbleUiState
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.roundToInt

/**
 * Drawing toolbar providing tool selection (Pen / Eraser / Pan), color swatches,
 * stroke width adjustments, undo/redo triggers, and guaranteed-visible Clear button.
 */
@Composable
fun DrawingToolbar(
    uiState: ScribbleUiState,
    onAction: (ScribbleAction) -> Unit,
    modifier: Modifier = Modifier,
    maxEraserSize: Float = 360f
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top row: Tools on left, Undo/Redo & Clear on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Sleek, compact tool toggles
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ToolTab(
                        selected = uiState.selectedTool == DrawingTool.PEN,
                        icon = Icons.Default.Edit,
                        label = "Pen",
                        onClick = { onAction(ScribbleAction.SelectTool(DrawingTool.PEN)) }
                    )

                    ToolTab(
                        selected = uiState.selectedTool == DrawingTool.ERASER,
                        icon = Icons.Default.AutoFixNormal,
                        label = "Eraser",
                        onClick = { onAction(ScribbleAction.SelectTool(DrawingTool.ERASER)) }
                    )

                    ToolTab(
                        selected = uiState.selectedTool == DrawingTool.HAND,
                        icon = Icons.Default.PanTool,
                        label = "Pan",
                        onClick = { onAction(ScribbleAction.SelectTool(DrawingTool.HAND)) }
                    )
                }

                // History (Undo / Redo) and Clear actions
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = { onAction(ScribbleAction.Undo) },
                        enabled = uiState.canUndo,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { onAction(ScribbleAction.Redo) },
                        enabled = uiState.canRedo,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Redo,
                            contentDescription = "Redo",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    VerticalDivider(
                        modifier = Modifier
                            .height(20.dp)
                            .padding(horizontal = 2.dp)
                    )

                    // Prominent, unmistakable Clear button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (uiState.strokes.isNotEmpty()) {
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f)
                        } else {
                            Color.Transparent
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                enabled = uiState.strokes.isNotEmpty() || uiState.currentStroke != null,
                                onClick = { onAction(ScribbleAction.RequestClearCanvas) }
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear All Canvas",
                                tint = if (uiState.strokes.isNotEmpty()) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                },
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Clear",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                color = if (uiState.strokes.isNotEmpty()) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                }
                            )
                        }
                    }
                }
            }

            // Bottom row: Colors / Eraser sizing / Mode details
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                when (uiState.selectedTool) {
                    DrawingTool.PEN -> {
                        // Pen color swatches
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (color in uiState.availableColors) {
                                val isSelected = color == uiState.selectedColor
                                val composeColor = Color(color.argb)

                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(composeColor)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.outlineVariant
                                            },
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            onAction(ScribbleAction.SelectColor(color))
                                        }
                                )
                            }
                        }

                        // Pen stroke width selector
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (width in uiState.availableStrokeWidths) {
                                val isSelected = abs(uiState.strokeWidth - width) < 0.5f

                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) {
                                                MaterialTheme.colorScheme.primaryContainer
                                            } else {
                                                Color.Transparent
                                            }
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.outlineVariant
                                            },
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            onAction(ScribbleAction.SetStrokeWidth(width))
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size((width.coerceIn(4f, 16f)).dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                }
                                            )
                                    )
                                }
                            }
                        }
                    }

                    DrawingTool.ERASER -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val effectiveMax = maxOf(maxEraserSize, 45f)
                            val currentWidth = uiState.strokeWidth.coerceIn(40f, effectiveMax)
                            val sliderSteps = maxOf(0, (((effectiveMax - 40f) / 5f).roundToInt() - 1))

                            Icon(
                                imageVector = Icons.Default.AutoFixNormal,
                                contentDescription = "Eraser size",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )

                            // Small size indicator
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                            )

                            // Slider for dynamic eraser size (minimum 40px up to 1/3rd viewport, 5px steps)
                            Slider(
                                value = currentWidth,
                                onValueChange = { newVal ->
                                    val stepped = (round(newVal / 5f) * 5f).coerceIn(40f, effectiveMax)
                                    onAction(ScribbleAction.SetStrokeWidth(stepped))
                                },
                                valueRange = 40f..effectiveMax,
                                steps = sliderSteps,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                            )

                            // Large size indicator
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                            )
                        }
                    }

                    DrawingTool.HAND -> {
                        Text(
                            text = "✋ Drag to pan canvas • Pinch to zoom",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolTab(
    selected: Boolean,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        selected = selected,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp
                )
            )
        }
    }
}

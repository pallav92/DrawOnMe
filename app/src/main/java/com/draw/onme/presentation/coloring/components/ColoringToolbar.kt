package com.draw.onme.presentation.coloring.components

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.draw.onme.domain.model.ColoringTool
import com.draw.onme.presentation.coloring.ColoringAction
import com.draw.onme.presentation.coloring.ColoringUiState
import com.draw.onme.presentation.components.colorpicker.ColorPickerButton
import com.draw.onme.presentation.components.colorpicker.UniversalColorPickerDialog
import kotlin.math.abs

/**
 * Bottom floating toolbar for Coloring Book Studio:
 * - Switchover button between 🪣 Bucket (Tap-to-Fill) and 🖍️ Magic Crayon (Stay-in-the-Lines).
 * - Compact stroke width selector dots (active in Crayon mode).
 * - Quick color swatches + Universal Color Picker.
 * - Undo, Redo, and Delete icon button (no text labels).
 */
@Composable
fun ColoringToolbar(
    uiState: ColoringUiState,
    onAction: (ColoringAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    var showColorPicker by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (isLandscape) 16.dp else 12.dp,
                vertical = if (isLandscape) 4.dp else 8.dp
            ),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top row: Tool Switchover Button, Stroke Widths, and Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Tool Switchover button for templates, or dedicated Crayon badge for user creations
                if (uiState.supportsBucketFill) {
                    ColoringToolSwitcher(
                        selectedTool = uiState.selectedTool,
                        onSelectTool = { tool -> onAction(ColoringAction.SelectTool(tool)) }
                    )
                } else {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.height(36.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Draw,
                                contentDescription = "Crayon Tool",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // If in Crayon mode or on user creation: stroke width dots
                if (uiState.selectedTool == ColoringTool.MAGIC_CRAYON || !uiState.supportsBucketFill) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (width in uiState.availableStrokeWidths) {
                            val isSelected = abs(uiState.strokeWidth - width) < 1f
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                        shape = CircleShape
                                    )
                                    .clickable { onAction(ColoringAction.SetStrokeWidth(width)) },
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size((width.coerceIn(6f, 16f)).dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                )
                            }
                        }
                    }
                }

                // Action buttons: Undo, Redo, Delete Icon (no text)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { onAction(ColoringAction.Undo) },
                        enabled = uiState.canUndo,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            tint = if (uiState.canUndo) MaterialTheme.colorScheme.onSurface
                                   else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { onAction(ColoringAction.Redo) },
                        enabled = uiState.canRedo,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Redo,
                            contentDescription = "Redo",
                            tint = if (uiState.canRedo) MaterialTheme.colorScheme.onSurface
                                   else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    VerticalDivider(
                        modifier = Modifier
                            .height(20.dp)
                            .padding(horizontal = 2.dp)
                    )

                    val hasModifications = uiState.fills.isNotEmpty() || uiState.customFills.isNotEmpty() || uiState.strokes.isNotEmpty()
                    IconButton(
                        onClick = { onAction(ColoringAction.RequestClear) },
                        enabled = hasModifications,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                if (hasModifications) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f)
                                else Color.Transparent
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Clear Canvas",
                            tint = if (hasModifications) MaterialTheme.colorScheme.error
                                   else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Bottom row: Lightweight Quick Color Swatches + Universal Color Picker
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (color in uiState.availableColors) {
                    val isSelected = color.argb == uiState.selectedColor.argb
                    val composeColor = Color(color.argb)

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(composeColor)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                shape = CircleShape
                            )
                            .clickable {
                                onAction(ColoringAction.SelectColor(color))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            val lum = 0.299f * composeColor.red + 0.587f * composeColor.green + 0.114f * composeColor.blue
                            val dotColor = if (lum > 0.65f) Color(0xFF212121) else Color.White
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(dotColor)
                            )
                        }
                    }
                }

                // Dedicated Color Picker button
                ColorPickerButton(
                    onClick = { showColorPicker = true },
                    size = 30.dp,
                    iconSize = 16.dp
                )
            }
        }
    }

    if (showColorPicker) {
        UniversalColorPickerDialog(
            initialColor = uiState.selectedColor,
            onColorSelected = { color ->
                onAction(ColoringAction.SelectColor(color))
            },
            onDismiss = { showColorPicker = false }
        )
    }
}

/**
 * Switchover button allowing immediate toggling between Bucket (Fill) and Crayon (Drawing).
 * Clean, icon-only design language without cluttered text.
 */
@Composable
private fun ColoringToolSwitcher(
    selectedTool: ColoringTool,
    onSelectTool: (ColoringTool) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColoringToolIconButton(
                selected = selectedTool == ColoringTool.BUCKET,
                icon = Icons.Default.FormatColorFill,
                contentDescription = "Bucket Fill Mode",
                onClick = { onSelectTool(ColoringTool.BUCKET) }
            )
            ColoringToolIconButton(
                selected = selectedTool == ColoringTool.MAGIC_CRAYON,
                icon = Icons.Default.Draw,
                contentDescription = "Magic Crayon Mode",
                onClick = { onSelectTool(ColoringTool.MAGIC_CRAYON) }
            )
        }
    }
}

@Composable
private fun ColoringToolIconButton(
    selected: Boolean,
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        label = "ToolBackground"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "ToolContentColor"
    )

    Surface(
        selected = selected,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        contentColor = contentColor,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clickable(
                    onClick = onClick,
                    role = Role.Button
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

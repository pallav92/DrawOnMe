package com.pallav.drawonme.presentation.scribble.components

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pallav.drawonme.domain.model.DrawingTool
import com.pallav.drawonme.presentation.scribble.ScribbleAction
import com.pallav.drawonme.presentation.scribble.ScribbleUiState
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.roundToInt

/**
 * Collapsible drawing toolbar that rolls back into a compact FAB.
 * When closed: the FAB displays a tools icon (Palette) indicating that tapping it reveals the entire tool set.
 * When open: the FAB displays a collapse arrow (KeyboardArrowDown) indicating that tapping it rolls the tools back.
 */
@Composable
fun CollapsibleDrawingToolbar(
    uiState: ScribbleUiState,
    onAction: (ScribbleAction) -> Unit,
    modifier: Modifier = Modifier,
    maxEraserSize: Float = 360f,
    initiallyExpanded: Boolean = true
) {
    var isExpanded by rememberSaveable { mutableStateOf(initiallyExpanded) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        // Landscape: Toolbar and FAB sit side-by-side in a single row; toolbar rolls horizontally into/out of the FAB
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            AnimatedVisibility(
                visible = isExpanded,
                enter = slideInHorizontally(initialOffsetX = { it }) + expandHorizontally(expandFrom = Alignment.End) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + shrinkHorizontally(shrinkTowards = Alignment.End) + fadeOut(),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                DrawingToolbar(
                    uiState = uiState,
                    onAction = onAction,
                    maxEraserSize = maxEraserSize
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // The FAB: Shows Palette icon when closed, KeyboardArrowDown when open
            FloatingActionButton(
                onClick = { isExpanded = !isExpanded },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp, pressedElevation = 2.dp),
                modifier = Modifier.size(52.dp)
            ) {
                AnimatedContent(
                    targetState = isExpanded,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(220, delayMillis = 60)) +
                            scaleIn(initialScale = 0.75f, animationSpec = tween(220, delayMillis = 60)))
                            .togetherWith(fadeOut(animationSpec = tween(90)) + scaleOut(targetScale = 0.75f, animationSpec = tween(90)))
                    },
                    label = "FabLandscapeIcon"
                ) { expanded ->
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.Palette,
                        contentDescription = if (expanded) "Hide Tools" else "Show Tools",
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    } else {
        // Portrait: Full width 2-row toolbar rolls down/up into the bottom-right FAB
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalAlignment = Alignment.End
        ) {
            // FAB anchored at top-right of toolbar when open, or resting at bottom-right when closed
            FloatingActionButton(
                onClick = { isExpanded = !isExpanded },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp, pressedElevation = 2.dp),
                modifier = Modifier
                    .padding(end = 16.dp, bottom = if (isExpanded) 6.dp else 4.dp)
                    .size(52.dp)
            ) {
                AnimatedContent(
                    targetState = isExpanded,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(220, delayMillis = 60)) +
                            scaleIn(initialScale = 0.75f, animationSpec = tween(220, delayMillis = 60)))
                            .togetherWith(fadeOut(animationSpec = tween(90)) + scaleOut(targetScale = 0.75f, animationSpec = tween(90)))
                    },
                    label = "FabPortraitIcon"
                ) { expanded ->
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.Palette,
                        contentDescription = if (expanded) "Hide Tools" else "Show Tools",
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = slideInVertically(initialOffsetY = { it }) + expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
            ) {
                DrawingToolbar(
                    uiState = uiState,
                    onAction = onAction,
                    maxEraserSize = maxEraserSize
                )
            }
        }
    }
}

/**
 * Drawing toolbar providing tool selection (Pen / Eraser / Pan), color swatches,
 * stroke width adjustments, undo/redo triggers, and guaranteed-visible Clear button.
 * Automatically adapts between a 2-row portrait layout and a sleek 1-row landscape layout.
 */
@Composable
fun DrawingToolbar(
    uiState: ScribbleUiState,
    onAction: (ScribbleAction) -> Unit,
    modifier: Modifier = Modifier,
    maxEraserSize: Float = 360f
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (isLandscape) 16.dp else 12.dp,
                vertical = if (isLandscape) 4.dp else 6.dp
            ),
        shape = RoundedCornerShape(if (isLandscape) 20.dp else 24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        if (isLandscape) {
            // Sleek single-row landscape layout (~50dp) to maximize drawing area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ToolToggleGroup(
                    selectedTool = uiState.selectedTool,
                    onAction = onAction
                )

                VerticalDivider(
                    modifier = Modifier
                        .height(28.dp)
                        .padding(horizontal = 8.dp)
                )

                Box(
                    modifier = Modifier.weight(1f, fill = false),
                    contentAlignment = Alignment.Center
                ) {
                    when (uiState.selectedTool) {
                        DrawingTool.PEN, DrawingTool.HAND -> PenControls(uiState = uiState, onAction = onAction)
                        DrawingTool.ERASER -> EraserControls(
                            uiState = uiState,
                            maxEraserSize = maxEraserSize,
                            onAction = onAction,
                            modifier = Modifier.widthIn(max = 340.dp)
                        )
                    }
                }

                VerticalDivider(
                    modifier = Modifier
                        .height(28.dp)
                        .padding(horizontal = 8.dp)
                )

                ToolbarActionGroup(
                    uiState = uiState,
                    onAction = onAction
                )
            }
        } else {
            // Traditional 2-row portrait layout
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
                    ToolToggleGroup(
                        selectedTool = uiState.selectedTool,
                        onAction = onAction
                    )

                    ToolbarActionGroup(
                        uiState = uiState,
                        onAction = onAction
                    )
                }

                // Bottom row: Colors / Eraser sizing
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    when (uiState.selectedTool) {
                        DrawingTool.PEN, DrawingTool.HAND -> PenControls(
                            uiState = uiState,
                            onAction = onAction,
                            modifier = Modifier.fillMaxWidth()
                        )
                        DrawingTool.ERASER -> EraserControls(
                            uiState = uiState,
                            maxEraserSize = maxEraserSize,
                            onAction = onAction,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolToggleGroup(
    selectedTool: DrawingTool,
    onAction: (ScribbleAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ToolTab(
            selected = selectedTool == DrawingTool.PEN || selectedTool == DrawingTool.HAND,
            icon = Icons.Default.Edit,
            label = "Pen",
            onClick = { onAction(ScribbleAction.SelectTool(DrawingTool.PEN)) }
        )

        ToolTab(
            selected = selectedTool == DrawingTool.ERASER,
            icon = Icons.Default.AutoFixNormal,
            label = "Eraser",
            onClick = { onAction(ScribbleAction.SelectTool(DrawingTool.ERASER)) }
        )
    }
}

@Composable
private fun ToolbarActionGroup(
    uiState: ScribbleUiState,
    onAction: (ScribbleAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
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

@Composable
private fun PenControls(
    uiState: ScribbleUiState,
    onAction: (ScribbleAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Color swatches
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
            modifier = Modifier.padding(start = 12.dp),
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
}

@Composable
private fun EraserControls(
    uiState: ScribbleUiState,
    maxEraserSize: Float,
    onAction: (ScribbleAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
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

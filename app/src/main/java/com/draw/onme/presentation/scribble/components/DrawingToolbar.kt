package com.draw.onme.presentation.scribble.components

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
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Velocity
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.draw.onme.domain.model.DrawingTool
import com.draw.onme.presentation.scribble.ScribbleAction
import com.draw.onme.presentation.scribble.ScribbleUiState
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.roundToInt

/**
 * Collapsible drawing toolbar that rolls back into a compact, movable FAB.
 * - Only movable when collapsed:
 *   - The FAB can be dragged anywhere freely across the canvas in 2D.
 *   - When released or thrown, it snaps and attaches directly to the left or right border of the screen.
 *   - It NEVER stays in between on the canvas.
 * - When tapped while collapsed: Expands the toolbar at the current dock location.
 * - When expanded: The FAB is NOT movable. Clicking it collapses the toolbar back into the FAB at that exact border location.
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

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val density = LocalDensity.current
        val coroutineScope = rememberCoroutineScope()
        val maxWidthPx = constraints.maxWidth.toFloat()
        val maxHeightPx = constraints.maxHeight.toFloat()
        val fabSizePx = with(density) { 52.dp.toPx() }
        val marginPx = with(density) { 16.dp.toPx() }
        val minXPx = marginPx
        val maxXPx = (maxWidthPx - fabSizePx - marginPx).coerceAtLeast(minXPx)

        val insets = WindowInsets.statusBars.asPaddingValues()
        val navInsets = WindowInsets.navigationBars.asPaddingValues()
        val topLimitPx = with(density) { insets.calculateTopPadding().toPx() } + with(density) { 68.dp.toPx() }
        val bottomLimitPx = (maxHeightPx - with(density) { navInsets.calculateBottomPadding().toPx() } - fabSizePx - with(density) { 16.dp.toPx() }).coerceAtLeast(topLimitPx)

        var isDockedOnLeft by rememberSaveable { mutableStateOf(false) }
        var fabYFraction by rememberSaveable { mutableFloatStateOf(0.85f) }

        val targetDockX = if (isDockedOnLeft) minXPx else maxXPx
        val targetDockY = topLimitPx + (bottomLimitPx - topLimitPx) * fabYFraction

        val animX = remember { Animatable(targetDockX) }
        val animY = remember { Animatable(targetDockY) }

        LaunchedEffect(maxWidthPx, maxHeightPx, isDockedOnLeft, fabYFraction) {
            val currentDockX = if (isDockedOnLeft) minXPx else maxXPx
            val currentDockY = topLimitPx + (bottomLimitPx - topLimitPx) * fabYFraction
            if (abs(animX.value - currentDockX) > 2f) animX.animateTo(currentDockX, spring())
            if (abs(animY.value - currentDockY) > 2f) animY.animateTo(currentDockY, spring())
        }

        val onFabDrag: (Offset) -> Unit = { dragAmount ->
            val newX = (animX.value + dragAmount.x).coerceIn(minXPx, maxXPx)
            val newY = (animY.value + dragAmount.y).coerceIn(topLimitPx, bottomLimitPx)
            coroutineScope.launch {
                animX.snapTo(newX)
                animY.snapTo(newY)
            }
        }

        val onFabDragEnd: (Velocity) -> Unit = { velocity ->
            val midX = (minXPx + maxXPx) / 2f
            val dockLeft = when {
                velocity.x < -500f -> true
                velocity.x > 500f -> false
                else -> animX.value < midX
            }
            isDockedOnLeft = dockLeft
            val targetX = if (dockLeft) minXPx else maxXPx

            val coastY = animY.value + (velocity.y * 0.08f)
            val targetY = coastY.coerceIn(topLimitPx, bottomLimitPx)
            val finalFraction = if (bottomLimitPx > topLimitPx) {
                ((targetY - topLimitPx) / (bottomLimitPx - topLimitPx)).coerceIn(0f, 1f)
            } else 0.85f
            fabYFraction = finalFraction

            coroutineScope.launch {
                launch { animX.animateTo(targetX, spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow)) }
                launch { animY.animateTo(targetY, spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow)) }
            }
        }

        if (isLandscape) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                if (!isDockedOnLeft) {
                    // Docked on Right border
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset { IntOffset(0, animY.value.roundToInt()) }
                            .padding(end = 16.dp),
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
                                maxEraserSize = maxEraserSize,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }

                        if (isExpanded) {
                            ToolbarFab(
                                isExpanded = true,
                                onTap = { isExpanded = false },
                                arrowVector = Icons.AutoMirrored.Filled.ArrowForward
                            )
                        }
                    }
                } else {
                    // Docked on Left border
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset { IntOffset(0, animY.value.roundToInt()) }
                            .padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        if (isExpanded) {
                            ToolbarFab(
                                isExpanded = true,
                                onTap = { isExpanded = false },
                                arrowVector = Icons.AutoMirrored.Filled.ArrowBack
                            )
                        }

                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = slideInHorizontally(initialOffsetX = { -it }) + expandHorizontally(expandFrom = Alignment.Start) + fadeIn(),
                            exit = slideOutHorizontally(targetOffsetX = { -it }) + shrinkHorizontally(shrinkTowards = Alignment.Start) + fadeOut(),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            DrawingToolbar(
                                uiState = uiState,
                                onAction = onAction,
                                maxEraserSize = maxEraserSize,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                // When collapsed: free movable FAB across screen
                if (!isExpanded) {
                    ToolbarFab(
                        isExpanded = false,
                        onTap = { isExpanded = true },
                        modifier = Modifier.offset {
                            IntOffset(animX.value.roundToInt(), animY.value.roundToInt())
                        },
                        onDrag = onFabDrag,
                        onDragEnd = onFabDragEnd
                    )
                }
            }
        } else {
            // Portrait: Toolbar always appears below the FAB
            var toolbarHeightPx by remember { mutableFloatStateOf(with(density) { 120.dp.toPx() }) }
            val spacingPx = with(density) { 8.dp.toPx() }
            val bottomInset = with(density) { navInsets.calculateBottomPadding().toPx() }

            // Maximum Y the FAB can be at while expanded so the toolbar fits on screen below it
            val maxExpandedFabY = (maxHeightPx - bottomInset - fabSizePx - toolbarHeightPx - spacingPx - with(density) { 16.dp.toPx() }).coerceAtLeast(topLimitPx)

            val targetFabY = if (isExpanded) {
                animY.value.coerceAtMost(maxExpandedFabY)
            } else {
                animY.value
            }

            val animatedFabY by animateFloatAsState(
                targetValue = targetFabY,
                animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
                label = "portraitFabY"
            )

            val toolbarY = animatedFabY + fabSizePx + spacingPx

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = slideInVertically(initialOffsetY = { -it }) + expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(0, toolbarY.roundToInt()) }
                ) {
                    DrawingToolbar(
                        uiState = uiState,
                        onAction = onAction,
                        maxEraserSize = maxEraserSize,
                        modifier = Modifier.onSizeChanged { size ->
                            if (size.height > 0) toolbarHeightPx = size.height.toFloat()
                        }
                    )
                }

                if (isExpanded) {
                    ToolbarFab(
                        isExpanded = true,
                        onTap = { isExpanded = false },
                        arrowVector = Icons.Default.KeyboardArrowUp,
                        modifier = Modifier.offset {
                            IntOffset(animX.value.roundToInt(), animatedFabY.roundToInt())
                        }
                    )
                } else {
                    ToolbarFab(
                        isExpanded = false,
                        onTap = { isExpanded = true },
                        modifier = Modifier.offset {
                            IntOffset(animX.value.roundToInt(), animY.value.roundToInt())
                        },
                        onDrag = onFabDrag,
                        onDragEnd = onFabDragEnd
                    )
                }
            }
        }
    }
}

/**
 * Floating Action Button for the drawing toolbar:
 * - When expanded: NOT movable. Plain clickable button that immediately collapses the toolbar.
 * - When collapsed: Movable via drag gestures in 2D. Tapping it expands the toolbar.
 * - Attaches itself to the borders when released or thrown.
 */
@Composable
private fun ToolbarFab(
    isExpanded: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
    arrowVector: ImageVector = Icons.Default.KeyboardArrowDown,
    onDrag: (Offset) -> Unit = {},
    onDragEnd: (Velocity) -> Unit = {}
) {
    val viewConfig = LocalViewConfiguration.current

    val fabModifier = if (isExpanded) {
        modifier
            .size(52.dp)
            .clickable(
                onClick = onTap,
                role = Role.Button
            )
    } else {
        modifier
            .size(52.dp)
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    down.consume()
                    var isDrag = false
                    var totalDrag = Offset.Zero
                    val touchSlop = viewConfig.touchSlop
                    val velocityTracker = VelocityTracker()
                    velocityTracker.addPosition(down.uptimeMillis, down.position)

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id }
                        if (change == null) {
                            if (isDrag) {
                                val velocity = velocityTracker.calculateVelocity()
                                onDragEnd(velocity)
                            }
                            break
                        }

                        velocityTracker.addPosition(change.uptimeMillis, change.position)

                        if (!change.pressed) {
                            change.consume()
                            if (!isDrag) {
                                onTap()
                            } else {
                                val velocity = velocityTracker.calculateVelocity()
                                onDragEnd(velocity)
                            }
                            break
                        }

                        change.consume()
                        val dragAmount = change.position - change.previousPosition
                        totalDrag += dragAmount

                        if (!isDrag) {
                            if (totalDrag.getDistance() > touchSlop) {
                                isDrag = true
                                onDrag(dragAmount)
                            }
                        } else {
                            onDrag(dragAmount)
                        }
                    }
                }
            }
    }

    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shadowElevation = 6.dp,
        tonalElevation = 6.dp,
        modifier = fabModifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            AnimatedContent(
                targetState = isExpanded,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(200, delayMillis = 50)) +
                        scaleIn(initialScale = 0.75f, animationSpec = tween(200, delayMillis = 50)))
                        .togetherWith(fadeOut(animationSpec = tween(80)) + scaleOut(targetScale = 0.75f, animationSpec = tween(80)))
                },
                label = "ToolbarFabIcon"
            ) { expanded ->
                Icon(
                    imageVector = if (expanded) arrowVector else Icons.Default.Palette,
                    contentDescription = if (expanded) "Collapse Toolbar" else "Show Tools",
                    modifier = Modifier.size(26.dp)
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

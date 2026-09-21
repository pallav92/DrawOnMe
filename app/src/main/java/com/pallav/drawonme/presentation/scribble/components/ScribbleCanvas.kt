package com.pallav.drawonme.presentation.scribble.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.pallav.drawonme.domain.model.DrawingTool
import com.pallav.drawonme.domain.model.Point
import com.pallav.drawonme.domain.model.Stroke
import com.pallav.drawonme.presentation.scribble.ScribbleAction
import com.pallav.drawonme.presentation.scribble.ScribbleUiState
import com.pallav.drawonme.presentation.scribble.model.CanvasTransformState
import com.pallav.drawonme.presentation.scribble.model.rememberCanvasTransformState
import kotlin.math.floor

/**
 * ScribbleCanvas renders committed and ongoing drawing strokes with full support for:
 * - Excalidraw-style infinite dot grid background that moves and scales with pan & zoom.
 * - 5x device window workspace boundaries.
 * - Single-finger drawing in world coordinates.
 * - Two-finger universal pinch-to-zoom & two-finger pan for all tools (Pen, Eraser, Hand).
 * - Dedicated Hand (Pan) tool for 1-finger panning.
 * - Persistent, interactive on-screen eraser disc reflecting active eraser width.
 * - Seamless eraser blending via offscreen compositing.
 */
@Composable
fun ScribbleCanvas(
    uiState: ScribbleUiState,
    onAction: (ScribbleAction) -> Unit,
    modifier: Modifier = Modifier,
    transformState: CanvasTransformState = rememberCanvasTransformState(),
    backgroundContent: (@Composable () -> Unit)? = null
) {
    val backgroundColor = MaterialTheme.colorScheme.surface
    val dotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.16f)
    val boundaryColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.50f)

    var eraserPosition by remember { mutableStateOf<Offset?>(null) }

    // Ensure eraser size is at least 40px when eraser tool is active, and restore pen size when pen is active
    LaunchedEffect(uiState.selectedTool) {
        if (uiState.selectedTool == DrawingTool.ERASER) {
            if (uiState.strokeWidth < 40f) {
                onAction(ScribbleAction.SetStrokeWidth(40f))
            }
        } else if (uiState.selectedTool == DrawingTool.PEN) {
            if (uiState.strokeWidth > 24f) {
                onAction(ScribbleAction.SetStrokeWidth(6f))
            }
        }
    }

    val defaultCenter = if (transformState.viewportSize.width > 0 && transformState.viewportSize.height > 0) {
        Offset(transformState.viewportSize.width / 2f, transformState.viewportSize.height / 2f)
    } else {
        Offset(540f, 960f)
    }
    val currentEraserPos = eraserPosition ?: defaultCenter
    val visualRadius = (uiState.strokeWidth * transformState.zoom) / 2f

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .onSizeChanged { size ->
                transformState.viewportSize = size
            }
    ) {
        // Excalidraw-style dot grid & 5x workspace boundary
        Canvas(modifier = Modifier.fillMaxSize()) {
            val zoom = transformState.zoom
            val pan = transformState.pan
            val gridSpacing = 28.dp.toPx()

            val minWorldX = (-pan.x) / zoom - gridSpacing
            val maxWorldX = (size.width - pan.x) / zoom + gridSpacing
            val minWorldY = (-pan.y) / zoom - gridSpacing
            val maxWorldY = (size.height - pan.y) / zoom + gridSpacing

            val startX = floor(minWorldX / gridSpacing) * gridSpacing
            val startY = floor(minWorldY / gridSpacing) * gridSpacing

            val dotRadius = (1.5f * zoom.coerceIn(0.6f, 1.8f))

            var x = startX
            while (x <= maxWorldX) {
                var y = startY
                while (y <= maxWorldY) {
                    val screenX = x * zoom + pan.x
                    val screenY = y * zoom + pan.y
                    drawCircle(
                        color = dotColor,
                        radius = dotRadius,
                        center = Offset(screenX, screenY)
                    )
                    y += gridSpacing
                }
                x += gridSpacing
            }

            // 5x canvas workspace boundary
            val width = size.width
            val height = size.height
            if (width > 0f && height > 0f) {
                val boundaryLeft = -2f * width * zoom + pan.x
                val boundaryTop = -2f * height * zoom + pan.y
                val boundaryWidth = 5f * width * zoom
                val boundaryHeight = 5f * height * zoom

                drawRect(
                    color = boundaryColor,
                    topLeft = Offset(boundaryLeft, boundaryTop),
                    size = Size(boundaryWidth, boundaryHeight),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
                    )
                )
            }
        }

        // Optional background content (stencil guide, etc.) transformed in world space
        if (backgroundContent != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = transformState.pan.x
                        translationY = transformState.pan.y
                        scaleX = transformState.zoom
                        scaleY = transformState.zoom
                        transformOrigin = TransformOrigin(0f, 0f)
                    }
            ) {
                backgroundContent()
            }
        }

        // Drawing layer: handles gesture input and stroke rendering
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                .pointerInput(uiState.selectedTool) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val initialTool = uiState.selectedTool

                        when (initialTool) {
                            DrawingTool.HAND -> {
                                // Hand tool: 1 or 2 fingers pan/zoom
                                down.consume()
                                var prevCentroid = down.position
                                var prevDistance = 0f

                                while (true) {
                                    val event = awaitPointerEvent()
                                    val pressedPointers = event.changes.filter { it.pressed }
                                    if (pressedPointers.isEmpty()) break

                                    if (pressedPointers.size == 1) {
                                        val change = pressedPointers.first()
                                        val delta = change.position - change.previousPosition
                                        transformState.panBy(delta)
                                        change.consume()
                                        prevCentroid = change.position
                                    } else if (pressedPointers.size >= 2) {
                                        val p1 = pressedPointers[0]
                                        val p2 = pressedPointers[1]
                                        val centroid = (p1.position + p2.position) / 2f
                                        val distance = (p1.position - p2.position).getDistance()

                                        val centroidDelta = centroid - prevCentroid
                                        val zoomFactor = if (prevDistance > 0f && distance > 0f) distance / prevDistance else 1f

                                        transformState.zoomBy(zoomFactor, centroid)
                                        transformState.panBy(centroidDelta)

                                        prevCentroid = centroid
                                        prevDistance = distance

                                        pressedPointers.forEach { it.consume() }
                                    }
                                }
                            }

                            DrawingTool.ERASER -> {
                                val initialPointers = currentEvent.changes.filter { it.pressed }
                                var isTwoFingerMode = initialPointers.size >= 2
                                var isStrokeActive = false

                                if (!isTwoFingerMode) {
                                    down.consume()
                                    eraserPosition = down.position
                                    val worldPoint = transformState.screenToWorld(down.position)
                                    onAction(ScribbleAction.StartStroke(worldPoint))
                                    isStrokeActive = true
                                } else {
                                    val p1 = initialPointers[0]
                                    val p2 = initialPointers[1]
                                    eraserPosition = (p1.position + p2.position) / 2f
                                    initialPointers.forEach { it.consume() }
                                }

                                var prevCentroid = down.position
                                var prevDistance = 0f
                                val strokePointerId = down.id

                                if (isTwoFingerMode && initialPointers.size >= 2) {
                                    val p1 = initialPointers[0]
                                    val p2 = initialPointers[1]
                                    prevCentroid = (p1.position + p2.position) / 2f
                                    prevDistance = (p1.position - p2.position).getDistance()
                                }

                                while (true) {
                                    val event = awaitPointerEvent()
                                    val pressedPointers = event.changes.filter { it.pressed }
                                    if (pressedPointers.isEmpty()) {
                                        if (isStrokeActive) {
                                            onAction(ScribbleAction.EndStroke)
                                        }
                                        break
                                    }

                                    if (!isTwoFingerMode && pressedPointers.size >= 2) {
                                        // Second finger arrived! Cancel in-progress stroke and switch to universal pan/zoom
                                        if (isStrokeActive) {
                                            onAction(ScribbleAction.CancelStroke)
                                            isStrokeActive = false
                                        }
                                        isTwoFingerMode = true
                                        val p1 = pressedPointers[0]
                                        val p2 = pressedPointers[1]
                                        prevCentroid = (p1.position + p2.position) / 2f
                                        prevDistance = (p1.position - p2.position).getDistance()
                                        eraserPosition = prevCentroid
                                        pressedPointers.forEach { it.consume() }
                                        continue
                                    }

                                    if (isTwoFingerMode) {
                                        if (pressedPointers.size >= 2) {
                                            val p1 = pressedPointers[0]
                                            val p2 = pressedPointers[1]
                                            val centroid = (p1.position + p2.position) / 2f
                                            val distance = (p1.position - p2.position).getDistance()

                                            val centroidDelta = centroid - prevCentroid
                                            val zoomFactor = if (prevDistance > 0f && distance > 0f) distance / prevDistance else 1f

                                            transformState.zoomBy(zoomFactor, centroid)
                                            transformState.panBy(centroidDelta)

                                            prevCentroid = centroid
                                            prevDistance = distance
                                            eraserPosition = centroid

                                            pressedPointers.forEach { it.consume() }
                                        } else {
                                            pressedPointers.forEach { it.consume() }
                                        }
                                    } else {
                                        // Single finger erasing in world coordinates
                                        val change = pressedPointers.firstOrNull { it.id == strokePointerId }
                                        if (change == null || !change.pressed) {
                                            if (isStrokeActive) {
                                                onAction(ScribbleAction.EndStroke)
                                                isStrokeActive = false
                                            }
                                            break
                                        }
                                        change.consume()
                                        eraserPosition = change.position
                                        val worldPoint = transformState.screenToWorld(change.position)
                                        onAction(ScribbleAction.AddPoint(worldPoint))
                                    }
                                }
                            }

                            DrawingTool.PEN -> {
                                val initialPointers = currentEvent.changes.filter { it.pressed }
                                var isTwoFingerMode = initialPointers.size >= 2
                                var isStrokeActive = false

                                if (!isTwoFingerMode) {
                                    down.consume()
                                    val worldPoint = transformState.screenToWorld(down.position)
                                    onAction(ScribbleAction.StartStroke(worldPoint))
                                    isStrokeActive = true
                                }

                                var prevCentroid = down.position
                                var prevDistance = 0f
                                val strokePointerId = down.id

                                while (true) {
                                    val event = awaitPointerEvent()
                                    val pressedPointers = event.changes.filter { it.pressed }
                                    if (pressedPointers.isEmpty()) {
                                        if (isStrokeActive) {
                                            onAction(ScribbleAction.EndStroke)
                                        }
                                        break
                                    }

                                    if (!isTwoFingerMode && pressedPointers.size >= 2) {
                                        // Second finger arrived! Cancel in-progress stroke and switch to pan/zoom
                                        if (isStrokeActive) {
                                            onAction(ScribbleAction.CancelStroke)
                                            isStrokeActive = false
                                        }
                                        isTwoFingerMode = true
                                        val p1 = pressedPointers[0]
                                        val p2 = pressedPointers[1]
                                        prevCentroid = (p1.position + p2.position) / 2f
                                        prevDistance = (p1.position - p2.position).getDistance()
                                        pressedPointers.forEach { it.consume() }
                                        continue
                                    }

                                    if (isTwoFingerMode) {
                                        if (pressedPointers.size >= 2) {
                                            val p1 = pressedPointers[0]
                                            val p2 = pressedPointers[1]
                                            val centroid = (p1.position + p2.position) / 2f
                                            val distance = (p1.position - p2.position).getDistance()

                                            val centroidDelta = centroid - prevCentroid
                                            val zoomFactor = if (prevDistance > 0f && distance > 0f) distance / prevDistance else 1f

                                            transformState.zoomBy(zoomFactor, centroid)
                                            transformState.panBy(centroidDelta)

                                            prevCentroid = centroid
                                            prevDistance = distance

                                            pressedPointers.forEach { it.consume() }
                                        } else {
                                            val change = pressedPointers.first()
                                            val delta = change.position - change.previousPosition
                                            transformState.panBy(delta)
                                            change.consume()
                                        }
                                    } else {
                                        // Single finger drawing in world coordinates
                                        val change = pressedPointers.firstOrNull { it.id == strokePointerId }
                                        if (change == null || !change.pressed) {
                                            if (isStrokeActive) {
                                                onAction(ScribbleAction.EndStroke)
                                                isStrokeActive = false
                                            }
                                            break
                                        }
                                        change.consume()
                                        val worldPoint = transformState.screenToWorld(change.position)
                                        onAction(ScribbleAction.AddPoint(worldPoint))
                                    }
                                }
                            }
                        }
                    }
                }
        ) {
            withTransform({
                translate(transformState.pan.x, transformState.pan.y)
                scale(transformState.zoom, transformState.zoom, Offset.Zero)
            }) {
                // Draw committed strokes
                for (stroke in uiState.strokes) {
                    drawScribbleStroke(stroke)
                }

                // Draw active stroke currently being traced
                uiState.currentStroke?.let { activeStroke ->
                    drawScribbleStroke(activeStroke)
                }
            }
        }

        // Interactive on-screen eraser disc
        if (uiState.selectedTool == DrawingTool.ERASER) {
            EraserCursorOverlay(
                position = currentEraserPos,
                radius = visualRadius
            )
        }
    }
}

@Composable
private fun EraserCursorOverlay(
    position: Offset,
    radius: Float,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier.fillMaxSize()) {
        // Soft translucent fill
        drawCircle(
            color = primaryColor.copy(alpha = 0.10f),
            radius = radius,
            center = position
        )

        // Dashed outer ring
        drawCircle(
            color = primaryColor.copy(alpha = 0.65f),
            radius = radius,
            center = position,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
        )

        // Center pivot dot
        drawCircle(
            color = primaryColor.copy(alpha = 0.75f),
            radius = 2.5.dp.toPx(),
            center = position
        )
    }
}

private fun DrawScope.drawScribbleStroke(stroke: Stroke) {
    if (stroke.points.isEmpty()) return

    val isEraser = stroke.tool == DrawingTool.ERASER
    val drawColor = if (isEraser) Color.Transparent else Color(stroke.color.argb)
    val blendMode = if (isEraser) BlendMode.Clear else BlendMode.SrcOver

    if (stroke.points.size == 1) {
        val pt = stroke.points[0]
        drawCircle(
            color = drawColor,
            radius = stroke.strokeWidth / 2f,
            center = Offset(pt.x, pt.y),
            blendMode = blendMode
        )
        return
    }

    val path = Path()
    val first = stroke.points[0]
    path.moveTo(first.x, first.y)

    for (i in 1 until stroke.points.size) {
        val prev = stroke.points[i - 1]
        val curr = stroke.points[i]
        val midX = (prev.x + curr.x) / 2f
        val midY = (prev.y + curr.y) / 2f
        path.quadraticTo(prev.x, prev.y, midX, midY)
    }
    val last = stroke.points.last()
    path.lineTo(last.x, last.y)

    drawPath(
        path = path,
        color = drawColor,
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            width = stroke.strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        ),
        blendMode = blendMode
    )
}

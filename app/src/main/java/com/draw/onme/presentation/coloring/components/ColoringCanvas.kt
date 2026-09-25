package com.draw.onme.presentation.coloring.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import com.draw.onme.domain.model.ColoringPage
import com.draw.onme.domain.model.ColoringRegion
import com.draw.onme.domain.model.ColoringTool
import com.draw.onme.domain.model.DrawingTool
import com.draw.onme.domain.model.Stroke
import com.draw.onme.presentation.coloring.ColoringAction
import com.draw.onme.presentation.coloring.ColoringUiState
import com.draw.onme.presentation.coloring.ColoringViewModel.Companion.CANONICAL_SIZE
import com.draw.onme.presentation.scribble.model.CanvasTransformState
import com.draw.onme.presentation.scribble.model.rememberCanvasTransformState

/**
 * Multi-layer drawing canvas for the Coloring Book Studio:
 * 1. Region Fills: Vector closed paths filled with solid colors.
 * 2. Flood Fills: Enclosed area scanline fills.
 * 3. Freehand Crayon Strokes: Tactile strokes drawn across canvas.
 * 4. Line-Art Outlines: Permanent bold contours (charcoal in light mode, glowing silver in dark mode).
 */
@Composable
fun ColoringCanvas(
    uiState: ColoringUiState,
    onAction: (ColoringAction) -> Unit,
    modifier: Modifier = Modifier,
    transformState: CanvasTransformState = rememberCanvasTransformState()
) {
    val page = uiState.page
    val isDark = isSystemInDarkTheme()

    val canvasBackgroundColor = MaterialTheme.colorScheme.surface
    val outlineColor = if (isDark) {
        Color(0xFFF0F0F0)
    } else {
        Color(0xFF212121)
    }

    // Pre-calculate region paths for fast rendering & clipping
    val regionPaths = remember(page) {
        page?.regions?.associate { region ->
            region.id to buildRegionPath(region, CANONICAL_SIZE)
        } ?: emptyMap()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(canvasBackgroundColor)
            .onSizeChanged { size ->
                transformState.viewportSize = size
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                .pointerInput(uiState.selectedTool, transformState) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val tool = uiState.selectedTool

                        if (tool == ColoringTool.BUCKET) {
                            val initialPointers = currentEvent.changes.filter { it.pressed }
                            var isTwoFingerMode = initialPointers.size >= 2
                            var isDrag = isTwoFingerMode
                            val startPos = down.position
                            var prevCentroid = if (isTwoFingerMode && initialPointers.size >= 2) {
                                (initialPointers[0].position + initialPointers[1].position) / 2f
                            } else {
                                down.position
                            }
                            var prevDistance = if (isTwoFingerMode && initialPointers.size >= 2) {
                                (initialPointers[0].position - initialPointers[1].position).getDistance()
                            } else {
                                0f
                            }
                            val strokePointerId = down.id
                            var maxDistFromStart = 0f

                            down.consume()

                            while (true) {
                                val event = awaitPointerEvent()
                                val pressedPointers = event.changes.filter { it.pressed }
                                if (pressedPointers.isEmpty()) {
                                    if (!isTwoFingerMode && maxDistFromStart <= 36f) {
                                        val worldPoint = transformState.screenToWorld(startPos)
                                        onAction(ColoringAction.TapCanvas(worldPoint))
                                    }
                                    break
                                }

                                if (!isTwoFingerMode && pressedPointers.size >= 2) {
                                    // Second finger arrived! Switch to 2-finger pan & zoom
                                    isTwoFingerMode = true
                                    isDrag = true
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
                                    // Single finger in Bucket mode: tap or 1-finger pan
                                    val change = pressedPointers.firstOrNull { it.id == strokePointerId }
                                    if (change == null || !change.pressed) {
                                        if (!isTwoFingerMode && maxDistFromStart <= 36f) {
                                            val worldPoint = transformState.screenToWorld(startPos)
                                            onAction(ColoringAction.TapCanvas(worldPoint))
                                        }
                                        break
                                    }

                                    change.consume()
                                    val currentPos = change.position
                                    val dist = (currentPos - startPos).getDistance()
                                    if (dist > maxDistFromStart) {
                                        maxDistFromStart = dist
                                    }

                                    if (!isDrag) {
                                        if (dist > 36f) {
                                            isDrag = true
                                            val delta = currentPos - change.previousPosition
                                            transformState.panBy(delta)
                                        }
                                    } else {
                                        val delta = currentPos - change.previousPosition
                                        transformState.panBy(delta)
                                    }
                                }
                            }
                        } else {
                            // Magic Crayon or Eraser mode
                            val initialPointers = currentEvent.changes.filter { it.pressed }
                            var isTwoFingerMode = initialPointers.size >= 2
                            var isStrokeActive = false

                            if (!isTwoFingerMode) {
                                down.consume()
                                val worldPoint = transformState.screenToWorld(down.position)
                                onAction(ColoringAction.StartStroke(worldPoint))
                                isStrokeActive = true
                            }

                            var prevCentroid = if (isTwoFingerMode && initialPointers.size >= 2) {
                                (initialPointers[0].position + initialPointers[1].position) / 2f
                            } else {
                                down.position
                            }
                            var prevDistance = if (isTwoFingerMode && initialPointers.size >= 2) {
                                (initialPointers[0].position - initialPointers[1].position).getDistance()
                            } else {
                                0f
                            }
                            val strokePointerId = down.id

                            while (true) {
                                val event = awaitPointerEvent()
                                val pressedPointers = event.changes.filter { it.pressed }
                                if (pressedPointers.isEmpty()) {
                                    if (isStrokeActive) {
                                        onAction(ColoringAction.EndStroke)
                                    }
                                    break
                                }

                                if (!isTwoFingerMode && pressedPointers.size >= 2) {
                                    // Second finger touch: cancel stroke and enter 2-finger pan/zoom
                                    if (isStrokeActive) {
                                        onAction(ColoringAction.CancelStroke)
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
                                    // Single finger drawing
                                    val change = pressedPointers.firstOrNull { it.id == strokePointerId }
                                    if (change == null || !change.pressed) {
                                        if (isStrokeActive) {
                                            onAction(ColoringAction.EndStroke)
                                            isStrokeActive = false
                                        }
                                        break
                                    }
                                    change.consume()
                                    val worldPoint = transformState.screenToWorld(change.position)
                                    onAction(ColoringAction.AddPoint(worldPoint))
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
                // Layer 1: Filled regions
                if (page != null) {
                    for (region in page.regions) {
                        val color = uiState.fills[region.id] ?: continue
                        val path = regionPaths[region.id] ?: continue
                        drawPath(
                            path = path,
                            color = Color(color.argb),
                            style = Fill
                        )
                    }
                }

                // Layer 1b: Flood-filled closed areas (enclosed by outlines or crayon strokes)
                for (fill in uiState.customFills) {
                    val composeColor = Color(fill.color.argb)
                    for (span in fill.spans) {
                        drawLine(
                            color = composeColor,
                            start = Offset(span.x1, span.y),
                            end = Offset(span.x2, span.y),
                            strokeWidth = 2.5f,
                            cap = StrokeCap.Square
                        )
                    }
                }

                // Layer 2: Freehand crayon strokes (draw anywhere across canvas)
                for (stroke in uiState.strokes) {
                    drawColoringStroke(stroke)
                }

                uiState.currentStroke?.let { active ->
                    drawColoringStroke(active)
                }

                // Layer 3: Bold line-art outlines on top
                if (page != null) {
                    for (outline in page.outlines) {
                        if (outline.points.isEmpty()) continue
                        val path = Path()
                        val first = outline.points[0]
                        path.moveTo(first.x * CANONICAL_SIZE, first.y * CANONICAL_SIZE)

                        for (i in 1 until outline.points.size) {
                            val pt = outline.points[i]
                            path.lineTo(pt.x * CANONICAL_SIZE, pt.y * CANONICAL_SIZE)
                        }

                        if (outline.isClosed) {
                            path.close()
                        }

                        drawPath(
                            path = path,
                            color = outlineColor,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = outline.strokeWidth * 1.5f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }
        }
    }
}

private fun buildRegionPath(region: ColoringRegion, canvasSize: Float): Path {
    val path = Path()
    if (region.boundaryPoints.isEmpty()) return path

    val first = region.boundaryPoints[0]
    path.moveTo(first.x * canvasSize, first.y * canvasSize)
    for (i in 1 until region.boundaryPoints.size) {
        val pt = region.boundaryPoints[i]
        path.lineTo(pt.x * canvasSize, pt.y * canvasSize)
    }
    path.close()
    return path
}

private fun DrawScope.drawColoringStroke(stroke: Stroke) {
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

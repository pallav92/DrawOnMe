package com.pallav.drawonme.presentation.scribble.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.pallav.drawonme.domain.model.DrawingTool
import com.pallav.drawonme.domain.model.Point
import com.pallav.drawonme.domain.model.Stroke
import com.pallav.drawonme.presentation.scribble.ScribbleAction
import com.pallav.drawonme.presentation.scribble.ScribbleUiState

import androidx.compose.foundation.layout.Box

/**
 * ScribbleCanvas renders committed and ongoing drawing strokes.
 * Uses zero-slop touch gesture tracking and offscreen compositing for seamless eraser blending.
 *
 * @param uiState Current UI state containing all committed strokes and active stroke.
 * @param onAction Callback to dispatch drawing actions to the ViewModel.
 * @param modifier Optional modifier applied to the canvas.
 * @param backgroundContent Optional background composable layer (e.g. stencil guide or grid lines)
 *        placed behind the drawing layer so it is never erased by the eraser.
 */
@Composable
fun ScribbleCanvas(
    uiState: ScribbleUiState,
    onAction: (ScribbleAction) -> Unit,
    modifier: Modifier = Modifier,
    backgroundContent: (@Composable () -> Unit)? = null
) {
    val backgroundColor = MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Optional background layer (stencil outline, paper grid, etc.)
        backgroundContent?.invoke()

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        down.consume()
                        onAction(ScribbleAction.StartStroke(Point(down.position.x, down.position.y)))

                        val pointerId = down.id
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == pointerId } ?: break
                            if (!change.pressed) {
                                change.consume()
                                onAction(ScribbleAction.EndStroke)
                                break
                            }
                            change.consume()
                            onAction(ScribbleAction.AddPoint(Point(change.position.x, change.position.y)))
                        }
                    }
                }
        ) {
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

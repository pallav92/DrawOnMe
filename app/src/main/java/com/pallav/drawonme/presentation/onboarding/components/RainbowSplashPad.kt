package com.pallav.drawonme.presentation.onboarding.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class RainbowStroke(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

/**
 * Interactive tactile rainbow splash pad embedded on the onboarding screen.
 * Teaches children in under 2 seconds that touching the screen creates colorful art.
 */
@Composable
fun RainbowSplashPad(
    modifier: Modifier = Modifier
) {
    val strokes = remember { mutableStateListOf<RainbowStroke>() }
    var hasInteracted by remember { mutableStateOf(false) }
    var currentHue by remember { mutableFloatStateOf(0f) }

    val rainbowBorder = remember {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFFF5252),
                Color(0xFFFF7A00),
                Color(0xFFFFD600),
                Color(0xFF00E676),
                Color(0xFF00B0FF),
                Color(0xFFD500F9)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(115.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            .border(2.5.dp, rainbowBorder, RoundedCornerShape(24.dp))
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    down.consume()
                    hasInteracted = true

                    val strokeColor = Color.hsl(currentHue % 360f, 0.85f, 0.50f)
                    currentHue = (currentHue + 32f) % 360f

                    val currentPoints = mutableListOf(down.position)
                    val activeStroke = RainbowStroke(
                        points = currentPoints.toList(),
                        color = strokeColor,
                        strokeWidth = 14f
                    )
                    strokes.add(activeStroke)
                    val activeIndex = strokes.lastIndex

                    val pointerId = down.id
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == pointerId } ?: break
                        if (!change.pressed) {
                            change.consume()
                            break
                        }
                        change.consume()
                        currentPoints.add(change.position)
                        strokes[activeIndex] = activeStroke.copy(points = currentPoints.toList())
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (stroke in strokes) {
                if (stroke.points.size == 1) {
                    drawCircle(
                        color = stroke.color,
                        radius = stroke.strokeWidth / 2f,
                        center = stroke.points[0]
                    )
                } else if (stroke.points.size > 1) {
                    val path = Path()
                    path.moveTo(stroke.points[0].x, stroke.points[0].y)
                    for (i in 1 until stroke.points.size) {
                        val prev = stroke.points[i - 1]
                        val curr = stroke.points[i]
                        val midX = (prev.x + curr.x) / 2f
                        val midY = (prev.y + curr.y) / 2f
                        path.quadraticTo(prev.x, prev.y, midX, midY)
                    }
                    path.lineTo(stroke.points.last().x, stroke.points.last().y)

                    drawPath(
                        path = path,
                        color = stroke.color,
                        style = Stroke(
                            width = stroke.strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }

        if (!hasInteracted) {
            Text(
                text = "✨ Touch & swirl here to make magic! ✨",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
        }
    }
}

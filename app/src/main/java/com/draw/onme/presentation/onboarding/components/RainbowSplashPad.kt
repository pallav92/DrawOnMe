package com.draw.onme.presentation.onboarding.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

private data class GlowingRainbowPoint(
    val position: Offset,
    val hue: Float
)

private data class GlowingRainbowStroke(
    val points: List<GlowingRainbowPoint>
)

private data class SparkleParticle(
    val position: Offset,
    val velocity: Offset,
    val hue: Float,
    val size: Float,
    val alpha: Float = 1.0f,
    val rotation: Float = 0f,
    val life: Float = 1.0f
)

/**
 * Interactive tactile rainbow splash pad ("magic board") embedded on the onboarding screen.
 * Features a multi-layer luminous neon glow, continuous rainbow hue progression along drawn paths,
 * an active comet head, and drifting magical sparkle stars.
 */
@Composable
fun RainbowSplashPad(
    modifier: Modifier = Modifier,
    height: Dp = 190.dp
) {
    val strokes = remember { mutableStateListOf<GlowingRainbowStroke>() }
    val activeTouches = remember { mutableStateMapOf<PointerId, GlowingRainbowPoint>() }
    var particles by remember { mutableStateOf(listOf<SparkleParticle>()) }
    var hasInteracted by remember { mutableStateOf(false) }
    var currentHue by remember { mutableFloatStateOf(0f) }

    // Particle animation frame loop
    LaunchedEffect(particles.isNotEmpty()) {
        if (particles.isNotEmpty()) {
            var lastTime = withFrameMillis { it }
            while (particles.isNotEmpty()) {
                val currentTime = withFrameMillis { it }
                val dt = (currentTime - lastTime).coerceIn(1L, 32L) / 1000f
                lastTime = currentTime

                particles = particles.mapNotNull { p ->
                    val newLife = p.life - dt * 2.2f
                    if (newLife <= 0f) null
                    else {
                        p.copy(
                            position = p.position + p.velocity * dt,
                            alpha = newLife.coerceIn(0f, 1f),
                            rotation = (p.rotation + dt * 180f) % 360f,
                            life = newLife
                        )
                    }
                }
            }
        }
    }

    val isDark = isSystemInDarkTheme()

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

    val backgroundBrush = remember(isDark) {
        if (isDark) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF161828),
                    Color(0xFF0F111C)
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFFFFF),
                    Color(0xFFF5F2FC)
                )
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .shadow(elevation = if (isDark) 0.dp else 4.dp, shape = RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundBrush)
            .border(2.5.dp, rainbowBorder, RoundedCornerShape(24.dp))
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    down.consume()
                    hasInteracted = true

                    val activeStrokeIndices = mutableMapOf<PointerId, Int>()
                    val startHue = currentHue
                    currentHue = (currentHue + 28f) % 360f

                    val firstPoint = GlowingRainbowPoint(down.position, startHue)
                    strokes.add(GlowingRainbowStroke(listOf(firstPoint)))
                    activeStrokeIndices[down.id] = strokes.lastIndex
                    activeTouches[down.id] = firstPoint

                    while (true) {
                        val event = awaitPointerEvent()
                        val anyPressed = event.changes.any { it.pressed }
                        if (!anyPressed) break

                        for (change in event.changes) {
                            val pointerId = change.id
                            if (change.pressed) {
                                change.consume()

                                val strokeIndex = activeStrokeIndices[pointerId]
                                if (strokeIndex != null && strokeIndex < strokes.size) {
                                    val currentStroke = strokes[strokeIndex]
                                    val prevPoint = currentStroke.points.lastOrNull()
                                    val distance = if (prevPoint != null) {
                                        (change.position - prevPoint.position).getDistance()
                                    } else 0f

                                    if (prevPoint == null || distance >= 3f) {
                                        val newHue = if (prevPoint != null) {
                                            (prevPoint.hue + distance * 0.35f) % 360f
                                        } else currentHue

                                        val newPoint = GlowingRainbowPoint(change.position, newHue)
                                        strokes[strokeIndex] = GlowingRainbowStroke(currentStroke.points + newPoint)
                                        activeTouches[pointerId] = newPoint

                                        // Spawn sparkle particles along the tail
                                        if (distance >= 7f && particles.size < 35) {
                                            val newSparkles = (0..1).map {
                                                val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
                                                val speed = Random.nextFloat() * 50f + 15f
                                                SparkleParticle(
                                                    position = change.position + Offset(
                                                        (Random.nextFloat() - 0.5f) * 12f,
                                                        (Random.nextFloat() - 0.5f) * 12f
                                                    ),
                                                    velocity = Offset(
                                                        kotlin.math.cos(angle) * speed,
                                                        kotlin.math.sin(angle) * speed
                                                    ),
                                                    hue = (newHue + Random.nextFloat() * 40f - 20f + 360f) % 360f,
                                                    size = Random.nextFloat() * 4f + 3.5f,
                                                    rotation = Random.nextFloat() * 360f
                                                )
                                            }
                                            particles = particles + newSparkles
                                        }
                                    }
                                } else {
                                    // Additional simultaneous touch
                                    val newFingerHue = currentHue
                                    currentHue = (currentHue + 32f) % 360f
                                    val newPt = GlowingRainbowPoint(change.position, newFingerHue)
                                    strokes.add(GlowingRainbowStroke(listOf(newPt)))
                                    activeStrokeIndices[pointerId] = strokes.lastIndex
                                    activeTouches[pointerId] = newPt
                                }
                            } else {
                                activeTouches.remove(pointerId)
                            }
                        }
                    }

                    activeTouches.clear()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 1. Draw multi-layered glowing rainbow strokes
            for (stroke in strokes) {
                if (stroke.points.isEmpty()) continue

                if (stroke.points.size == 1) {
                    val pt = stroke.points[0]
                    val color = Color.hsl(pt.hue, 1.0f, if (isDark) 0.55f else 0.48f)
                    drawCircle(color = color.copy(alpha = if (isDark) 0.22f else 0.16f), radius = 14f, center = pt.position)
                    drawCircle(color = color.copy(alpha = if (isDark) 0.55f else 0.45f), radius = 8f, center = pt.position)
                    drawCircle(color = color, radius = 4.5f, center = pt.position)
                    drawCircle(color = if (isDark) Color.White.copy(alpha = 0.9f) else Color.hsl(pt.hue, 1.0f, 0.82f), radius = 2f, center = pt.position)
                    continue
                }

                for (i in 1 until stroke.points.size) {
                    val pPrev = stroke.points[i - 1]
                    val pCurr = stroke.points[i]
                    val segHue = pCurr.hue
                    val segColor = Color.hsl(segHue, 1.0f, if (isDark) 0.55f else 0.48f)

                    // Layer 1: Outer soft ambient glow
                    drawLine(
                        color = segColor.copy(alpha = if (isDark) 0.22f else 0.16f),
                        start = pPrev.position,
                        end = pCurr.position,
                        strokeWidth = if (isDark) 26f else 24f,
                        cap = StrokeCap.Round
                    )
                    // Layer 2: Vivid inner neon aura
                    drawLine(
                        color = segColor.copy(alpha = if (isDark) 0.55f else 0.45f),
                        start = pPrev.position,
                        end = pCurr.position,
                        strokeWidth = if (isDark) 15f else 14f,
                        cap = StrokeCap.Round
                    )
                    // Layer 3: Saturated vibrant core
                    drawLine(
                        color = segColor.copy(alpha = 0.95f),
                        start = pPrev.position,
                        end = pCurr.position,
                        strokeWidth = if (isDark) 8.5f else 9f,
                        cap = StrokeCap.Round
                    )
                    // Layer 4: Laser / highlight center
                    drawLine(
                        color = if (isDark) Color.White.copy(alpha = 0.85f) else Color.hsl(segHue, 1.0f, 0.82f).copy(alpha = 0.80f),
                        start = pPrev.position,
                        end = pCurr.position,
                        strokeWidth = 3f,
                        cap = StrokeCap.Round
                    )
                }
            }

            // 2. Draw glowing comet heads at active touch points
            for ((_, activePt) in activeTouches) {
                val orbColor = Color.hsl(activePt.hue, 1.0f, if (isDark) 0.60f else 0.50f)
                drawCircle(color = orbColor.copy(alpha = 0.35f), radius = 22f, center = activePt.position)
                drawCircle(color = orbColor.copy(alpha = 0.75f), radius = 12f, center = activePt.position)
                drawCircle(color = if (isDark) Color.White.copy(alpha = 0.95f) else Color.hsl(activePt.hue, 1.0f, 0.85f).copy(alpha = 0.95f), radius = 5f, center = activePt.position)
            }

            // 3. Draw magical floating sparkle particles
            for (particle in particles) {
                val pColor = Color.hsl(particle.hue, 1.0f, if (isDark) 0.65f else 0.48f).copy(alpha = particle.alpha)
                drawSparkleStar(
                    center = particle.position,
                    radius = particle.size * particle.alpha,
                    color = pColor,
                    rotation = particle.rotation
                )
                drawCircle(
                    color = if (isDark) Color.White.copy(alpha = particle.alpha * 0.9f) else Color.hsl(particle.hue, 1.0f, 0.80f).copy(alpha = particle.alpha * 0.9f),
                    radius = particle.size * 0.35f * particle.alpha,
                    center = particle.position
                )
            }
        }

        // Welcome prompt cue when pad hasn't been touched yet
        if (!hasInteracted && strokes.isEmpty()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "✨",
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Touch & swirl here to make magic!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isDark) Color(0xFFFFD54F) else Color(0xFF5E35B1) // Warm gold in dark, rich royal purple in light
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Draw glowing rainbow trails with your fingers",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = if (isDark) FontWeight.Normal else FontWeight.Medium
                    ),
                    color = if (isDark) Color(0xFFCE93D8) else Color(0xFF7E57C2) // Soft lilac in dark, vibrant violet in light
                )
            }
        }

        // Clear button to reset the pad for endless doodling
        if (strokes.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isDark) Color.Black.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.92f),
                shadowElevation = if (isDark) 0.dp else 3.dp,
                tonalElevation = if (isDark) 0.dp else 2.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clickable {
                        strokes.clear()
                        activeTouches.clear()
                        particles = emptyList()
                        hasInteracted = false
                    }
            ) {
                Text(
                    text = "Clear 🔄",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isDark) Color(0xFFFF80AB) else Color(0xFFD81B60),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * Draws a whimsical 4-pointed sparkle star centered at [center].
 */
private fun DrawScope.drawSparkleStar(
    center: Offset,
    radius: Float,
    color: Color,
    rotation: Float
) {
    if (radius <= 0.5f) return
    val path = Path()
    val innerRadius = radius * 0.28f
    for (i in 0 until 8) {
        val angle = Math.toRadians((i * 45.0 + rotation)).toFloat()
        val r = if (i % 2 == 0) radius else innerRadius
        val x = center.x + r * kotlin.math.cos(angle)
        val y = center.y + r * kotlin.math.sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path = path, color = color)
}

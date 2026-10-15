package com.draw.onme.presentation.coloring.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin
import kotlin.random.Random

private data class ConfettiPiece(
    val initialX: Float,
    val speedY: Float,
    val color: Color,
    val size: Float,
    val isRibbon: Boolean,
    val swayFreq: Float
)

/**
 * Non-intrusive full-screen celebration overlay for Coloring Book Studio:
 * - Joyful falling confetti particles raining down across the canvas without any background dimming.
 * - Floating congratulatory banner at the top ("Masterpiece Complete! 🎉").
 * - Fully unblocking: allows the child to admire their artwork in full view without modal popups.
 */
@Composable
fun ColoringCelebrationOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400)),
        exit = fadeOut(tween(500)),
        modifier = modifier
    ) {
        val particles = remember {
            val colors = listOf(
                Color(0xFFFF1744), // Crimson
                Color(0xFFFF9100), // Tangerine
                Color(0xFFFFEA00), // Gold
                Color(0xFF00E676), // Spring Green
                Color(0xFF00E5FF), // Electric Cyan
                Color(0xFF2979FF), // Royal Blue
                Color(0xFFD500F9)  // Violet
            )
            (0..65).map {
                ConfettiPiece(
                    initialX = Random.nextFloat(),
                    speedY = Random.nextFloat() * 0.7f + 0.5f,
                    color = colors.random(),
                    size = Random.nextFloat() * 12f + 8f,
                    isRibbon = Random.nextBoolean(),
                    swayFreq = Random.nextFloat() * 6f + 4f
                )
            }
        }

        var progress by remember { mutableFloatStateOf(0f) }
        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(durationMillis = 3500, easing = LinearEasing),
            label = "coloring_confetti"
        )

        LaunchedEffect(visible) {
            progress = if (visible) 1f else 0f
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
        ) {
            // Full-screen transparent confetti canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                for (p in particles) {
                    val y = (p.speedY * animatedProgress * h * 1.2f) % (h + 40f) - 20f
                    val x = p.initialX * w + sin(animatedProgress * p.swayFreq + p.initialX * 10f) * 36f

                    if (p.isRibbon) {
                        drawRect(
                            color = p.color,
                            topLeft = Offset(x, y),
                            size = Size(p.size * 0.6f, p.size * 1.6f)
                        )
                    } else {
                        drawCircle(
                            color = p.color,
                            radius = p.size / 2f,
                            center = Offset(x, y)
                        )
                    }
                }
            }

            // Top Floating Completion Badge
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.96f),
                    tonalElevation = 8.dp,
                    shadowElevation = 10.dp,
                    modifier = Modifier
                        .border(
                            width = 2.dp,
                            color = Color(0xFFFFD54F),
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🎉", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Completed! ⭐",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Every part is colored beautifully!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

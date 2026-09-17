package com.pallav.drawonme.presentation.stencil.drawing.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin
import kotlin.random.Random

private data class ConfettiParticle(
    val initialX: Float,
    val speedY: Float,
    val color: Color,
    val size: Float
)

/**
 * Celebratory particle overlay bursting with stars and confetti when a child completes a drawing.
 */
@Composable
fun CelebrationOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)),
        exit = fadeOut(tween(300)),
        modifier = modifier
    ) {
        val particles = remember {
            val colors = listOf(
                Color(0xFFFF1744),
                Color(0xFFFF9100),
                Color(0xFFFFEA00),
                Color(0xFF00E676),
                Color(0xFF00E5FF),
                Color(0xFFD500F9)
            )
            (0..50).map {
                ConfettiParticle(
                    initialX = Random.nextFloat(),
                    speedY = Random.nextFloat() * 0.6f + 0.4f,
                    color = colors.random(),
                    size = Random.nextFloat() * 12f + 8f
                )
            }
        }

        var progress by remember { mutableFloatStateOf(0f) }
        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(durationMillis = 1800, easing = LinearEasing),
            label = "confetti"
        )

        LaunchedEffect(visible) {
            if (visible) {
                progress = 1f
            } else {
                progress = 0f
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.40f)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                for (p in particles) {
                    val y = (p.speedY * animatedProgress * h) % h
                    val x = p.initialX * w + sin((animatedProgress * 6.0 + p.initialX * 10.0)).toFloat() * 30f
                    drawCircle(
                        color = p.color,
                        radius = p.size / 2f,
                        center = Offset(x, y)
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🌟", fontSize = 54.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "You Did It!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "You drew this all by yourself!\nLook at your amazing masterpiece! 🎨",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Admire My Art ✨", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

package com.draw.onme.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.res.painterResource
import com.draw.onme.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Whimsical animated splash intro screen for young budding artists.
 * Automatically proceeds to Onboarding after 1.8 seconds or immediately on tap.
 *
 * @param onSplashFinished Callback invoked when splash completes or is skipped.
 * @param modifier Optional modifier applied to the root container.
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Automatically advance after 1800ms
    LaunchedEffect(Unit) {
        delay(1800)
        onSplashFinished()
    }

    // Animation drivers
    val logoScale = remember { Animatable(0.2f) }
    val titleAlpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val underlineProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // 1. Pop-in the logo with a playful spring bounce
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        // 2. Fade in title shortly after
        launch {
            delay(250)
            titleAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
            )
        }

        // 3. Draw the rainbow brush underline
        launch {
            delay(400)
            underlineProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing)
            )
        }

        // 4. Fade in subtitle
        launch {
            delay(550)
            subtitleAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
            )
        }
    }

    // Gentle continuous floating animation for background paint bubbles
    val infiniteTransition = rememberInfiniteTransition(label = "floating_bubbles")
    val floatPhase = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val sparkleRotation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkle_rotation"
    )

    // Dark mode adaptive styling
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isDark) Color(0xFF141218) else Color(0xFFFFFDF7)
    val titleColor = if (isDark) Color(0xFFF3EDF7) else Color(0xFF2E2340)
    val subtitleColor = if (isDark) Color(0xFFD0BCFF) else Color(0xFF6750A4)
    val glowColor = if (isDark) Color(0xFFFFB74D).copy(alpha = 0.35f) else Color(0xFFFFE082).copy(alpha = 0.5f)

    val bubbleColors = if (isDark) {
        listOf(
            Color(0xFFFF5252).copy(alpha = 0.35f),
            Color(0xFF40C4FF).copy(alpha = 0.35f),
            Color(0xFFFFD740).copy(alpha = 0.35f),
            Color(0xFF69F0AE).copy(alpha = 0.35f),
            Color(0xFFE040FB).copy(alpha = 0.30f)
        )
    } else {
        listOf(
            Color(0xFFFF8A80).copy(alpha = 0.45f),
            Color(0xFF80D8FF).copy(alpha = 0.45f),
            Color(0xFFFFD54F).copy(alpha = 0.50f),
            Color(0xFFA7FFEB).copy(alpha = 0.50f),
            Color(0xFFEA80FC).copy(alpha = 0.40f)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSplashFinished
            ),
        contentAlignment = Alignment.Center
    ) {
        // Floating decorative background paint bubbles
        FloatingBubble(
            color = bubbleColors[0],
            size = 64.dp,
            baseOffset = IntOffset(-130, -220),
            phaseOffset = 0f,
            currentPhase = floatPhase.value
        )
        FloatingBubble(
            color = bubbleColors[1],
            size = 48.dp,
            baseOffset = IntOffset(140, -180),
            phaseOffset = 1.5f,
            currentPhase = floatPhase.value
        )
        FloatingBubble(
            color = bubbleColors[2],
            size = 56.dp,
            baseOffset = IntOffset(-120, 200),
            phaseOffset = 3f,
            currentPhase = floatPhase.value
        )
        FloatingBubble(
            color = bubbleColors[3],
            size = 52.dp,
            baseOffset = IntOffset(130, 180),
            phaseOffset = 4.5f,
            currentPhase = floatPhase.value
        )
        FloatingBubble(
            color = bubbleColors[4],
            size = 40.dp,
            baseOffset = IntOffset(0, -280),
            phaseOffset = 2f,
            currentPhase = floatPhase.value
        )

        // Center Brand Identity
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // Animated Palette Logo & Sparkles
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(logoScale.value)
                    .size(130.dp)
            ) {
                // Background subtle glow
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(glowColor, Color.Transparent)
                            )
                        )
                )

                // Brand Palette Vector Graphic
                Image(
                    painter = painterResource(R.drawable.ic_splash_logo),
                    contentDescription = "DrawOnMe Logo",
                    modifier = Modifier.size(116.dp)
                )

                // Top right rotating starburst
                Text(
                    text = "✨",
                    fontSize = 28.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-8).dp)
                        .rotate(sparkleRotation.value)
                )

                // Bottom left mini star
                Text(
                    text = "⭐️",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = (-4).dp, y = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Name with warm vibrant typography
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(titleAlpha.value)
            ) {
                Text(
                    text = "DrawOnMe",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    ),
                    color = titleColor
                )

                // Self-drawing rainbow brush underline
                Spacer(modifier = Modifier.height(6.dp))
                Canvas(
                    modifier = Modifier
                        .width(180.dp)
                        .height(12.dp)
                ) {
                    val progress = underlineProgress.value
                    if (progress > 0f) {
                        val path = Path().apply {
                            moveTo(0f, size.height * 0.4f)
                            quadraticTo(
                                size.width * 0.5f,
                                size.height * 1.0f,
                                size.width * progress,
                                size.height * (0.3f + 0.3f * progress)
                            )
                        }
                        val rainbowBrush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFF5252),
                                Color(0xFFFFAB40),
                                Color(0xFFFFEB3B),
                                Color(0xFF69F0AE),
                                Color(0xFF40C4FF),
                                Color(0xFF7C4DFF)
                            )
                        )
                        drawPath(
                            path = path,
                            brush = rainbowBrush,
                            style = Stroke(width = 6f, cap = StrokeCap.Round)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle Tagline
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(subtitleAlpha.value)
            ) {
                Text(
                    text = "Where Little Artists Shine",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = subtitleColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "🌟",
                    fontSize = 16.sp
                )
            }
        }
    }
}

/**
 * Animated decorative floating paint bubble.
 */
@Composable
private fun FloatingBubble(
    color: Color,
    size: androidx.compose.ui.unit.Dp,
    baseOffset: IntOffset,
    phaseOffset: Float,
    currentPhase: Float,
    modifier: Modifier = Modifier
) {
    val dynamicY = (sin(currentPhase + phaseOffset) * 14).toInt()
    val dynamicX = (sin((currentPhase + phaseOffset) * 0.7f) * 10).toInt()

    Box(
        modifier = modifier
            .offset { IntOffset(baseOffset.x + dynamicX, baseOffset.y + dynamicY) }
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

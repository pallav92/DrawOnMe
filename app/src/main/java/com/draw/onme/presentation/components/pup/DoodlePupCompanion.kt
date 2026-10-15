package com.draw.onme.presentation.components.pup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.draw.onme.domain.model.PupMood
import kotlin.math.abs
import kotlin.math.sin

/**
 * Animated Full Body Puppy Companion featuring a lifelike, flexible wagging tail
 * and a comic speech dialog box appearing directly above him displaying identified text.
 */
@Composable
fun FullBodyPuppyWithDialog(
    mood: PupMood,
    dialogText: String? = null,
    onDialogClick: (() -> Unit)? = null,
    onPupClick: (() -> Unit)? = null,
    width: Dp = 106.dp,
    height: Dp = 120.dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Comic Speech Dialog Box directly above the puppy
        AnimatedVisibility(
            visible = !dialogText.isNullOrBlank(),
            enter = fadeIn() + scaleIn(initialScale = 0.82f),
            exit = fadeOut() + scaleOut(targetScale = 0.82f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFB74D)),
                    modifier = Modifier
                        .widthIn(min = 96.dp, max = 240.dp)
                        .clickable(enabled = onDialogClick != null) { onDialogClick?.invoke() }
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dialogText.orEmpty(),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Speech pointer tail pointing straight down to Pup
                Canvas(
                    modifier = Modifier.size(width = 16.dp, height = 9.dp)
                ) {
                    val tailPath = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width * 0.5f, size.height)
                        close()
                    }
                    drawPath(tailPath, color = Color(0xFFFFB74D))
                    val innerTail = Path().apply {
                        moveTo(1.8f, 0f)
                        lineTo(size.width - 1.8f, 0f)
                        lineTo(size.width * 0.5f, size.height - 1.8f)
                        close()
                    }
                    drawPath(innerTail, color = Color(0xFFFFF8E1))
                }
            }
        }

        // Animated Full Body Puppy with Organic Flexible Wagging Tail
        FullBodyPuppy(
            mood = mood,
            width = width,
            height = height,
            modifier = Modifier.clickable(enabled = onPupClick != null) { onPupClick?.invoke() }
        )
    }
}

/**
 * Expressive, high-fidelity cartoon puppy illustration with fluid,
 * dynamic Bezier curve tail wagging physics and animated blinking eyes.
 */
@Composable
fun FullBodyPuppy(
    mood: PupMood,
    width: Dp = 106.dp,
    height: Dp = 120.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pup_organic_anim")

    // Smooth sinusoidal tail wag factor (-1.0f to +1.0f)
    val wagProgress by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (mood == PupMood.EXCITED || mood == PupMood.PROUD) 170 else 550,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tail_wag_progress"
    )

    // Gentle head bob and ear bounce
    val earBounce by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (mood == PupMood.EXCITED) 240 else 1100,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ear_bounce"
    )

    // Eye blinking cycle (closed briefly every 3.2s)
    val blinkProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "blink_cycle"
    )
    val isBlinking = blinkProgress in 0.94f..0.98f

    Canvas(
        modifier = modifier.size(width = width, height = height)
    ) {
        drawLifelikePuppy(
            mood = mood,
            wagFactor = wagProgress,
            earOffset = earBounce,
            isBlinking = isBlinking
        )
    }
}

private fun DrawScope.drawLifelikePuppy(
    mood: PupMood,
    wagFactor: Float,
    earOffset: Float,
    isBlinking: Boolean
) {
    val w = size.width
    val h = size.height

    // Warm, cohesive cartoon color palette
    val goldenFur = Color(0xFFFFB74D)
    val furShadow = Color(0xFFF57C00)
    val earChocolate = Color(0xFF8D4E00)
    val earInner = Color(0xFFB06D2E)
    val muzzleCream = Color(0xFFFFF9E6)
    val noseCharcoal = Color(0xFF262626)
    val tonguePink = Color(0xFFFF5252)
    val collarRuby = Color(0xFFD32F2F)
    val bellGold = Color(0xFFFFD54F)
    val blushGlow = Color(0xFFFF8A80).copy(alpha = 0.45f)

    // Gentle hip sway in counter-phase to tail wagging
    val hipSway = -wagFactor * (w * 0.015f)

    // =========================================================================
    // 1. ORGANIC FLEXIBLE WAGGING TAIL (Curved Bezier with dynamic elastic whip)
    // =========================================================================
    val tailBase = Offset(w * 0.26f + hipSway, h * 0.72f)
    val tailMid = Offset(
        x = w * 0.14f + (wagFactor * w * 0.09f),
        y = h * 0.58f
    )
    val tailTip = Offset(
        x = w * 0.03f + (wagFactor * w * 0.22f),
        y = h * 0.42f - (abs(wagFactor) * h * 0.04f)
    )

    // Outer tail plush curve
    val tailPath = Path().apply {
        moveTo(tailBase.x, tailBase.y)
        quadraticTo(tailMid.x, tailMid.y, tailTip.x, tailTip.y)
    }
    drawPath(
        path = tailPath,
        color = earChocolate,
        style = Stroke(
            width = w * 0.095f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // White fluffy tail tip
    drawCircle(
        color = muzzleCream,
        radius = w * 0.052f,
        center = tailTip
    )

    // =========================================================================
    // 2. REAR HIND LEGS (Sitting Puppy Silhouette)
    // =========================================================================
    // Left sitting hip
    drawOval(
        color = furShadow,
        topLeft = Offset(w * 0.16f + hipSway, h * 0.66f),
        size = Size(w * 0.28f, h * 0.25f)
    )
    drawOval(
        color = goldenFur,
        topLeft = Offset(w * 0.17f + hipSway, h * 0.67f),
        size = Size(w * 0.25f, h * 0.22f)
    )
    // Left rear foot with paw pad
    drawOval(
        color = muzzleCream,
        topLeft = Offset(w * 0.12f + hipSway, h * 0.86f),
        size = Size(w * 0.21f, h * 0.11f)
    )

    // Right rear foot
    drawOval(
        color = muzzleCream,
        topLeft = Offset(w * 0.67f - hipSway, h * 0.86f),
        size = Size(w * 0.21f, h * 0.11f)
    )

    // =========================================================================
    // 3. PUPPY TORSO & CHEST FLUFF
    // =========================================================================
    val torsoRect = Offset(w * 0.27f, h * 0.46f)
    drawOval(
        color = goldenFur,
        topLeft = torsoRect,
        size = Size(w * 0.46f, h * 0.44f)
    )
    // Soft cream chest bib
    drawOval(
        color = muzzleCream,
        topLeft = Offset(w * 0.35f, h * 0.52f),
        size = Size(w * 0.30f, h * 0.34f)
    )

    // =========================================================================
    // 4. FRONT LEGS & PAWS
    // =========================================================================
    // Left front leg
    drawRoundRect(
        color = goldenFur,
        topLeft = Offset(w * 0.35f, h * 0.62f),
        size = Size(w * 0.12f, h * 0.30f),
        cornerRadius = CornerRadius(w * 0.06f)
    )
    // Left front paw with cute toe separations
    drawOval(
        color = muzzleCream,
        topLeft = Offset(w * 0.33f, h * 0.88f),
        size = Size(w * 0.15f, h * 0.09f)
    )
    drawCircle(color = furShadow, radius = w * 0.015f, center = Offset(w * 0.38f, h * 0.92f))
    drawCircle(color = furShadow, radius = w * 0.015f, center = Offset(w * 0.43f, h * 0.92f))

    // Right front leg
    drawRoundRect(
        color = goldenFur,
        topLeft = Offset(w * 0.53f, h * 0.62f),
        size = Size(w * 0.12f, h * 0.30f),
        cornerRadius = CornerRadius(w * 0.06f)
    )
    // Right front paw
    drawOval(
        color = muzzleCream,
        topLeft = Offset(w * 0.52f, h * 0.88f),
        size = Size(w * 0.15f, h * 0.09f)
    )
    drawCircle(color = furShadow, radius = w * 0.015f, center = Offset(w * 0.57f, h * 0.92f))
    drawCircle(color = furShadow, radius = w * 0.015f, center = Offset(w * 0.62f, h * 0.92f))

    // =========================================================================
    // 5. COLLAR WITH GOLDEN BELL
    // =========================================================================
    drawRoundRect(
        color = collarRuby,
        topLeft = Offset(w * 0.32f, h * 0.44f),
        size = Size(w * 0.36f, h * 0.065f),
        cornerRadius = CornerRadius(w * 0.03f)
    )
    // Shiny bell
    drawCircle(
        color = bellGold,
        radius = w * 0.045f,
        center = Offset(w * 0.50f, h * 0.50f)
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.8f),
        radius = w * 0.018f,
        center = Offset(w * 0.485f, h * 0.485f)
    )

    // =========================================================================
    // 6. FLOPPY EARS (Curved teardrops with natural shading)
    // =========================================================================
    // Left ear
    val leftEarPath = Path().apply {
        moveTo(w * 0.28f, h * 0.14f)
        cubicTo(
            w * 0.10f, h * 0.10f + earOffset,
            w * 0.06f, h * 0.36f + earOffset,
            w * 0.22f, h * 0.42f + earOffset
        )
        close()
    }
    drawPath(leftEarPath, color = earChocolate)
    val leftEarInner = Path().apply {
        moveTo(w * 0.26f, h * 0.18f)
        cubicTo(
            w * 0.13f, h * 0.16f + earOffset,
            w * 0.11f, h * 0.34f + earOffset,
            w * 0.22f, h * 0.38f + earOffset
        )
        close()
    }
    drawPath(leftEarInner, color = earInner)

    // Right ear
    val rightEarPath = Path().apply {
        moveTo(w * 0.72f, h * 0.14f)
        cubicTo(
            w * 0.90f, h * 0.10f - earOffset,
            w * 0.94f, h * 0.36f - earOffset,
            w * 0.78f, h * 0.42f - earOffset
        )
        close()
    }
    drawPath(rightEarPath, color = earChocolate)
    val rightEarInner = Path().apply {
        moveTo(w * 0.74f, h * 0.18f)
        cubicTo(
            w * 0.87f, h * 0.16f - earOffset,
            w * 0.89f, h * 0.34f - earOffset,
            w * 0.78f, h * 0.38f - earOffset
        )
        close()
    }
    drawPath(rightEarInner, color = earInner)

    // =========================================================================
    // 7. PUPPY HEAD
    // =========================================================================
    val headCenter = Offset(w * 0.50f, h * 0.27f)
    drawOval(
        color = goldenFur,
        topLeft = Offset(headCenter.x - w * 0.28f, headCenter.y - h * 0.21f),
        size = Size(w * 0.56f, h * 0.42f)
    )
    drawOval(
        color = furShadow,
        topLeft = Offset(headCenter.x - w * 0.28f, headCenter.y - h * 0.21f),
        size = Size(w * 0.56f, h * 0.42f),
        style = Stroke(width = w * 0.016f)
    )

    // =========================================================================
    // 8. PLUSH CREAM SNOUT & WHISKERS
    // =========================================================================
    val muzzleCenter = Offset(w * 0.50f, h * 0.35f)
    drawOval(
        color = muzzleCream,
        topLeft = Offset(muzzleCenter.x - w * 0.20f, muzzleCenter.y - h * 0.11f),
        size = Size(w * 0.40f, h * 0.22f)
    )

    // Soft Rosy Cheeks
    drawOval(
        color = blushGlow,
        topLeft = Offset(w * 0.25f, h * 0.31f),
        size = Size(w * 0.11f, h * 0.07f)
    )
    drawOval(
        color = blushGlow,
        topLeft = Offset(w * 0.64f, h * 0.31f),
        size = Size(w * 0.11f, h * 0.07f)
    )

    // Whiskers dots
    drawCircle(color = earChocolate.copy(alpha = 0.5f), radius = w * 0.008f, center = Offset(w * 0.38f, h * 0.36f))
    drawCircle(color = earChocolate.copy(alpha = 0.5f), radius = w * 0.008f, center = Offset(w * 0.35f, h * 0.38f))
    drawCircle(color = earChocolate.copy(alpha = 0.5f), radius = w * 0.008f, center = Offset(w * 0.62f, h * 0.36f))
    drawCircle(color = earChocolate.copy(alpha = 0.5f), radius = w * 0.008f, center = Offset(w * 0.65f, h * 0.38f))

    // =========================================================================
    // 9. EXPRESSIVE CARTOON EYES
    // =========================================================================
    val eyeY = h * 0.22f
    val leftEyeX = w * 0.38f
    val rightEyeX = w * 0.62f
    val eyeR = w * 0.068f

    if (mood == PupMood.PROUD) {
        // Joyful arched eyes (^ ^)
        drawArc(
            color = noseCharcoal,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(leftEyeX - eyeR, eyeY - eyeR * 0.4f),
            size = Size(eyeR * 2, eyeR * 1.0f),
            style = Stroke(width = w * 0.026f, cap = StrokeCap.Round)
        )
        drawArc(
            color = noseCharcoal,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(rightEyeX - eyeR, eyeY - eyeR * 0.4f),
            size = Size(eyeR * 2, eyeR * 1.0f),
            style = Stroke(width = w * 0.026f, cap = StrokeCap.Round)
        )
    } else if (isBlinking) {
        // Natural blink line
        drawLine(
            color = noseCharcoal,
            start = Offset(leftEyeX - eyeR * 0.8f, eyeY),
            end = Offset(leftEyeX + eyeR * 0.8f, eyeY),
            strokeWidth = w * 0.024f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = noseCharcoal,
            start = Offset(rightEyeX - eyeR * 0.8f, eyeY),
            end = Offset(rightEyeX + eyeR * 0.8f, eyeY),
            strokeWidth = w * 0.024f,
            cap = StrokeCap.Round
        )
    } else {
        // Big sparkling Disney/anime eyes
        drawCircle(color = Color.White, radius = eyeR, center = Offset(leftEyeX, eyeY))
        drawCircle(color = Color.White, radius = eyeR, center = Offset(rightEyeX, eyeY))

        drawCircle(color = noseCharcoal, radius = eyeR * 0.75f, center = Offset(leftEyeX, eyeY))
        drawCircle(color = noseCharcoal, radius = eyeR * 0.75f, center = Offset(rightEyeX, eyeY))

        // Glossy catchlight sparkles
        drawCircle(color = Color.White, radius = eyeR * 0.32f, center = Offset(leftEyeX - eyeR * 0.22f, eyeY - eyeR * 0.22f))
        drawCircle(color = Color.White, radius = eyeR * 0.32f, center = Offset(rightEyeX - eyeR * 0.22f, eyeY - eyeR * 0.22f))
        drawCircle(color = Color.White, radius = eyeR * 0.14f, center = Offset(leftEyeX + eyeR * 0.22f, eyeY + eyeR * 0.18f))
        drawCircle(color = Color.White, radius = eyeR * 0.14f, center = Offset(rightEyeX + eyeR * 0.22f, eyeY + eyeR * 0.18f))
    }

    // =========================================================================
    // 10. HEART NOSE, SMILE & PANTING PINK TONGUE
    // =========================================================================
    // Button nose
    val noseCenter = Offset(w * 0.50f, h * 0.31f)
    drawOval(
        color = noseCharcoal,
        topLeft = Offset(noseCenter.x - w * 0.075f, noseCenter.y - h * 0.045f),
        size = Size(w * 0.15f, h * 0.08f)
    )
    drawOval(
        color = Color.White.copy(alpha = 0.75f),
        topLeft = Offset(noseCenter.x - w * 0.045f, noseCenter.y - h * 0.035f),
        size = Size(w * 0.05f, h * 0.025f)
    )

    // Pink tongue sticking out happily
    val mouthY = h * 0.38f
    if (mood == PupMood.EXCITED || mood == PupMood.PROUD) {
        drawOval(
            color = tonguePink,
            topLeft = Offset(w * 0.45f, mouthY),
            size = Size(w * 0.10f, h * 0.10f)
        )
    }

    // Smiling mouth curves
    val leftSmile = Path().apply {
        moveTo(w * 0.50f, h * 0.35f)
        quadraticTo(w * 0.42f, mouthY + h * 0.015f, w * 0.39f, mouthY)
    }
    drawPath(leftSmile, color = noseCharcoal, style = Stroke(width = w * 0.016f, cap = StrokeCap.Round))

    val rightSmile = Path().apply {
        moveTo(w * 0.50f, h * 0.35f)
        quadraticTo(w * 0.58f, mouthY + h * 0.015f, w * 0.61f, mouthY)
    }
    drawPath(rightSmile, color = noseCharcoal, style = Stroke(width = w * 0.016f, cap = StrokeCap.Round))
}

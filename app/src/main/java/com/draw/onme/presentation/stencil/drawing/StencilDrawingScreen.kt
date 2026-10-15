package com.draw.onme.presentation.stencil.drawing

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.draw.onme.presentation.scribble.model.CanvasTransformState
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.draw.onme.data.repository.InMemoryStencilRepository
import com.draw.onme.domain.model.SavedArtwork
import com.draw.onme.domain.model.Stencil
import com.draw.onme.domain.repository.ArtworkRepository
import com.draw.onme.domain.repository.BoardDraftRepository
import com.draw.onme.domain.repository.StencilRepository
import com.draw.onme.data.evaluator.GeometricStencilEvaluator
import com.draw.onme.domain.evaluator.StencilEvaluator
import com.draw.onme.domain.model.StencilEvaluation
import com.draw.onme.presentation.components.pup.FullBodyPuppyWithDialog
import com.draw.onme.domain.model.PupMood
import com.draw.onme.presentation.scribble.ScribbleAction
import com.draw.onme.presentation.scribble.ScribbleViewModel
import com.draw.onme.presentation.scribble.components.CollapsibleDrawingToolbar
import com.draw.onme.presentation.scribble.components.DrawingToolbar
import com.draw.onme.presentation.scribble.components.ScribbleCanvas
import com.draw.onme.presentation.stencil.drawing.components.CelebrationOverlay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Screen where children follow and trace cartoon character stencils.
 * Features a dashed guideline overlay, Magic Wand celebration, and Fridge pinning.
 *
 * @param stencilId ID of the character stencil to load.
 * @param onNavigateBack Callback to return to the Stencil Gallery.
 * @param modifier Optional modifier applied to the screen root.
 * @param repository Repository supplying available stencils.
 * @param artworkRepository Optional repository for saving artwork to the fridge.
 * @param boardDraftRepository Optional repository for loading and saving board strokes.
 * @param boardId Unique identifier for this stencil board.
 * @param evaluator On-device trace comparison evaluator for coaching feedback.
 * @param viewModel ViewModel managing strokes, colors, undo/redo, and tools.
 */
@Composable
fun StencilDrawingScreen(
    stencilId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    repository: StencilRepository = remember { InMemoryStencilRepository() },
    artworkRepository: ArtworkRepository? = null,
    boardDraftRepository: BoardDraftRepository? = null,
    boardId: String = ScribbleViewModel.stencilBoardId(stencilId),
    evaluator: StencilEvaluator = remember { GeometricStencilEvaluator() },
    doodleClassifier: com.draw.onme.domain.classifier.DoodleClassifier? = null,
    viewModel: ScribbleViewModel = run {
        val context = androidx.compose.ui.platform.LocalContext.current
        val classifier = doodleClassifier ?: androidx.compose.runtime.remember(context) {
            com.draw.onme.data.classifier.DoodleClassifierProvider.get(context)
        }
        viewModel(
            key = "scribble_vm_$boardId",
            factory = ScribbleViewModel.provideFactory(boardId, boardDraftRepository, classifier)
        )
    }
) {
    val allStencils = remember { repository.getStencils() }
    var currentStencilId by remember(stencilId) { mutableStateOf(stencilId) }
    val stencil = remember(currentStencilId, allStencils) {
        allStencils.firstOrNull { it.id == currentStencilId } ?: repository.getStencilById(currentStencilId)
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val tracker = com.draw.onme.presentation.analytics.LocalAnalyticsTracker.current

    var isGuideVisible by remember { mutableStateOf(true) }
    val guideOpacity by remember { mutableFloatStateOf(0.40f) }
    var showCelebration by remember { mutableStateOf(false) }
    var showPinnedToast by remember { mutableStateOf(false) }
    var showPupCoach by remember { mutableStateOf(false) }
    var showPupGreeting by remember { mutableStateOf(false) }
    var activeEvaluation by remember { mutableStateOf<StencilEvaluation?>(null) }
    var recognizedByMlKit by remember { mutableStateOf(false) }

    // Step-by-Step Guided Drawing state: -1 means "show all steps overview"
    var activeStepIndex by remember(currentStencilId) { mutableStateOf(0) }
    val totalSteps = stencil?.paths?.size ?: 1

    // Clamped active step index whenever stencil changes
    val clampedStep = activeStepIndex.coerceIn(0, (totalSteps - 1).coerceAtLeast(0))

    // Calm, rhythmic stroke guide animation (slower, teaches 1 path at a time)
    val stepStrokeAnim = remember { Animatable(0f) }
    LaunchedEffect(currentStencilId, clampedStep, isGuideVisible) {
        if (isGuideVisible) {
            stepStrokeAnim.snapTo(0f)
            stepStrokeAnim.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 2800, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    // Real-time evaluation and ML Kit recognition as child draws
    LaunchedEffect(uiState.strokes) {
        if (stencil != null && uiState.strokes.isNotEmpty()) {
            val eval = evaluator.evaluate(stencil, uiState.strokes, STENCIL_CANVAS_SIZE)
            activeEvaluation = eval

            try {
                val classifier = doodleClassifier ?: com.draw.onme.data.classifier.DoodleClassifierProvider.get(context)
                val predictions = classifier.classify(uiState.strokes)
                val topMatch = predictions.firstOrNull()
                val targetName = stencil.title.lowercase()
                val targetId = stencil.id.lowercase()
                val matched = topMatch != null && (
                    topMatch.category.id.contains(targetId) ||
                    targetId.contains(topMatch.category.id) ||
                    targetName.contains(topMatch.category.displayName.lowercase()) ||
                    topMatch.confidence >= 0.70f
                )
                recognizedByMlKit = matched
            } catch (_: Exception) {
                recognizedByMlKit = false
            }

            if (eval.isGood) {
                showPupCoach = true
            }
        } else {
            activeEvaluation = null
            recognizedByMlKit = false
        }
    }

    LaunchedEffect(showPinnedToast) {
        if (showPinnedToast) {
            delay(2400)
            showPinnedToast = false
        }
    }

    LaunchedEffect(showPupGreeting) {
        if (showPupGreeting) {
            delay(2600)
            showPupGreeting = false
        }
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val isLandscape = width > height

        val scale = if (isLandscape) height * 0.76f else minOf(width, height) * 0.92f
        val offsetX = (width - scale) / 2f
        val offsetY = (height - scale) / 2f
        val targetZoom = if (scale > 0f) scale / STENCIL_CANVAS_SIZE else 1.0f
        val targetPan = Offset(offsetX, offsetY)

        val transformState = remember {
            CanvasTransformState(
                initialZoom = targetZoom,
                initialPan = targetPan
            ).apply {
                viewportSize = IntSize(width.roundToInt(), height.roundToInt())
            }
        }

        val lastSizeRef = remember { object { var size = IntSize.Zero } }
        val currentSize = IntSize(width.roundToInt(), height.roundToInt())
        if (width > 0f && height > 0f && currentSize != lastSizeRef.size) {
            lastSizeRef.size = currentSize
            transformState.setTransform(targetPan, targetZoom)
            transformState.viewportSize = currentSize
        }

        // 1. Drawing Canvas with Step-by-Step Guided Stencil Background
        ScribbleCanvas(
            uiState = uiState,
            onAction = viewModel::onAction,
            modifier = Modifier.fillMaxSize(),
            transformState = transformState,
            showBoundary = false,
            isPanZoomEnabled = true,
            backgroundContent = {
                if (isGuideVisible && stencil != null) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawStepByStepGuide(
                            stencil = stencil,
                            activeStepIndex = clampedStep,
                            opacity = guideOpacity,
                            guideColor = Color(0xFF7E57C2),
                            canvasSize = STENCIL_CANVAS_SIZE,
                            stepProgress = stepStrokeAnim.value
                        )
                    }
                }
            }
        )

        // 2. Clean, Uncluttered Top Bar (Back button on left, Title + Pin/Wand on right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clean isolated Back Button
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.92f),
                tonalElevation = 3.dp,
                shadowElevation = 4.dp
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Gallery",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Top Right Pill: Stencil Title, Guide toggle, Magic Wand, Fridge Pin
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.92f),
                tonalElevation = 3.dp,
                shadowElevation = 4.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = stencil?.let { "${it.iconEmoji} ${it.title}" } ?: "Art School",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // Toggle guide
                    IconButton(
                        onClick = { isGuideVisible = !isGuideVisible },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = if (isGuideVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (isGuideVisible) "Hide Guide" else "Show Guide",
                            tint = if (isGuideVisible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Magic Wand
                    IconButton(
                        onClick = {
                            if (stencil != null && uiState.strokes.isNotEmpty()) {
                                tracker.trackEvent(
                                    "magic_wand_celebration",
                                    mapOf("stencil_id" to currentStencilId, "stroke_count" to uiState.strokes.size)
                                )
                                coroutineScope.launch {
                                    val eval = evaluator.evaluate(stencil, uiState.strokes, STENCIL_CANVAS_SIZE)
                                    activeEvaluation = eval
                                    showPupCoach = true
                                    if (eval.isGood || recognizedByMlKit) {
                                        isGuideVisible = false
                                        showCelebration = true
                                    }
                                }
                            } else {
                                tracker.trackBlankPress("empty_magic_wand", "stencil_drawing")
                            }
                        },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Text(
                            text = "🪄",
                            fontSize = 19.sp,
                            modifier = Modifier.alpha(if (uiState.strokes.isNotEmpty()) 1f else 0.5f)
                        )
                    }

                    // Pin to Fridge
                    if (artworkRepository != null) {
                        val hasStrokes = uiState.strokes.isNotEmpty()
                        IconButton(
                            onClick = {
                                if (hasStrokes) {
                                    tracker.trackEvent(
                                        "pin_to_fridge",
                                        mapOf("stencil_id" to currentStencilId, "stroke_count" to uiState.strokes.size)
                                    )
                                    coroutineScope.launch {
                                        val artwork = SavedArtwork.createCropped(
                                            title = stencil?.title ?: "My Masterpiece",
                                            strokes = uiState.strokes,
                                            stencilId = currentStencilId
                                        )
                                        artworkRepository.saveArtwork(artwork)
                                        showPinnedToast = true
                                    }
                                } else {
                                    tracker.trackBlankPress("empty_canvas_save", "stencil_drawing")
                                }
                            },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Text(
                                text = "📌",
                                fontSize = 17.sp,
                                modifier = Modifier.alpha(if (hasStrokes) 1f else 0.45f)
                            )
                        }
                    }
                }
            }
        }

        // 3. Step Controller Pill (Positioned neatly under top bar, showing Step X of Y + Step Navigation)
        val activePathLabel = stencil?.paths?.getOrNull(clampedStep)?.label ?: "Outline"
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.90f),
            tonalElevation = 2.dp,
            shadowElevation = 3.dp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 62.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                IconButton(
                    onClick = {
                        if (clampedStep > 0) activeStepIndex = clampedStep - 1
                    },
                    enabled = clampedStep > 0,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous Step",
                        modifier = Modifier.size(18.dp),
                        tint = if (clampedStep > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }

                Text(
                    text = "Step ${clampedStep + 1} of $totalSteps: $activePathLabel",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                IconButton(
                    onClick = {
                        if (clampedStep < totalSteps - 1) activeStepIndex = clampedStep + 1
                    },
                    enabled = clampedStep < totalSteps - 1,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next Step",
                        modifier = Modifier.size(18.dp),
                        tint = if (clampedStep < totalSteps - 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
            }
        }

        // 5. Non-overlapping Companion Pup (Positioned on Bottom-Start, elevated above bottom toolbar)
        val isDrawingRecognized = (activeEvaluation?.isGood == true) || recognizedByMlKit
        val stencilText = when {
            recognizedByMlKit && activeEvaluation != null && activeEvaluation!!.isGood -> {
                "Spot on! 🌟"
            }
            recognizedByMlKit -> {
                "I see it! 🎉"
            }
            showPupCoach && activeEvaluation != null -> {
                if (activeEvaluation!!.isGood) {
                    "Spot On! 🌟"
                } else {
                    activeEvaluation!!.suggestedImprovementPart?.let {
                        "Try the $it! ✏️"
                    } ?: "Keep going! 🐾"
                }
            }
            showPupGreeting -> {
                "Step ${clampedStep + 1}! 🐾"
            }
            else -> null
        }

        FullBodyPuppyWithDialog(
            mood = when {
                showCelebration || isDrawingRecognized -> PupMood.PROUD
                showPupCoach && activeEvaluation?.isGood == true -> PupMood.PROUD
                showPupCoach -> PupMood.ENCOURAGING
                else -> PupMood.IDLE
            },
            dialogText = stencilText,
            width = 68.dp,
            height = 76.dp,
            onDialogClick = { showPupCoach = false },
            onPupClick = {
                if (stencil != null && uiState.strokes.isNotEmpty()) {
                    coroutineScope.launch {
                        val eval = evaluator.evaluate(stencil, uiState.strokes, STENCIL_CANVAS_SIZE)
                        activeEvaluation = eval
                        showPupCoach = true
                        if (eval.isGood || recognizedByMlKit) {
                            isGuideVisible = false
                            showCelebration = true
                        }
                    }
                } else {
                    showPupGreeting = true
                }
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(start = 16.dp, bottom = 138.dp)
        )

        // 6. Toast feedback when pinned to fridge
        AnimatedVisibility(
            visible = showPinnedToast,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 96.dp)
        ) {
            Row(
                modifier = Modifier
                    .shadow(6.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF2E7D32))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🖼️", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Pinned to your Fridge!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        // 7. Movable collapsible drawing toolbar (Docks freely, default right side)
        CollapsibleDrawingToolbar(
            uiState = uiState,
            onAction = viewModel::onAction,
            modifier = Modifier.fillMaxSize()
        )

        // 8. Clear confirmation dialog
        if (uiState.showClearDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onAction(ScribbleAction.DismissClearCanvas) },
                title = { Text("Clear Tracing?") },
                text = { Text("This will clear all your drawings on this stencil. The stencil outline will remain.") },
                confirmButton = {
                    TextButton(onClick = { viewModel.onAction(ScribbleAction.ConfirmClearCanvas) }) {
                        Text("Clear")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onAction(ScribbleAction.DismissClearCanvas) }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // 9. Celebratory Confetti Overlay
        CelebrationOverlay(
            visible = showCelebration,
            onDismiss = { showCelebration = false }
        )
    }
}

const val STENCIL_CANVAS_SIZE: Float = 1000f

/**
 * Step-by-Step Drawing Guide Renderer:
 * - Completed / inactive steps are drawn in a subtle, faint lavender guideline.
 * - The CURRENT step is highlighted prominently in primary purple with a dashed stroke.
 * - An animated glowing golden sparkle dot leads the child along the CURRENT step at a calm, natural drawing speed.
 */
private fun DrawScope.drawStepByStepGuide(
    stencil: Stencil,
    activeStepIndex: Int,
    opacity: Float,
    guideColor: Color,
    canvasSize: Float = STENCIL_CANVAS_SIZE,
    stepProgress: Float = 0f
) {
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
    var currentAnimatedPencilOffset: Offset? = null

    for ((index, stencilPath) in stencil.paths.withIndex()) {
        if (stencilPath.points.isEmpty()) continue

        val path = Path()
        val first = stencilPath.points[0]
        path.moveTo(first.x * canvasSize, first.y * canvasSize)

        for (i in 1 until stencilPath.points.size) {
            val pt = stencilPath.points[i]
            path.lineTo(pt.x * canvasSize, pt.y * canvasSize)
        }

        if (stencilPath.isClosed) {
            path.close()
        }

        val isCurrentStep = index == activeStepIndex

        // Faint outline for other parts; vibrant bold outline for the active step
        val strokeColor = if (isCurrentStep) {
            guideColor.copy(alpha = opacity.coerceAtLeast(0.70f))
        } else {
            guideColor.copy(alpha = (opacity * 0.32f).coerceAtLeast(0.12f))
        }

        val strokeWidth = if (isCurrentStep) 4.dp.toPx() else 2.dp.toPx()

        drawPath(
            path = path,
            color = strokeColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
                pathEffect = dashEffect
            )
        )

        // Animate the guiding pencil tip ONLY along the current step path
        if (isCurrentStep && stencilPath.points.size > 1) {
            val totalSegments = stencilPath.points.size - 1
            val exactIndex = stepProgress.coerceIn(0f, 1f) * totalSegments
            val segIdx = exactIndex.toInt().coerceIn(0, totalSegments - 1)
            val segT = exactIndex - segIdx

            val pA = stencilPath.points[segIdx]
            val pB = stencilPath.points[segIdx + 1]
            val curX = (pA.x + (pB.x - pA.x) * segT) * canvasSize
            val curY = (pA.y + (pB.y - pA.y) * segT) * canvasSize
            currentAnimatedPencilOffset = Offset(curX, curY)
        }
    }

    // Draw the animated guiding sparkle dot at the active step pencil point
    currentAnimatedPencilOffset?.let { pos ->
        // Soft glowing halo
        drawCircle(
            color = Color(0xFFFFB300).copy(alpha = 0.35f),
            radius = 15.dp.toPx(),
            center = pos
        )
        // Mid aura
        drawCircle(
            color = Color(0xFFFF9800).copy(alpha = 0.75f),
            radius = 8.dp.toPx(),
            center = pos
        )
        // White core pencil tip
        drawCircle(
            color = Color.White,
            radius = 4.5f.dp.toPx(),
            center = pos
        )
    }
}

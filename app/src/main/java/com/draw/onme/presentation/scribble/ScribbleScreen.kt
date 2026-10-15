package com.draw.onme.presentation.scribble

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import kotlin.math.abs
import kotlin.math.roundToInt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.draw.onme.domain.model.SavedArtwork
import com.draw.onme.domain.repository.ArtworkRepository
import com.draw.onme.domain.repository.BoardDraftRepository
import com.draw.onme.presentation.components.pup.FullBodyPuppyWithDialog
import com.draw.onme.domain.model.PupMood
import com.draw.onme.presentation.scribble.components.CollapsibleDrawingToolbar
import com.draw.onme.presentation.scribble.components.DrawingToolbar
import com.draw.onme.presentation.scribble.components.ScribbleCanvas
import com.draw.onme.presentation.scribble.components.ZoomControlsHud
import com.draw.onme.presentation.scribble.model.CanvasTransformState
import com.draw.onme.presentation.scribble.model.rememberCanvasTransformState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Stateful entry point for the Free Scribble Screen.
 */
@Composable
fun ScribbleScreen(
    modifier: Modifier = Modifier,
    boardId: String = ScribbleViewModel.BOARD_MAGIC_DOODLE,
    onNavigateBack: (() -> Unit)? = null,
    artworkRepository: ArtworkRepository? = null,
    boardDraftRepository: BoardDraftRepository? = null,
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    var showPinnedToast by remember { mutableStateOf(false) }

    LaunchedEffect(showPinnedToast) {
        if (showPinnedToast) {
            delay(2400)
            showPinnedToast = false
        }
    }

    ScribbleScreenContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack,
        onPinToFridge = {
            if (artworkRepository != null && uiState.strokes.isNotEmpty()) {
                coroutineScope.launch {
                    artworkRepository.saveArtwork(
                        SavedArtwork.createCropped(
                            title = "Magic Doodle",
                            strokes = uiState.strokes
                        )
                    )
                    showPinnedToast = true
                }
            }
        },
        showPinnedToast = showPinnedToast,
        modifier = modifier
    )
}

/**
 * Stateless content composable for testing and previews.
 */
@Composable
fun ScribbleScreenContent(
    uiState: ScribbleUiState,
    onAction: (ScribbleAction) -> Unit,
    modifier: Modifier = Modifier,
    transformState: CanvasTransformState = rememberCanvasTransformState(),
    onNavigateBack: (() -> Unit)? = null,
    onPinToFridge: (() -> Unit)? = null,
    showPinnedToast: Boolean = false
) {
    val tracker = com.draw.onme.presentation.analytics.LocalAnalyticsTracker.current
    var showPupGreeting by remember { mutableStateOf(false) }

    LaunchedEffect(showPupGreeting) {
        if (showPupGreeting) {
            delay(2600)
            showPupGreeting = false
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Fullscreen drawing canvas with pan, zoom, and Excalidraw dot grid
        ScribbleCanvas(
            uiState = uiState,
            onAction = onAction,
            transformState = transformState
        )

        // Studio Top Bar (Unified Back + Title + Pin)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.92f),
                tonalElevation = 2.dp,
                shadowElevation = 3.dp
            ) {
                IconButton(
                    onClick = { onNavigateBack?.invoke() },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Home",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.92f),
                tonalElevation = 2.dp,
                shadowElevation = 3.dp
            ) {
                Text(
                    text = "Magic Doodle 🎨",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (onPinToFridge != null) {
                val hasStrokes = uiState.strokes.isNotEmpty()
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(
                        alpha = if (hasStrokes) 0.92f else 0.45f
                    ),
                    tonalElevation = 2.dp,
                    shadowElevation = 3.dp
                ) {
                    IconButton(
                        onClick = {
                            if (hasStrokes) {
                                tracker.trackEvent("pin_to_fridge", mapOf("stroke_count" to uiState.strokes.size))
                                onPinToFridge()
                            } else {
                                tracker.trackBlankPress("empty_canvas_save", "free_scribble")
                            }
                        },
                        modifier = Modifier.size(42.dp)
                    ) {
                        Text(
                            text = "📌",
                            fontSize = 20.sp,
                            modifier = Modifier.alpha(if (hasStrokes) 1f else 0.45f)
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.size(42.dp))
            }
        }

        // Pinned toast feedback
        AnimatedVisibility(
            visible = showPinnedToast,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 64.dp)
        ) {
            Row(
                modifier = Modifier
                    .shadow(6.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF2E7D32))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🖼️", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pinned to your Fridge!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // Full Body Puppy with Wagging Tail and Comic Dialog Box
        val identifiedText = when {
            uiState.isPupGuessVisible && uiState.activePupGuess != null -> {
                "${uiState.activePupGuess.category.displayName} ${uiState.activePupGuess.category.emoji}"
            }
            uiState.pupPraiseMessage != null -> {
                uiState.pupPraiseMessage
            }
            showPupGreeting -> {
                "Woof! 🎨🐾"
            }
            else -> null
        }

        FullBodyPuppyWithDialog(
            mood = uiState.pupMood,
            dialogText = identifiedText,
            onDialogClick = {
                if (uiState.isPupGuessVisible) {
                    onAction(ScribbleAction.ConfirmPupGuess)
                }
            },
            onPupClick = {
                if (uiState.isPupGuessVisible) {
                    onAction(ScribbleAction.ConfirmPupGuess)
                } else {
                    showPupGreeting = true
                }
            },
            width = 72.dp,
            height = 82.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 16.dp, bottom = 138.dp)
        )

        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val maxEraserSize = remember(transformState.viewportSize) {
            if (transformState.viewportSize.width > 0 && transformState.viewportSize.height > 0) {
                minOf(transformState.viewportSize.width.toFloat(), transformState.viewportSize.height.toFloat()) / 3f
            } else {
                360f
            }
        }

        // Transient Zoom Badge (auto-hidden when at 100%, tap to reset)
        val isZoomed = abs(transformState.zoom - 1.0f) > 0.05f || transformState.pan.getDistanceSquared() > 100f
        AnimatedVisibility(
            visible = isZoomed,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 64.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.90f),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { transformState.reset() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${(transformState.zoom * 100f).roundToInt()}%",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "• Reset",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Movable collapsible drawing toolbar: free 2D movement, border snapping
        CollapsibleDrawingToolbar(
            uiState = uiState,
            onAction = onAction,
            maxEraserSize = maxEraserSize,
            modifier = Modifier.fillMaxSize()
        )

        // Clear confirmation dialog
        if (uiState.showClearDialog) {
            AlertDialog(
                onDismissRequest = { onAction(ScribbleAction.DismissClearCanvas) },
                title = { Text("Clear Scribble Board?") },
                text = { Text("This will clear all current drawings on the canvas. You can undo this action afterwards if needed.") },
                confirmButton = {
                    TextButton(onClick = { onAction(ScribbleAction.ConfirmClearCanvas) }) {
                        Text("Clear")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onAction(ScribbleAction.DismissClearCanvas) }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

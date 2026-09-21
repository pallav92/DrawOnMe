package com.pallav.drawonme.presentation.scribble

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pallav.drawonme.domain.model.SavedArtwork
import com.pallav.drawonme.domain.repository.ArtworkRepository
import com.pallav.drawonme.domain.repository.BoardDraftRepository
import com.pallav.drawonme.presentation.scribble.components.CollapsibleDrawingToolbar
import com.pallav.drawonme.presentation.scribble.components.DrawingToolbar
import com.pallav.drawonme.presentation.scribble.components.ScribbleCanvas
import com.pallav.drawonme.presentation.scribble.components.ZoomControlsHud
import com.pallav.drawonme.presentation.scribble.model.CanvasTransformState
import com.pallav.drawonme.presentation.scribble.model.rememberCanvasTransformState
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
    viewModel: ScribbleViewModel = viewModel(
        key = "scribble_vm_$boardId",
        factory = ScribbleViewModel.provideFactory(boardId, boardDraftRepository)
    )
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
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Fullscreen drawing canvas with pan, zoom, and Excalidraw dot grid
        ScribbleCanvas(
            uiState = uiState,
            onAction = onAction,
            transformState = transformState
        )

        // Minimal Top Left pill: Back button + Title
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.92f),
            tonalElevation = 3.dp,
            shadowElevation = 4.dp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 16.dp, top = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(
                    start = if (onNavigateBack != null) 4.dp else 12.dp,
                    end = 14.dp,
                    top = 4.dp,
                    bottom = 4.dp
                )
            ) {
                if (onNavigateBack != null) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = "Magic Doodle 🎨",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        // Minimal Top Right pill: Pin to Fridge button
        if (onPinToFridge != null && uiState.strokes.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.92f),
                tonalElevation = 3.dp,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(end = 16.dp, top = 12.dp)
            ) {
                IconButton(
                    onClick = onPinToFridge,
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                        .size(36.dp)
                ) {
                    Text(text = "📌", fontSize = 20.sp)
                }
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

        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val maxEraserSize = remember(transformState.viewportSize) {
            if (transformState.viewportSize.width > 0 && transformState.viewportSize.height > 0) {
                minOf(transformState.viewportSize.width.toFloat(), transformState.viewportSize.height.toFloat()) / 3f
            } else {
                360f
            }
        }

        if (isLandscape) {
            // Zoom controls HUD tucked neatly in top-left under the minimal top bar pill
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(start = 16.dp, top = 68.dp)
            ) {
                ZoomControlsHud(transformState = transformState)
            }

            // Compact collapsible toolbar anchored at bottom
            CollapsibleDrawingToolbar(
                uiState = uiState,
                onAction = onAction,
                maxEraserSize = maxEraserSize,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
            )
        } else {
            // Floating bottom controls: Zoom HUD & Collapsible Drawing toolbar stacked vertically in portrait
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    ZoomControlsHud(transformState = transformState)
                }

                CollapsibleDrawingToolbar(
                    uiState = uiState,
                    onAction = onAction,
                    maxEraserSize = maxEraserSize
                )
            }
        }

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

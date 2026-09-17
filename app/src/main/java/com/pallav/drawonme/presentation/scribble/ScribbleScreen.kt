package com.pallav.drawonme.presentation.scribble

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pallav.drawonme.domain.model.SavedArtwork
import com.pallav.drawonme.domain.repository.ArtworkRepository
import com.pallav.drawonme.presentation.scribble.components.DrawingToolbar
import com.pallav.drawonme.presentation.scribble.components.ScribbleCanvas
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Stateful entry point for the Free Scribble Screen.
 */
@Composable
fun ScribbleScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    artworkRepository: ArtworkRepository? = null,
    viewModel: ScribbleViewModel = viewModel()
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
                        SavedArtwork(
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
    onNavigateBack: (() -> Unit)? = null,
    onPinToFridge: (() -> Unit)? = null,
    showPinnedToast: Boolean = false
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Fullscreen drawing canvas
        ScribbleCanvas(
            uiState = uiState,
            onAction = onAction
        )

        // Top back button if navigation is enabled
        if (onNavigateBack != null) {
            FilledTonalIconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Home"
                )
            }
        }

        // Top right "Pin to Fridge" button
        if (onPinToFridge != null && uiState.strokes.isNotEmpty()) {
            FilledTonalIconButton(
                onClick = onPinToFridge,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Text(text = "📌", fontSize = 20.sp)
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

        // Floating drawing toolbar anchored to the bottom
        DrawingToolbar(
            uiState = uiState,
            onAction = onAction,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
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

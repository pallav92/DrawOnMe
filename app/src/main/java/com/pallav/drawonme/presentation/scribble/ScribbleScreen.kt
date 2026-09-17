package com.pallav.drawonme.presentation.scribble

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pallav.drawonme.presentation.scribble.components.DrawingToolbar
import com.pallav.drawonme.presentation.scribble.components.ScribbleCanvas

/**
 * Stateful entry point for the Scribble Screen.
 */
@Composable
fun ScribbleScreen(
    modifier: Modifier = Modifier,
    viewModel: ScribbleViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScribbleScreenContent(
        uiState = uiState,
        onAction = viewModel::onAction,
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
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Fullscreen drawing canvas
        ScribbleCanvas(
            uiState = uiState,
            onAction = onAction
        )

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

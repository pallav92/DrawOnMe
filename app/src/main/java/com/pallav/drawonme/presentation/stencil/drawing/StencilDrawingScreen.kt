package com.pallav.drawonme.presentation.stencil.drawing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pallav.drawonme.data.repository.InMemoryStencilRepository
import com.pallav.drawonme.domain.model.SavedArtwork
import com.pallav.drawonme.domain.model.Stencil
import com.pallav.drawonme.domain.repository.ArtworkRepository
import com.pallav.drawonme.domain.repository.BoardDraftRepository
import com.pallav.drawonme.domain.repository.StencilRepository
import com.pallav.drawonme.presentation.scribble.ScribbleAction
import com.pallav.drawonme.presentation.scribble.ScribbleViewModel
import com.pallav.drawonme.presentation.scribble.components.DrawingToolbar
import com.pallav.drawonme.presentation.scribble.components.ScribbleCanvas
import com.pallav.drawonme.presentation.stencil.drawing.components.CelebrationOverlay
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
 * @param viewModel ViewModel managing strokes, colors, undo/redo, and tools.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StencilDrawingScreen(
    stencilId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    repository: StencilRepository = remember { InMemoryStencilRepository() },
    artworkRepository: ArtworkRepository? = null,
    boardDraftRepository: BoardDraftRepository? = null,
    boardId: String = ScribbleViewModel.stencilBoardId(stencilId),
    viewModel: ScribbleViewModel = viewModel(
        key = "scribble_vm_$boardId",
        factory = ScribbleViewModel.provideFactory(boardId, boardDraftRepository)
    )
) {
    val stencil = remember(stencilId) { repository.getStencilById(stencilId) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    var isGuideVisible by remember { mutableStateOf(true) }
    val guideOpacity by remember { mutableFloatStateOf(0.38f) }
    var showCelebration by remember { mutableStateOf(false) }
    var showPinnedToast by remember { mutableStateOf(false) }

    LaunchedEffect(showPinnedToast) {
        if (showPinnedToast) {
            delay(2400)
            showPinnedToast = false
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Drawing canvas with background stencil guide
        ScribbleCanvas(
            uiState = uiState,
            onAction = viewModel::onAction,
            modifier = Modifier.fillMaxSize(),
            backgroundContent = {
                if (isGuideVisible && stencil != null) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawStencilGuide(
                            stencil = stencil,
                            opacity = guideOpacity,
                            guideColor = Color(0xFF7E57C2)
                        )
                    }
                }
            }
        )

        // Top AppBar for Navigation, Magic Wand, Fridge Pin, and Stencil Controls
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = stencil?.let { "${it.iconEmoji} ${it.title}" } ?: "Cartoon Studio",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Gallery"
                    )
                }
            },
            actions = {
                // Magic Wand Reveal & Celebration button
                IconButton(
                    onClick = {
                        isGuideVisible = false
                        showCelebration = true
                    }
                ) {
                    Text(text = "🪄", fontSize = 22.sp)
                }

                // Pin to Fridge button
                if (artworkRepository != null && uiState.strokes.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                val artwork = SavedArtwork(
                                    title = stencil?.title ?: "My Masterpiece",
                                    strokes = uiState.strokes,
                                    stencilId = stencilId
                                )
                                artworkRepository.saveArtwork(artwork)
                                showPinnedToast = true
                            }
                        }
                    ) {
                        Text(text = "📌", fontSize = 20.sp)
                    }
                }

                // Toggle guide visibility
                IconButton(onClick = { isGuideVisible = !isGuideVisible }) {
                    Icon(
                        imageVector = if (isGuideVisible) {
                            Icons.Default.Visibility
                        } else {
                            Icons.Default.VisibilityOff
                        },
                        contentDescription = if (isGuideVisible) {
                            "Hide Stencil Outline"
                        } else {
                            "Show Stencil Outline"
                        },
                        tint = if (isGuideVisible) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
            ),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )

        // Toast feedback when pinned to fridge
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
            onAction = viewModel::onAction,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )

        // Clear confirmation dialog
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

        // Celebratory Confetti and Star Burst Overlay
        CelebrationOverlay(
            visible = showCelebration,
            onDismiss = { showCelebration = false }
        )
    }
}

private fun DrawScope.drawStencilGuide(
    stencil: Stencil,
    opacity: Float,
    guideColor: Color
) {
    val width = size.width
    val height = size.height
    // Keep 1:1 aspect ratio centered on screen, scaled to 92% to boldly fill the canvas
    val scale = minOf(width, height) * 0.92f
    val offsetX = (width - scale) / 2f
    val offsetY = (height - scale) / 2f - 40f

    val strokeColor = guideColor.copy(alpha = opacity)
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)

    for (stencilPath in stencil.paths) {
        if (stencilPath.points.isEmpty()) continue

        val path = Path()
        val first = stencilPath.points[0]
        path.moveTo(offsetX + first.x * scale, offsetY + first.y * scale)

        for (i in 1 until stencilPath.points.size) {
            val pt = stencilPath.points[i]
            path.lineTo(offsetX + pt.x * scale, offsetY + pt.y * scale)
        }

        if (stencilPath.isClosed) {
            path.close()
        }

        drawPath(
            path = path,
            color = strokeColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 2.5.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
                pathEffect = dashEffect
            )
        )
    }
}

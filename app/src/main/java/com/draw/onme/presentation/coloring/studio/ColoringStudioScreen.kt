package com.draw.onme.presentation.coloring.studio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.draw.onme.data.repository.InMemoryColoringBookRepository
import com.draw.onme.domain.model.ColoringArtworkConverter
import com.draw.onme.domain.repository.ArtworkRepository
import com.draw.onme.domain.repository.ColoringBookRepository
import com.draw.onme.domain.repository.ColoringDraftRepository
import com.draw.onme.presentation.coloring.ColoringAction
import com.draw.onme.presentation.coloring.ColoringViewModel
import com.draw.onme.presentation.coloring.ColoringViewModel.Companion.CANONICAL_SIZE
import com.draw.onme.presentation.coloring.components.ColoringCanvas
import androidx.compose.runtime.saveable.rememberSaveable
import com.draw.onme.presentation.coloring.components.ColoringCelebrationOverlay
import com.draw.onme.presentation.coloring.components.ColoringToolbar
import com.draw.onme.presentation.scribble.components.ZoomControlsHud
import com.draw.onme.presentation.scribble.model.CanvasTransformState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Main Coloring Book Studio screen.
 * Hosts the multi-layer coloring canvas, palette toolbar, celebration confetti,
 * and 1-tap pinning to the virtual refrigerator.
 */
@Composable
fun ColoringStudioScreen(
    pageId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    repository: ColoringBookRepository = remember { InMemoryColoringBookRepository() },
    draftRepository: ColoringDraftRepository? = null,
    artworkRepository: ArtworkRepository? = null,
    viewModel: ColoringViewModel = viewModel(
        key = "coloring_vm_$pageId",
        factory = ColoringViewModel.provideFactory(pageId, repository, draftRepository)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val page = uiState.page
    val coroutineScope = rememberCoroutineScope()
    val tracker = com.draw.onme.presentation.analytics.LocalAnalyticsTracker.current

    var showCelebration by remember { mutableStateOf(false) }
    var showPinnedToast by remember { mutableStateOf(false) }
    var hasCelebratedThisSession by rememberSaveable(pageId) { mutableStateOf(false) }

    LaunchedEffect(showPinnedToast) {
        if (showPinnedToast) {
            delay(2400)
            showPinnedToast = false
        }
    }

    // Automatically trigger joyful confetti when all regions of a coloring page are filled!
    LaunchedEffect(uiState.isAllRegionsFilled) {
        if (uiState.isAllRegionsFilled && !hasCelebratedThisSession) {
            hasCelebratedThisSession = true
            showCelebration = true
            tracker.trackEvent("coloring_completed", mapOf("page_id" to pageId))
            delay(4000)
            showCelebration = false
        }
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        val isLandscape = width > height

        val scale = if (isLandscape) height * 0.84f else minOf(width, height) * 0.98f
        val offsetX = (width - scale) / 2f
        val offsetY = (height - scale) / 2f
        val targetZoom = if (scale > 0f) scale / CANONICAL_SIZE else 1.0f
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

        // The multi-layer coloring canvas
        ColoringCanvas(
            uiState = uiState,
            onAction = viewModel::onAction,
            modifier = Modifier.fillMaxSize(),
            transformState = transformState
        )

        // Top Left pill: Back button + Page Title + Done badge
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.92f),
            tonalElevation = 3.dp,
            shadowElevation = 4.dp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 16.dp, top = 12.dp)
                // Leave room for the Fridge pill on the right so a long page title (plus its
                // "Done" badge) truncates instead of growing underneath/into it.
                .widthIn(max = maxWidth - 152.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, end = 14.dp, top = 4.dp, bottom = 4.dp)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Gallery",
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = page?.let { "${it.iconEmoji} ${it.title}" } ?: "Coloring Studio",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (uiState.isAllRegionsFilled) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF2E7D32).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "✓ Done",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Transient Zoom Badge (auto-hidden when at default view, tap to reset)
        val isZoomed = kotlin.math.abs(transformState.zoom - targetZoom) > 0.05f || (transformState.pan - targetPan).getDistanceSquared() > 100f
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
                    .clickable { transformState.setTransform(targetPan, targetZoom) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val percent = if (targetZoom > 0f) ((transformState.zoom / targetZoom) * 100f).roundToInt() else 100
                    Text(
                        text = "$percent%",
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

        // Top Right: Save to Fridge (Virtual Fridge Gallery)
        if (artworkRepository != null && page != null) {
            val hasProgress = uiState.fills.isNotEmpty() || uiState.customFills.isNotEmpty() || uiState.strokes.isNotEmpty()
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(
                            enabled = hasProgress,
                            onClick = {
                                tracker.trackEvent("coloring_pin_to_fridge", mapOf("page_id" to pageId))
                                coroutineScope.launch {
                                    val artwork = ColoringArtworkConverter.toSavedArtwork(
                                        page = page,
                                        fills = uiState.fills,
                                        freehandStrokes = uiState.strokes,
                                        customFills = uiState.customFills
                                    )
                                    artworkRepository.saveArtwork(artwork)
                                    showPinnedToast = true
                                }
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = "🖼️", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Fridge",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (hasProgress) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        }

        // Pinned to Fridge Toast Feedback
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

        // Bottom Coloring Toolbar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        ) {
            ColoringToolbar(
                uiState = uiState,
                onAction = viewModel::onAction
            )
        }

        // Clear confirmation dialog
        if (uiState.showClearDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onAction(ColoringAction.DismissClear) },
                title = { Text("Clear Coloring?") },
                text = { Text("This will reset all your colors and crayon drawings on this page.") },
                confirmButton = {
                    TextButton(onClick = { viewModel.onAction(ColoringAction.ConfirmClear) }) {
                        Text("Clear", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onAction(ColoringAction.DismissClear) }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Non-intrusive full-screen falling confetti and completion banner
        ColoringCelebrationOverlay(
            visible = showCelebration,
            onDismiss = { showCelebration = false }
        )
    }
}

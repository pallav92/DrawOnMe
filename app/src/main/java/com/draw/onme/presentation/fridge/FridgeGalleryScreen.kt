package com.draw.onme.presentation.fridge

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import com.draw.onme.presentation.fridge.export.ArtworkImageExporter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.draw.onme.domain.model.DrawingTool
import com.draw.onme.domain.model.SavedArtwork
import com.draw.onme.domain.model.Stroke as DomainStroke
import com.draw.onme.domain.repository.ArtworkRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Screen displaying the child's virtual refrigerator door with pinned drawings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FridgeGalleryScreen(
    repository: ArtworkRepository,
    onNavigateBack: () -> Unit,
    onStartNewDrawing: () -> Unit,
    modifier: Modifier = Modifier
) {
    val artworks by repository.observeArtworks().collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val tracker = com.draw.onme.presentation.analytics.LocalAnalyticsTracker.current

    var artworkToDelete by remember { mutableStateOf<SavedArtwork?>(null) }
    var previewArtwork by remember { mutableStateOf<SavedArtwork?>(null) }
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (artworks.isEmpty()) {
            // Empty fridge state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null
                    ) {
                        tracker.trackBlankPress("empty_fridge_tap", "fridge_gallery")
                    }
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "🧊", fontSize = 64.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "The Fridge is Empty!",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Draw something and tap 'Pin to Fridge' to hang your masterpiece here!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onStartNewDrawing,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Create Art Now 🎨", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 68.dp,
                    bottom = 24.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                items(
                    items = artworks,
                    key = { it.id }
                ) { artwork ->
                    PinnedArtworkCard(
                        artwork = artwork,
                        onClick = { previewArtwork = artwork },
                        onShare = {
                            tracker.trackShare(artwork.id, artwork.stencilId != null, artwork.strokes.size)
                            coroutineScope.launch {
                                ArtworkImageExporter.shareArtwork(context, artwork)
                            }
                        },
                        onSave = {
                            tracker.trackSaveToGallery(artwork.id, artwork.stencilId != null, artwork.strokes.size)
                            coroutineScope.launch {
                                val result = ArtworkImageExporter.saveToGallery(context, artwork)
                                if (result.isSuccess) {
                                    Toast.makeText(context, "Saved to Gallery! 🖼️", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Could not save to Gallery", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onDelete = { artworkToDelete = artwork }
                    )
                }
            }
        }

        // Minimal Top Left pill: Back button + Screen Title
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
                modifier = Modifier.padding(start = 4.dp, end = 14.dp, top = 4.dp, bottom = 4.dp)
            ) {
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
                Text(
                    text = "My Fridge Door 🖼️",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        // Minimal Top Right pill: New Drawing button (when artworks is not empty)
        if (artworks.isNotEmpty()) {
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
                        .clickable(onClick = onStartNewDrawing)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "🎨 New Drawing",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Delete confirmation dialog
        artworkToDelete?.let { target ->
            AlertDialog(
                onDismissRequest = { artworkToDelete = null },
                title = { Text("Remove from Fridge?") },
                text = { Text("Are you sure you want to unpin '${target.title}' from the fridge?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                repository.deleteArtwork(target.id)
                                artworkToDelete = null
                            }
                        }
                    ) {
                        Text("Remove", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { artworkToDelete = null }) {
                        Text("Keep")
                    }
                }
            )
        }

        // Fullscreen preview and share/save/print dialog
        previewArtwork?.let { target ->
            ArtworkShareDialog(
                artwork = target,
                onDismiss = { previewArtwork = null },
                onShare = {
                    tracker.trackShare(target.id, target.stencilId != null, target.strokes.size)
                    coroutineScope.launch {
                        ArtworkImageExporter.shareArtwork(context, target)
                    }
                },
                onSaveToGallery = {
                    tracker.trackSaveToGallery(target.id, target.stencilId != null, target.strokes.size)
                    coroutineScope.launch {
                        val result = ArtworkImageExporter.saveToGallery(context, target)
                        if (result.isSuccess) {
                            Toast.makeText(context, "Saved to Gallery! 🖼️", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Could not save to Gallery", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onPrint = {
                    tracker.trackPrint(target.id, target.stencilId != null, target.strokes.size)
                    ArtworkImageExporter.printArtwork(context, target)
                },
                isLandscape = isLandscape
            )
        }
    }
}

@Composable
private fun ArtworkShareDialog(
    artwork: SavedArtwork,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onSaveToGallery: () -> Unit,
    onPrint: () -> Unit,
    isLandscape: Boolean
) {
    val dateString = remember(artwork.createdAt) {
        val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        formatter.format(Date(artwork.createdAt))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = artwork.magnetEmoji,
                    fontSize = 28.sp,
                    modifier = Modifier.padding(end = 10.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = artwork.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // High-resolution framed paper card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    tonalElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isLandscape) 180.dp else 280.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    ArtworkThumbnailCanvas(
                        strokes = artwork.strokes,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Save this drawing to your device gallery, share it with family, or print a physical keepsake!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onPrint,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Print")
                }

                OutlinedButton(
                    onClick = onSaveToGallery,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save")
                }

                Button(
                    onClick = onShare,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun PinnedArtworkCard(
    artwork: SavedArtwork,
    onClick: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    onSave: (() -> Unit)? = null
) {
    val dateString = remember(artwork.createdAt) {
        val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        formatter.format(Date(artwork.createdAt))
    }

    val visibleStrokes = remember(artwork.strokes) {
        artwork.strokes.filter { it.tool != DrawingTool.ERASER && it.points.isNotEmpty() }
    }

    val artworkAspectRatio = remember(visibleStrokes) {
        if (visibleStrokes.isEmpty()) {
            1.2f
        } else {
            var minX = Float.MAX_VALUE
            var maxX = -Float.MAX_VALUE
            var minY = Float.MAX_VALUE
            var maxY = -Float.MAX_VALUE
            for (s in visibleStrokes) {
                val hw = s.strokeWidth / 2f
                for (p in s.points) {
                    if (p.x - hw < minX) minX = p.x - hw
                    if (p.x + hw > maxX) maxX = p.x + hw
                    if (p.y - hw < minY) minY = p.y - hw
                    if (p.y + hw > maxY) maxY = p.y + hw
                }
            }
            val spanX = (maxX - minX).coerceAtLeast(1f)
            val spanY = (maxY - minY).coerceAtLeast(1f)
            (spanX / spanY).coerceIn(0.85f, 1.45f)
        }
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp)
                .shadow(4.dp, RoundedCornerShape(18.dp))
                .clip(RoundedCornerShape(18.dp))
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Drawing Canvas Preview with artwork-adaptive aspect ratio
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(artworkAspectRatio)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFAFAFA))
                ) {
                    ArtworkThumbnailCanvas(
                        strokes = artwork.strokes,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = artwork.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFF1E1E1E),
                            maxLines = 1
                        )
                        Text(
                            text = dateString,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        if (onSave != null) {
                            IconButton(
                                onClick = onSave,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "Save to Gallery",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = onShare,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Artwork",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove Artwork",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Cute Fruit/Star Magnet holding the card on top
        Box(
            modifier = Modifier
                .size(32.dp)
                .offset(y = 0.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = artwork.magnetEmoji,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun ArtworkThumbnailCanvas(
    strokes: List<DomainStroke>,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    ) {
        val visibleStrokes = strokes.filter { it.tool != DrawingTool.ERASER && it.points.isNotEmpty() }
        if (visibleStrokes.isEmpty()) return@Canvas

        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        // Calculate bounding box of VISIBLE stroke points (ignore erasers so erasers never expand bounds)
        var minX = Float.MAX_VALUE
        var maxX = -Float.MAX_VALUE
        var minY = Float.MAX_VALUE
        var maxY = -Float.MAX_VALUE

        for (stroke in visibleStrokes) {
            val halfWidth = stroke.strokeWidth / 2f
            for (p in stroke.points) {
                if (p.x - halfWidth < minX) minX = p.x - halfWidth
                if (p.x + halfWidth > maxX) maxX = p.x + halfWidth
                if (p.y - halfWidth < minY) minY = p.y - halfWidth
                if (p.y + halfWidth > maxY) maxY = p.y + halfWidth
            }
        }

        val strokeSpanX = (maxX - minX).coerceAtLeast(1f)
        val strokeSpanY = (maxY - minY).coerceAtLeast(1f)

        // Scale to fill 90% of thumbnail dimensions
        val scale = minOf((w * 0.90f) / strokeSpanX, (h * 0.90f) / strokeSpanY)
        val centerOffsetX = (w - strokeSpanX * scale) / 2f - minX * scale
        val centerOffsetY = (h - strokeSpanY * scale) / 2f - minY * scale

        for (stroke in strokes) {
            if (stroke.points.isEmpty()) continue

            val isEraser = stroke.tool == DrawingTool.ERASER
            val drawColor = if (isEraser) Color.Transparent else Color(stroke.color.argb)
            val blendMode = if (isEraser) BlendMode.Clear else BlendMode.SrcOver
            val strokeWidth = (stroke.strokeWidth * scale).coerceIn(2.5f, 18f)

            if (stroke.points.size == 1) {
                val pt = stroke.points[0]
                drawCircle(
                    color = drawColor,
                    radius = strokeWidth / 2f,
                    center = Offset(pt.x * scale + centerOffsetX, pt.y * scale + centerOffsetY),
                    blendMode = blendMode
                )
            } else {
                val path = Path()
                val first = stroke.points[0]
                path.moveTo(first.x * scale + centerOffsetX, first.y * scale + centerOffsetY)

                for (i in 1 until stroke.points.size) {
                    val prev = stroke.points[i - 1]
                    val curr = stroke.points[i]
                    val prevX = prev.x * scale + centerOffsetX
                    val prevY = prev.y * scale + centerOffsetY
                    val currX = curr.x * scale + centerOffsetX
                    val currY = curr.y * scale + centerOffsetY
                    val midX = (prevX + currX) / 2f
                    val midY = (prevY + currY) / 2f
                    path.quadraticTo(prevX, prevY, midX, midY)
                }
                val last = stroke.points.last()
                path.lineTo(last.x * scale + centerOffsetX, last.y * scale + centerOffsetY)

                drawPath(
                    path = path,
                    color = drawColor,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    ),
                    blendMode = blendMode
                )
            }
        }
    }
}

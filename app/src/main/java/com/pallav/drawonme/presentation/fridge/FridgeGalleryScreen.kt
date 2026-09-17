package com.pallav.drawonme.presentation.fridge

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pallav.drawonme.domain.model.DrawingTool
import com.pallav.drawonme.domain.model.SavedArtwork
import com.pallav.drawonme.domain.model.Stroke as DomainStroke
import com.pallav.drawonme.domain.repository.ArtworkRepository
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
    val artworks by repository.observeArtworks().collectAsStateWithLifecycle(initialValue = emptyList())
    val coroutineScope = rememberCoroutineScope()

    var artworkToDelete by remember { mutableStateOf<SavedArtwork?>(null) }
    var previewArtwork by remember { mutableStateOf<SavedArtwork?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "My Fridge Door 🖼️",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        if (artworks.isEmpty()) {
            // Empty fridge state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
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
                    top = innerPadding.calculateTopPadding() + 8.dp,
                    bottom = innerPadding.calculateBottomPadding() + 24.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = artworks,
                    key = { it.id }
                ) { artwork ->
                    PinnedArtworkCard(
                        artwork = artwork,
                        onClick = { previewArtwork = artwork },
                        onDelete = { artworkToDelete = artwork }
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

        // Fullscreen preview dialog
        previewArtwork?.let { target ->
            AlertDialog(
                onDismissRequest = { previewArtwork = null },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(target.title, fontWeight = FontWeight.Bold)
                        Text(target.magnetEmoji, fontSize = 24.sp)
                    }
                },
                text = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                    ) {
                        ArtworkThumbnailCanvas(
                            strokes = target.strokes,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = { previewArtwork = null }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
private fun PinnedArtworkCard(
    artwork: SavedArtwork,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateString = remember(artwork.createdAt) {
        val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        formatter.format(Date(artwork.createdAt))
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
                // Drawing Canvas Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
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
    Canvas(modifier = modifier) {
        if (strokes.isEmpty()) return@Canvas

        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        // Calculate bounding box of all stroke points to scale the preview nicely
        var minX = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var minY = Float.MAX_VALUE
        var maxY = Float.MIN_VALUE

        for (stroke in strokes) {
            for (p in stroke.points) {
                if (p.x < minX) minX = p.x
                if (p.x > maxX) maxX = p.x
                if (p.y < minY) minY = p.y
                if (p.y > maxY) maxY = p.y
            }
        }

        val strokeSpanX = (maxX - minX).coerceAtLeast(1f)
        val strokeSpanY = (maxY - minY).coerceAtLeast(1f)

        val scale = minOf(w * 0.85f / strokeSpanX, h * 0.85f / strokeSpanY)
        val centerOffsetX = (w - strokeSpanX * scale) / 2f - minX * scale
        val centerOffsetY = (h - strokeSpanY * scale) / 2f - minY * scale

        for (stroke in strokes) {
            if (stroke.tool == DrawingTool.ERASER || stroke.points.isEmpty()) continue

            val drawColor = Color(stroke.color.argb)
            val strokeWidth = (stroke.strokeWidth * scale * 0.35f).coerceAtLeast(2f)

            if (stroke.points.size == 1) {
                val pt = stroke.points[0]
                drawCircle(
                    color = drawColor,
                    radius = strokeWidth / 2f,
                    center = Offset(pt.x * scale + centerOffsetX, pt.y * scale + centerOffsetY)
                )
            } else {
                val path = Path()
                val first = stroke.points[0]
                path.moveTo(first.x * scale + centerOffsetX, first.y * scale + centerOffsetY)

                for (i in 1 until stroke.points.size) {
                    val p = stroke.points[i]
                    path.lineTo(p.x * scale + centerOffsetX, p.y * scale + centerOffsetY)
                }

                drawPath(
                    path = path,
                    color = drawColor,
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}

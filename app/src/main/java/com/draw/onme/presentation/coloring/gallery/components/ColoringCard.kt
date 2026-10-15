package com.draw.onme.presentation.coloring.gallery.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.draw.onme.domain.model.ColoringDraft
import com.draw.onme.domain.model.ColoringPage
import com.draw.onme.domain.model.PointInPolygon

/**
 * Thread-safe LRU cache storing scaled Compose [Path] instances for coloring page thumbnails.
 * Prevents allocating thousands of native Skia Path objects on every frame during scrolling.
 */
private object OutlinePathCache {
    private const val MAX_ENTRIES = 64
    private val cache = object : LinkedHashMap<String, List<Path>>(MAX_ENTRIES, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, List<Path>>?): Boolean {
            return size > MAX_ENTRIES
        }
    }

    @Synchronized
    fun getOrPut(key: String, defaultValue: () -> List<Path>): List<Path> {
        return cache.getOrPut(key, defaultValue)
    }

    @Synchronized
    fun clear() {
        cache.clear()
    }
}

/**
 * High-performance Card displayed in the Coloring Book Gallery presenting a template preview.
 * Displays the artwork rendered in the user's filled colors from [draft], a "✓ Done" completion
 * badge when all parts are colored, and a "Color Again 🔄" action to reset and color from scratch.
 */
@Composable
fun ColoringCard(
    page: ColoringPage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    draft: ColoringDraft? = null,
    onRetry: (() -> Unit)? = null
) {
    val isDark = isSystemInDarkTheme()
    val thumbnailBgColor = MaterialTheme.colorScheme.surface
    val outlineColor = if (isDark) {
        Color(0xFFECEFF1)
    } else {
        Color(0xFF37474F)
    }

    val isCompleted = page.regions.isNotEmpty() && (draft?.fills?.size ?: 0) >= page.regions.size
    val hasFills = (draft?.fills?.isNotEmpty() == true) || (draft?.strokes?.isNotEmpty() == true)
    val sortedRegions = remember(page) {
        page.regions.sortedByDescending { PointInPolygon.calculateArea(it.boundaryPoints) }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Preview thumbnail of colored regions and line-art outlines
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.15f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(thumbnailBgColor)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = if (isDark) 0.45f else 0.2f),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (page.outlines.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(text = "🖍️", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Free Draw",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Spacer(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .drawWithCache {
                                val w = size.width
                                val h = size.height
                                val strokeStyle = Stroke(
                                    width = 2.5.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )

                                // Build region paths for filled areas
                                val regionPaths = if (draft != null && draft.fills.isNotEmpty()) {
                                    page.regions.associate { region ->
                                        val path = Path()
                                        if (region.boundaryPoints.isNotEmpty()) {
                                            path.moveTo(region.boundaryPoints[0].x * w, region.boundaryPoints[0].y * h)
                                            for (i in 1 until region.boundaryPoints.size) {
                                                val pt = region.boundaryPoints[i]
                                                path.lineTo(pt.x * w, pt.y * h)
                                            }
                                            path.close()
                                        }
                                        region.id to path
                                    }
                                } else emptyMap()

                                val cacheKey = "${page.id}_${w.toInt()}_${h.toInt()}"
                                val outlinePaths = OutlinePathCache.getOrPut(cacheKey) {
                                    page.outlines.mapNotNull { outline ->
                                        if (outline.points.isEmpty()) return@mapNotNull null
                                        val path = Path()
                                        val first = outline.points[0]
                                        path.moveTo(first.x * w, first.y * h)

                                        for (i in 1 until outline.points.size) {
                                            val pt = outline.points[i]
                                            path.lineTo(pt.x * w, pt.y * h)
                                        }

                                        if (outline.isClosed) {
                                            path.close()
                                        }
                                        path
                                    }
                                }

                                onDrawBehind {
                                    // Layer 1: Filled regions from user's draft (drawn largest to smallest)
                                    if (draft != null && draft.fills.isNotEmpty()) {
                                        for (region in sortedRegions) {
                                            val fillColor = draft.fills[region.id] ?: continue
                                            val path = regionPaths[region.id] ?: continue
                                            drawPath(
                                                path = path,
                                                color = Color(fillColor.argb),
                                                style = Fill
                                            )
                                        }
                                    }

                                    // Layer 2: Line-art outlines on top
                                    for (path in outlinePaths) {
                                        drawPath(
                                            path = path,
                                            color = outlineColor,
                                            style = strokeStyle
                                        )
                                    }
                                }
                            }
                    )
                }

                // Completion or In-Progress Badge in top-left corner
                if (isCompleted) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF2E7D32),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "⭐", fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Done",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                } else if (hasFills && page.regions.isNotEmpty()) {
                    val filledCount = draft?.fills?.size ?: 0
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "$filledCount/${page.regions.size}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Emoji Badge in top-right corner
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = page.iconEmoji, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & region count
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = page.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isCompleted) Color(0xFF2E7D32).copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Text(
                        text = when {
                            page.regions.isEmpty() -> "Free Draw"
                            isCompleted -> "Completed"
                            else -> "${page.regions.size} parts"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isCompleted) Color(0xFF2E7D32)
                        else MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Retry / Color Again action when page has fills
            if (hasFills && onRetry != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        modifier = Modifier.clickable { onRetry() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Color Again",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Color Again",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

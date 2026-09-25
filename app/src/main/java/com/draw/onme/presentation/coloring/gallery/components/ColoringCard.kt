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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.draw.onme.domain.model.ColoringPage

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
 * Employs [Modifier.drawWithCache] and [OutlinePathCache] to guarantee 60/120 FPS jank-free scrolling.
 */
@Composable
fun ColoringCard(
    page: ColoringPage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val thumbnailBgColor = MaterialTheme.colorScheme.surface
    val outlineColor = if (isDark) {
        Color(0xFFECEFF1)
    } else {
        Color(0xFF37474F)
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
            // Preview thumbnail of line-art outlines
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

                                val cacheKey = "${page.id}_${w.toInt()}_${h.toInt()}"
                                val paths = OutlinePathCache.getOrPut(cacheKey) {
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
                                    for (path in paths) {
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
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Text(
                        text = if (page.regions.isEmpty()) "Free Draw" else "${page.regions.size} parts",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

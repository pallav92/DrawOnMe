package com.draw.onme.domain.model

import java.util.UUID

/**
 * Geometric bounds for a collection of drawing strokes and vector graphics.
 */
data class ArtworkBounds(
    val minX: Float,
    val maxX: Float,
    val minY: Float,
    val maxY: Float
) {
    val width: Float get() = (maxX - minX).coerceAtLeast(1f)
    val height: Float get() = (maxY - minY).coerceAtLeast(1f)
    val aspectRatio: Float get() = width / height
}

/**
 * Represents a filled vector polygon (e.g. from coloring book regions).
 * Coordinates are normalized to canvas space (matching Stroke coordinates).
 *
 * @property points Closed polygon vertices.
 * @property color The fill color.
 */
data class VectorFill(
    val points: List<Point>,
    val color: StrokeColor
)

/**
 * Represents a crisp vector outline path (e.g. from coloring book line art).
 * Coordinates are normalized to canvas space (matching Stroke coordinates).
 * Rendered with line segments (not Bézier-smoothed) to preserve sharp geometric corners.
 *
 * @property points Ordered vertices along the outline path.
 * @property strokeWidth Stroke thickness in canvas units.
 * @property isClosed Whether the path closes back to the start point.
 * @property color Outline color (default black).
 */
data class VectorOutline(
    val points: List<Point>,
    val strokeWidth: Float = 4f,
    val isClosed: Boolean = true,
    val color: StrokeColor = StrokeColor.Black
)

/**
 * Represents an artwork saved by the child or parent to the virtual refrigerator.
 *
 * @property id Unique identifier.
 * @property title Friendly title (e.g. "My Cozy House" or "Magic Doodle").
 * @property createdAt Epoch millisecond timestamp when the artwork was pinned.
 * @property strokes Immutable list of drawing strokes forming the artwork.
 * @property stencilId Optional ID of the stencil traced, if created in Stencil Studio or Coloring Book.
 * @property magnetEmoji Fun magnet holding the artwork on the fridge (e.g. "⭐️", "🍎", "🚀", "🌸").
 * @property vectorFills Vector polygon fills (e.g. from coloring book pages) rendered with exact geometric bounds.
 * @property vectorOutlines Crisp vector outline paths rendered with straight line segments to preserve sharp corners.
 */
data class SavedArtwork(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val strokes: List<Stroke>,
    val stencilId: String? = null,
    val magnetEmoji: String = DefaultMagnets.random(),
    val vectorFills: List<VectorFill> = emptyList(),
    val vectorOutlines: List<VectorOutline> = emptyList()
) {
    companion object {
        val DefaultMagnets: List<String> = listOf("⭐️", "🍎", "🚀", "🌸", "🍌", "🌈", "🐱", "🎈")

        /**
         * Creates a [SavedArtwork] with strokes and vector shapes cropped tightly to their visible bounding box.
         * Normalizes coordinates to origin (0, 0) plus padding, eliminating dead canvas space.
         */
        fun createCropped(
            title: String,
            strokes: List<Stroke>,
            stencilId: String? = null,
            id: String = UUID.randomUUID().toString(),
            createdAt: Long = System.currentTimeMillis(),
            magnetEmoji: String = DefaultMagnets.random(),
            padding: Float = ArtworkCropper.DEFAULT_PADDING,
            vectorFills: List<VectorFill> = emptyList(),
            vectorOutlines: List<VectorOutline> = emptyList()
        ): SavedArtwork {
            val cropped = ArtworkCropper.cropArtworkContent(
                strokes = strokes,
                vectorFills = vectorFills,
                vectorOutlines = vectorOutlines,
                padding = padding
            )
            val hasContent = cropped.strokes.isNotEmpty() ||
                cropped.vectorFills.isNotEmpty() ||
                cropped.vectorOutlines.isNotEmpty()

            return SavedArtwork(
                id = id,
                title = title,
                createdAt = createdAt,
                strokes = if (hasContent) cropped.strokes else strokes,
                stencilId = stencilId,
                magnetEmoji = magnetEmoji,
                vectorFills = if (hasContent) cropped.vectorFills else vectorFills,
                vectorOutlines = if (hasContent) cropped.vectorOutlines else vectorOutlines
            )
        }
    }
}

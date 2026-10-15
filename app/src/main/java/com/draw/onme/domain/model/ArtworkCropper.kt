package com.draw.onme.domain.model

/**
 * Container for cropped strokes and vector graphics after bounding-box normalization.
 */
data class CroppedArtworkContent(
    val strokes: List<Stroke>,
    val vectorFills: List<VectorFill> = emptyList(),
    val vectorOutlines: List<VectorOutline> = emptyList()
)

/**
 * Utility for cropping and normalizing drawing strokes and vector graphics to their visible bounding box.
 * Discards empty space surrounding the artwork and normalizes coordinates to origin (0, 0),
 * ensuring landscape or small artworks are saved tightly without canvas dead space.
 */
object ArtworkCropper {

    const val DEFAULT_PADDING: Float = 24f

    /**
     * Calculates the bounding box of all visible (non-eraser) strokes, vector fills,
     * and vector outlines. Returns null if there are no visible elements.
     */
    fun calculateBounds(
        strokes: List<Stroke> = emptyList(),
        vectorFills: List<VectorFill> = emptyList(),
        vectorOutlines: List<VectorOutline> = emptyList()
    ): ArtworkBounds? {
        val visibleStrokes = strokes.filter { it.tool != DrawingTool.ERASER && it.points.isNotEmpty() }
        val validFills = vectorFills.filter { it.points.isNotEmpty() }
        val validOutlines = vectorOutlines.filter { it.points.isNotEmpty() }

        if (visibleStrokes.isEmpty() && validFills.isEmpty() && validOutlines.isEmpty()) {
            return null
        }

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

        for (fill in validFills) {
            for (p in fill.points) {
                if (p.x < minX) minX = p.x
                if (p.x > maxX) maxX = p.x
                if (p.y < minY) minY = p.y
                if (p.y > maxY) maxY = p.y
            }
        }

        for (outline in validOutlines) {
            val halfWidth = outline.strokeWidth / 2f
            for (p in outline.points) {
                if (p.x - halfWidth < minX) minX = p.x - halfWidth
                if (p.x + halfWidth > maxX) maxX = p.x + halfWidth
                if (p.y - halfWidth < minY) minY = p.y - halfWidth
                if (p.y + halfWidth > maxY) maxY = p.y + halfWidth
            }
        }

        if (minX == Float.MAX_VALUE || maxX == -Float.MAX_VALUE || minY == Float.MAX_VALUE || maxY == -Float.MAX_VALUE) {
            return null
        }

        return ArtworkBounds(minX, maxX, minY, maxY)
    }

    /**
     * Crops strokes, vector fills, and vector outlines together to their combined visible bounding box,
     * adding a comfortable padding margin and shifting coordinates so the artwork
     * starts near (padding, padding).
     *
     * Eraser strokes outside the visible drawing boundary are filtered out, while
     * eraser strokes intersecting the visible drawing are retained and translated
     * to preserve erased details.
     */
    fun cropArtworkContent(
        strokes: List<Stroke>,
        vectorFills: List<VectorFill> = emptyList(),
        vectorOutlines: List<VectorOutline> = emptyList(),
        padding: Float = DEFAULT_PADDING
    ): CroppedArtworkContent {
        val bounds = calculateBounds(strokes, vectorFills, vectorOutlines)
            ?: return CroppedArtworkContent(emptyList(), emptyList(), emptyList())

        val cropMinX = bounds.minX - padding
        val cropMinY = bounds.minY - padding
        val cropMaxX = bounds.maxX + padding
        val cropMaxY = bounds.maxY + padding

        val croppedStrokes = strokes.mapNotNull { stroke ->
            if (stroke.points.isEmpty()) return@mapNotNull null

            if (stroke.tool == DrawingTool.ERASER) {
                val halfWidth = stroke.strokeWidth / 2f
                val intersects = stroke.points.any { p ->
                    p.x + halfWidth >= cropMinX && p.x - halfWidth <= cropMaxX &&
                    p.y + halfWidth >= cropMinY && p.y - halfWidth <= cropMaxY
                }
                if (!intersects) return@mapNotNull null
            }

            stroke.copy(
                points = stroke.points.map { p ->
                    Point(x = p.x - cropMinX, y = p.y - cropMinY)
                }
            )
        }

        val croppedFills = vectorFills.mapNotNull { fill ->
            if (fill.points.isEmpty()) return@mapNotNull null
            fill.copy(
                points = fill.points.map { p ->
                    Point(x = p.x - cropMinX, y = p.y - cropMinY)
                }
            )
        }

        val croppedOutlines = vectorOutlines.mapNotNull { outline ->
            if (outline.points.isEmpty()) return@mapNotNull null
            outline.copy(
                points = outline.points.map { p ->
                    Point(x = p.x - cropMinX, y = p.y - cropMinY)
                }
            )
        }

        return CroppedArtworkContent(
            strokes = croppedStrokes,
            vectorFills = croppedFills,
            vectorOutlines = croppedOutlines
        )
    }

    /**
     * Crops a list of strokes to the bounding box of its visible (non-eraser) strokes,
     * adding a comfortable padding margin and shifting coordinates so the artwork
     * starts near (padding, padding).
     */
    fun cropStrokes(strokes: List<Stroke>, padding: Float = DEFAULT_PADDING): List<Stroke> {
        return cropArtworkContent(strokes, emptyList(), emptyList(), padding).strokes
    }
}

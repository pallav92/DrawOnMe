package com.pallav.drawonme.domain.model

/**
 * Utility for cropping and normalizing drawing strokes to their visible bounding box.
 * Discards empty space surrounding the artwork and normalizes coordinates to origin (0, 0),
 * ensuring landscape or small artworks are saved tightly without canvas dead space.
 */
object ArtworkCropper {

    const val DEFAULT_PADDING: Float = 24f

    /**
     * Crops a list of strokes to the bounding box of its visible (non-eraser) strokes,
     * adding a comfortable padding margin and shifting coordinates so the artwork
     * starts near (padding, padding).
     *
     * Eraser strokes outside the visible drawing boundary are filtered out, while
     * eraser strokes intersecting the visible drawing are retained and translated
     * to preserve erased details.
     */
    fun cropStrokes(strokes: List<Stroke>, padding: Float = DEFAULT_PADDING): List<Stroke> {
        val visibleStrokes = strokes.filter { it.tool != DrawingTool.ERASER && it.points.isNotEmpty() }
        if (visibleStrokes.isEmpty()) return emptyList()

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

        val cropMinX = minX - padding
        val cropMinY = minY - padding
        val cropMaxX = maxX + padding
        val cropMaxY = maxY + padding

        return strokes.mapNotNull { stroke ->
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
    }
}

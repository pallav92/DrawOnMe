package com.draw.onme.domain.model

/**
 * Utility for converting a colored page (fills, outlines, and freehand strokes)
 * into a standard [SavedArtwork] ready for display on the virtual refrigerator door
 * and high-resolution image exporting.
 * Pure Kotlin implementation with zero Android framework dependencies.
 */
object ColoringArtworkConverter {

    const val CANONICAL_CANVAS_SIZE: Float = 1000f

    /**
     * Converts a completed coloring session into a [SavedArtwork] with scaled strokes.
     *
     * @param page The coloring page template.
     * @param fills Map of region ID to filled [StrokeColor].
     * @param freehandStrokes Additional crayon strokes drawn by the child.
     * @param title Title of the masterpiece (defaults to page title).
     * @return [SavedArtwork] containing the rendered strokes.
     */
    fun toSavedArtwork(
        page: ColoringPage,
        fills: Map<String, StrokeColor>,
        freehandStrokes: List<Stroke> = emptyList(),
        customFills: List<ClosedAreaFill> = emptyList(),
        title: String = page.title
    ): SavedArtwork {
        val vectorFills = mutableListOf<VectorFill>()

        // 1. Vector Fills: closed polygon regions from the coloring page template
        for (region in page.regions) {
            val color = fills[region.id] ?: continue
            val poly = region.boundaryPoints
            if (poly.size < 3) continue

            vectorFills.add(
                VectorFill(
                    points = poly.map { Point(it.x * CANONICAL_CANVAS_SIZE, it.y * CANONICAL_CANVAS_SIZE) },
                    color = color
                )
            )
        }

        val strokes = mutableListOf<Stroke>()

        // 2. Custom flood fills: represented as 2-point line strokes
        for (fill in customFills) {
            for (span in fill.spans) {
                strokes.add(
                    Stroke(
                        points = listOf(
                            Point(span.x1, span.y),
                            Point(span.x2, span.y)
                        ),
                        color = fill.color,
                        strokeWidth = 2.5f,
                        tool = DrawingTool.PEN
                    )
                )
            }
        }

        // 3. Child's freehand / crayon strokes
        strokes.addAll(freehandStrokes)

        // 4. Vector Outlines: bold line-art outlines on top (straight line segments to preserve sharp geometry)
        val vectorOutlines = mutableListOf<VectorOutline>()
        for (outline in page.outlines) {
            if (outline.points.isEmpty()) continue
            vectorOutlines.add(
                VectorOutline(
                    points = outline.points.map {
                        Point(it.x * CANONICAL_CANVAS_SIZE, it.y * CANONICAL_CANVAS_SIZE)
                    },
                    strokeWidth = outline.strokeWidth * 1.5f,
                    isClosed = outline.isClosed,
                    color = StrokeColor.Black
                )
            )
        }

        return SavedArtwork.createCropped(
            title = title,
            strokes = strokes,
            stencilId = "coloring_${page.id}",
            vectorFills = vectorFills,
            vectorOutlines = vectorOutlines
        )
    }
}

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
        val strokes = mutableListOf<Stroke>()

        // 1b. Render closed-area flood fills as horizontal scanline strokes
        for (fill in customFills) {
            for (span in fill.spans) {
                strokes.add(
                    Stroke(
                        points = listOf(
                            Point(span.x1, span.y),
                            Point(span.x2, span.y)
                        ),
                        color = fill.color,
                        strokeWidth = 3f,
                        tool = DrawingTool.PEN
                    )
                )
            }
        }

        // 1. Render filled regions as dense horizontal scanlines + perimeter strokes
        for (region in page.regions) {
            val color = fills[region.id] ?: continue
            val poly = region.boundaryPoints
            if (poly.size < 3) continue

            // Add perimeter boundary stroke
            strokes.add(
                Stroke(
                    points = poly.map { Point(it.x * CANONICAL_CANVAS_SIZE, it.y * CANONICAL_CANVAS_SIZE) } +
                        Point(poly[0].x * CANONICAL_CANVAS_SIZE, poly[0].y * CANONICAL_CANVAS_SIZE),
                    color = color,
                    strokeWidth = 14f,
                    tool = DrawingTool.PEN
                )
            )

            // Calculate scanline bounds
            var minY = Float.MAX_VALUE
            var maxY = -Float.MAX_VALUE
            for (p in poly) {
                if (p.y < minY) minY = p.y
                if (p.y > maxY) maxY = p.y
            }

            // Step through scanlines
            val step = 0.010f // 10 units in 1000x1000 space
            var y = minY + step / 2f
            while (y <= maxY) {
                val intersections = mutableListOf<Float>()
                var j = poly.size - 1
                for (i in poly.indices) {
                    val pi = poly[i]
                    val pj = poly[j]
                    if ((pi.y > y) != (pj.y > y)) {
                        val x = (pj.x - pi.x) * (y - pi.y) / (pj.y - pi.y) + pi.x
                        intersections.add(x)
                    }
                    j = i
                }

                intersections.sort()
                var k = 0
                while (k + 1 < intersections.size) {
                    val x1 = intersections[k] * CANONICAL_CANVAS_SIZE
                    val x2 = intersections[k + 1] * CANONICAL_CANVAS_SIZE
                    if (x2 > x1) {
                        strokes.add(
                            Stroke(
                                points = listOf(
                                    Point(x1, y * CANONICAL_CANVAS_SIZE),
                                    Point(x2, y * CANONICAL_CANVAS_SIZE)
                                ),
                                color = color,
                                strokeWidth = 14f,
                                tool = DrawingTool.PEN
                            )
                        )
                    }
                    k += 2
                }
                y += step
            }
        }

        // 2. Add child's freehand / crayon strokes
        strokes.addAll(freehandStrokes)

        // 3. Render bold line-art outlines on top
        for (outline in page.outlines) {
            if (outline.points.isEmpty()) continue
            val outlinePoints = outline.points.map {
                Point(it.x * CANONICAL_CANVAS_SIZE, it.y * CANONICAL_CANVAS_SIZE)
            }
            val finalPoints = if (outline.isClosed && outlinePoints.isNotEmpty()) {
                outlinePoints + outlinePoints.first()
            } else {
                outlinePoints
            }

            strokes.add(
                Stroke(
                    points = finalPoints,
                    color = StrokeColor.Black,
                    strokeWidth = outline.strokeWidth * 1.5f,
                    tool = DrawingTool.PEN
                )
            )
        }

        return SavedArtwork.createCropped(
            title = title,
            strokes = strokes,
            stencilId = "coloring_${page.id}"
        )
    }
}

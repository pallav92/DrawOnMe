package com.draw.onme.domain.model

import java.util.ArrayDeque
import java.util.BitSet
import java.util.UUID
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * A horizontal line span representing a slice of a filled closed area.
 *
 * @property y Vertical coordinate in canvas space.
 * @property x1 Left horizontal coordinate in canvas space.
 * @property x2 Right horizontal coordinate in canvas space.
 */
data class FillSpan(
    val y: Float,
    val x1: Float,
    val x2: Float
)

/**
 * Represents a flood-filled closed area on the coloring canvas.
 * Can fill any closed boundary: page line-art outlines, user crayon strokes,
 * or combinations of both.
 *
 * @property id Unique identifier for the fill operation.
 * @property color The filled [StrokeColor].
 * @property spans The horizontal scanline spans that compose this filled area.
 * @property seedPoint The point where the user tapped to initiate the fill.
 * @property regionId Optional predefined region ID if tapped inside a template region.
 */
data class ClosedAreaFill(
    val id: String = UUID.randomUUID().toString(),
    val color: StrokeColor,
    val spans: List<FillSpan>,
    val seedPoint: Point,
    val regionId: String? = null
)

/**
 * High-performance flood fill engine for arbitrary closed areas.
 * Operates on a discrete boundary grid and uses scanline flood fill to find
 * exact horizontal spans bounded by page outlines, user strokes, and canvas edges.
 *
 * Pure Kotlin implementation with zero Android framework dependencies.
 */
object ClosedAreaFloodFiller {

    const val GRID_SIZE: Int = 500
    private const val CANONICAL_SIZE: Float = 1000f
    private const val SCALE: Float = CANONICAL_SIZE / GRID_SIZE.toFloat() // 2.0f

    /**
     * Executes a scanline flood fill from [seed] on the canonical canvas, bounded by [outlines],
     * [strokes], and canvas borders.
     *
     * @param seed Touch point in canvas coordinates (0..1000).
     * @param outlines Page template line-art outlines.
     * @param strokes Freehand crayon strokes drawn by the user.
     * @param fillColor Color to fill.
     * @param regionId Optional region ID if seed is inside a template region.
     * @param requireClosed If true and [regionId] is null, rejects fills that leak to the outer canvas border.
     * @return [ClosedAreaFill] containing the filled spans, or null if tap is outside a closed area.
     */
    fun floodFill(
        seed: Point,
        outlines: List<ColoringOutline>,
        strokes: List<Stroke>,
        fillColor: StrokeColor,
        regionId: String? = null,
        requireClosed: Boolean = true
    ): ClosedAreaFill? {
        val boundaryGrid = rasterizeBoundaries(outlines, strokes)

        val rawSeedX = (seed.x / SCALE).roundToInt().coerceIn(0, GRID_SIZE - 1)
        val rawSeedY = (seed.y / SCALE).roundToInt().coerceIn(0, GRID_SIZE - 1)

        // Find nearest open pixel if tapped directly on a boundary stroke/outline
        val (startX, startY) = findOpenSeed(rawSeedX, rawSeedY, boundaryGrid) ?: return null

        val visited = BitSet(GRID_SIZE * GRID_SIZE)
        val spans = mutableListOf<FillSpan>()
        val queue = ArrayDeque<Int>() // packed x (low 16) and y (high 16)

        var touchedCanvasBorder = false

        queue.add(startX or (startY shl 16))

        while (queue.isNotEmpty()) {
            val packed = queue.removeFirst()
            val cx = packed and 0xFFFF
            val cy = packed ushr 16

            val idx = cy * GRID_SIZE + cx
            if (boundaryGrid.get(idx) || visited.get(idx)) continue

            // Scan left
            var left = cx
            while (left > 0 && !boundaryGrid.get(cy * GRID_SIZE + (left - 1)) && !visited.get(cy * GRID_SIZE + (left - 1))) {
                left--
            }

            // Scan right
            var right = cx
            while (right < GRID_SIZE - 1 && !boundaryGrid.get(cy * GRID_SIZE + (right + 1)) && !visited.get(cy * GRID_SIZE + (right + 1))) {
                right++
            }

            if (left <= 1 || right >= GRID_SIZE - 2 || cy <= 1 || cy >= GRID_SIZE - 2) {
                touchedCanvasBorder = true
            }

            // Mark horizontal span [left..right] as visited
            for (x in left..right) {
                visited.set(cy * GRID_SIZE + x)
            }

            // Scale span back to 1000x1000 canvas space
            spans.add(
                FillSpan(
                    y = cy * SCALE + SCALE / 2f,
                    x1 = left * SCALE,
                    x2 = (right + 1) * SCALE
                )
            )

            // Scan adjacent rows for seed candidates
            if (cy > 0) {
                scanRowForSeeds(left, right, cy - 1, boundaryGrid, visited, queue)
            }
            if (cy < GRID_SIZE - 1) {
                scanRowForSeeds(left, right, cy + 1, boundaryGrid, visited, queue)
            }
        }

        if (spans.isEmpty()) return null

        // If the fill leaked out to the infinite canvas borders and wasn't in a template region,
        // it was tapped in open space rather than an enclosed closed area.
        if (requireClosed && regionId == null && touchedCanvasBorder) {
            return null
        }

        return ClosedAreaFill(
            color = fillColor,
            spans = spans,
            seedPoint = seed,
            regionId = regionId
        )
    }

    /**
     * Rasterizes line-art outlines, user crayon strokes, and canvas perimeters into a boundary [BitSet].
     */
    fun rasterizeBoundaries(
        outlines: List<ColoringOutline>,
        strokes: List<Stroke>
    ): BitSet {
        val grid = BitSet(GRID_SIZE * GRID_SIZE)

        // 1. Mark canvas outer borders
        for (x in 0 until GRID_SIZE) {
            grid.set(x) // Top border
            grid.set((GRID_SIZE - 1) * GRID_SIZE + x) // Bottom border
        }
        for (y in 0 until GRID_SIZE) {
            grid.set(y * GRID_SIZE) // Left border
            grid.set(y * GRID_SIZE + (GRID_SIZE - 1)) // Right border
        }

        // 2. Rasterize page line-art outlines
        for (outline in outlines) {
            if (outline.points.size < 2) continue
            val radius = (outline.strokeWidth / SCALE / 2f).roundToInt().coerceIn(1, 4)

            for (i in 0 until outline.points.size - 1) {
                val p1 = outline.points[i]
                val p2 = outline.points[i + 1]
                val x0 = (p1.x * (GRID_SIZE - 1)).roundToInt()
                val y0 = (p1.y * (GRID_SIZE - 1)).roundToInt()
                val x1 = (p2.x * (GRID_SIZE - 1)).roundToInt()
                val y1 = (p2.y * (GRID_SIZE - 1)).roundToInt()
                drawThickLine(x0, y0, x1, y1, radius, grid)
            }

            if (outline.isClosed && outline.points.size > 2) {
                val first = outline.points.first()
                val last = outline.points.last()
                val x0 = (last.x * (GRID_SIZE - 1)).roundToInt()
                val y0 = (last.y * (GRID_SIZE - 1)).roundToInt()
                val x1 = (first.x * (GRID_SIZE - 1)).roundToInt()
                val y1 = (first.y * (GRID_SIZE - 1)).roundToInt()
                drawThickLine(x0, y0, x1, y1, radius, grid)
            }
        }

        // 3. Rasterize user crayon strokes
        for (stroke in strokes) {
            if (stroke.points.isEmpty()) continue
            val radius = (stroke.strokeWidth / SCALE / 2f).roundToInt().coerceIn(1, 10)

            if (stroke.points.size == 1) {
                val pt = stroke.points[0]
                val cx = (pt.x / SCALE).roundToInt()
                val cy = (pt.y / SCALE).roundToInt()
                drawThickCircle(cx, cy, radius, grid)
            } else {
                for (i in 0 until stroke.points.size - 1) {
                    val p1 = stroke.points[i]
                    val p2 = stroke.points[i + 1]
                    val x0 = (p1.x / SCALE).roundToInt()
                    val y0 = (p1.y / SCALE).roundToInt()
                    val x1 = (p2.x / SCALE).roundToInt()
                    val y1 = (p2.y / SCALE).roundToInt()
                    drawThickLine(x0, y0, x1, y1, radius, grid)
                }
            }
        }

        // 4. Automatically bridge small corner gaps, loop ends, and T-junctions
        bridgeGaps(strokes, grid)

        return grid
    }

    const val MAX_GAP_DISTANCE: Float = 42f

    /**
     * Bridges small gaps in hand-drawn strokes (such as corners of boxes, nearly-closed loops,
     * or T-junctions) so they form valid closed boundaries for the flood fill engine.
     */
    private fun bridgeGaps(
        strokes: List<Stroke>,
        grid: BitSet,
        maxGap: Float = MAX_GAP_DISTANCE
    ) {
        if (strokes.isEmpty()) return
        val maxGapSq = maxGap * maxGap

        // 1. Bridge start and end of single-stroke loops
        for (stroke in strokes) {
            if (stroke.points.size >= 3) {
                val first = stroke.points.first()
                val last = stroke.points.last()
                val dSq = (first.x - last.x) * (first.x - last.x) + (first.y - last.y) * (first.y - last.y)
                if (dSq <= maxGapSq) {
                    val radius = (stroke.strokeWidth / SCALE / 2f).roundToInt().coerceIn(1, 10)
                    drawThickLine(
                        (last.x / SCALE).roundToInt(),
                        (last.y / SCALE).roundToInt(),
                        (first.x / SCALE).roundToInt(),
                        (first.y / SCALE).roundToInt(),
                        radius,
                        grid
                    )
                }
            }
        }

        // 2. Collect all endpoints from all strokes
        data class Endpoint(val point: Point, val strokeIndex: Int, val isStart: Boolean, val strokeWidth: Float)
        val endpoints = mutableListOf<Endpoint>()
        for (i in strokes.indices) {
            val stroke = strokes[i]
            if (stroke.points.isNotEmpty()) {
                endpoints.add(Endpoint(stroke.points.first(), i, true, stroke.strokeWidth))
                if (stroke.points.size > 1) {
                    endpoints.add(Endpoint(stroke.points.last(), i, false, stroke.strokeWidth))
                }
            }
        }

        // Bridge endpoints between different strokes (corners of multi-stroke boxes, shapes)
        for (i in endpoints.indices) {
            for (j in i + 1 until endpoints.size) {
                val ep1 = endpoints[i]
                val ep2 = endpoints[j]
                if (ep1.strokeIndex != ep2.strokeIndex) {
                    val dSq = (ep1.point.x - ep2.point.x) * (ep1.point.x - ep2.point.x) +
                              (ep1.point.y - ep2.point.y) * (ep1.point.y - ep2.point.y)
                    if (dSq <= maxGapSq) {
                        val radius = (maxOf(ep1.strokeWidth, ep2.strokeWidth) / SCALE / 2f).roundToInt().coerceIn(1, 10)
                        drawThickLine(
                            (ep1.point.x / SCALE).roundToInt(),
                            (ep1.point.y / SCALE).roundToInt(),
                            (ep2.point.x / SCALE).roundToInt(),
                            (ep2.point.y / SCALE).roundToInt(),
                            radius,
                            grid
                        )
                    }
                }
            }
        }

        // 3. Bridge endpoints to nearby stroke segments (T-junctions and near-overshoots)
        for (ep in endpoints) {
            for (sIdx in strokes.indices) {
                if (sIdx == ep.strokeIndex) continue
                val targetStroke = strokes[sIdx]
                if (targetStroke.points.size < 2) continue

                var closestPt: Point? = null
                var minDSq = Float.MAX_VALUE

                for (pIdx in 0 until targetStroke.points.size - 1) {
                    val segA = targetStroke.points[pIdx]
                    val segB = targetStroke.points[pIdx + 1]
                    val proj = closestPointOnSegment(ep.point, segA, segB)
                    val dSq = (ep.point.x - proj.x) * (ep.point.x - proj.x) +
                              (ep.point.y - proj.y) * (ep.point.y - proj.y)
                    if (dSq < minDSq) {
                        minDSq = dSq
                        closestPt = proj
                    }
                }

                if (closestPt != null && minDSq <= maxGapSq) {
                    val radius = (maxOf(ep.strokeWidth, targetStroke.strokeWidth) / SCALE / 2f).roundToInt().coerceIn(1, 10)
                    drawThickLine(
                        (ep.point.x / SCALE).roundToInt(),
                        (ep.point.y / SCALE).roundToInt(),
                        (closestPt.x / SCALE).roundToInt(),
                        (closestPt.y / SCALE).roundToInt(),
                        radius,
                        grid
                    )
                }
            }
        }
    }

    private fun closestPointOnSegment(p: Point, a: Point, b: Point): Point {
        val abx = b.x - a.x
        val aby = b.y - a.y
        val lenSq = abx * abx + aby * aby
        if (lenSq < 1e-4f) return a
        val apx = p.x - a.x
        val apy = p.y - a.y
        val t = ((apx * abx + apy * aby) / lenSq).coerceIn(0f, 1f)
        return Point(a.x + t * abx, a.y + t * aby)
    }

    private fun scanRowForSeeds(
        left: Int,
        right: Int,
        y: Int,
        boundary: BitSet,
        visited: BitSet,
        queue: ArrayDeque<Int>
    ) {
        var inSpan = false
        val rowOffset = y * GRID_SIZE
        for (x in left..right) {
            val idx = rowOffset + x
            val isBlocked = boundary.get(idx) || visited.get(idx)
            if (!inSpan && !isBlocked) {
                inSpan = true
                queue.add(x or (y shl 16))
            } else if (inSpan && isBlocked) {
                inSpan = false
            }
        }
    }

    private fun findOpenSeed(
        centerX: Int,
        centerY: Int,
        boundary: BitSet,
        maxSearchRadius: Int = 14
    ): Pair<Int, Int>? {
        if (!boundary.get(centerY * GRID_SIZE + centerX)) {
            return Pair(centerX, centerY)
        }

        for (r in 1..maxSearchRadius) {
            for (dy in -r..r) {
                for (dx in -r..r) {
                    if (abs(dx) == r || abs(dy) == r) {
                        val px = (centerX + dx).coerceIn(0, GRID_SIZE - 1)
                        val py = (centerY + dy).coerceIn(0, GRID_SIZE - 1)
                        if (!boundary.get(py * GRID_SIZE + px)) {
                            return Pair(px, py)
                        }
                    }
                }
            }
        }
        return null
    }

    /**
     * Bresenham thick line rasterization on grid.
     */
    private fun drawThickLine(
        x0: Int,
        y0: Int,
        x1: Int,
        y1: Int,
        radius: Int,
        grid: BitSet
    ) {
        var cx = x0
        var cy = y0
        val dx = abs(x1 - x0)
        val dy = -abs(y1 - y0)
        val sx = if (x0 < x1) 1 else -1
        val sy = if (y0 < y1) 1 else -1
        var err = dx + dy

        while (true) {
            drawThickCircle(cx, cy, radius, grid)
            if (cx == x1 && cy == y1) break
            val e2 = 2 * err
            if (e2 >= dy) {
                err += dy
                cx += sx
            }
            if (e2 <= dx) {
                err += dx
                cy += sy
            }
        }
    }

    private fun drawThickCircle(cx: Int, cy: Int, radius: Int, grid: BitSet) {
        val rSq = radius * radius
        for (dy in -radius..radius) {
            val py = cy + dy
            if (py !in 0 until GRID_SIZE) continue
            val rowOffset = py * GRID_SIZE
            for (dx in -radius..radius) {
                val px = cx + dx
                if (px !in 0 until GRID_SIZE) continue
                if (dx * dx + dy * dy <= rSq) {
                    grid.set(rowOffset + px)
                }
            }
        }
    }
}

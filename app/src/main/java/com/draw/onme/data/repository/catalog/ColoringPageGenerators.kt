package com.draw.onme.data.repository.catalog

import com.draw.onme.domain.model.ColoringOutline
import com.draw.onme.domain.model.ColoringPage
import com.draw.onme.domain.model.ColoringRegion
import com.draw.onme.domain.model.Point
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Geometric primitives and utility functions for generating rich vector coloring pages.
 * Ensures all points stay in normalized [0.01f..0.99f] bounds and every outline is closed.
 */
internal object ColoringPageGenerators {

    fun circlePoints(cx: Float, cy: Float, r: Float, steps: Int = 16): List<Point> {
        return (0 until steps).map { i ->
            val angle = 2.0 * PI * i / steps
            Point(
                x = (cx + r * cos(angle)).toFloat().coerceIn(0.01f, 0.99f),
                y = (cy + r * sin(angle)).toFloat().coerceIn(0.01f, 0.99f)
            )
        }
    }

    fun ovalPoints(cx: Float, cy: Float, rx: Float, ry: Float, steps: Int = 16): List<Point> {
        return (0 until steps).map { i ->
            val angle = 2.0 * PI * i / steps
            Point(
                x = (cx + rx * cos(angle)).toFloat().coerceIn(0.01f, 0.99f),
                y = (cy + ry * sin(angle)).toFloat().coerceIn(0.01f, 0.99f)
            )
        }
    }

    fun rectPoints(x1: Float, y1: Float, x2: Float, y2: Float): List<Point> {
        val minX = minOf(x1, x2).coerceIn(0.01f, 0.99f)
        val maxX = maxOf(x1, x2).coerceIn(0.01f, 0.99f)
        val minY = minOf(y1, y2).coerceIn(0.01f, 0.99f)
        val maxY = maxOf(y1, y2).coerceIn(0.01f, 0.99f)
        return listOf(
            Point(minX, minY),
            Point(maxX, minY),
            Point(maxX, maxY),
            Point(minX, maxY)
        )
    }

    fun starPoints(cx: Float, cy: Float, rOuter: Float, rInner: Float, points: Int = 5): List<Point> {
        val totalSteps = points * 2
        return (0 until totalSteps).map { i ->
            val r = if (i % 2 == 0) rOuter else rInner
            val angle = -PI / 2.0 + (i.toDouble() * PI / points.toDouble())
            Point(
                x = (cx + r * cos(angle)).toFloat().coerceIn(0.01f, 0.99f),
                y = (cy + r * sin(angle)).toFloat().coerceIn(0.01f, 0.99f)
            )
        }
    }

    fun trapezoidPoints(xTop1: Float, xTop2: Float, yTop: Float, xBot1: Float, xBot2: Float, yBot: Float): List<Point> = listOf(
        Point(xTop1.coerceIn(0.01f, 0.99f), yTop.coerceIn(0.01f, 0.99f)),
        Point(xTop2.coerceIn(0.01f, 0.99f), yTop.coerceIn(0.01f, 0.99f)),
        Point(xBot2.coerceIn(0.01f, 0.99f), yBot.coerceIn(0.01f, 0.99f)),
        Point(xBot1.coerceIn(0.01f, 0.99f), yBot.coerceIn(0.01f, 0.99f))
    )

    fun trianglePoints(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float): List<Point> = listOf(
        Point(x1.coerceIn(0.01f, 0.99f), y1.coerceIn(0.01f, 0.99f)),
        Point(x2.coerceIn(0.01f, 0.99f), y2.coerceIn(0.01f, 0.99f)),
        Point(x3.coerceIn(0.01f, 0.99f), y3.coerceIn(0.01f, 0.99f))
    )

    fun diamondPoints(cx: Float, cy: Float, rx: Float, ry: Float): List<Point> = listOf(
        Point(cx.coerceIn(0.01f, 0.99f), (cy - ry).coerceIn(0.01f, 0.99f)),
        Point((cx + rx).coerceIn(0.01f, 0.99f), cy.coerceIn(0.01f, 0.99f)),
        Point(cx.coerceIn(0.01f, 0.99f), (cy + ry).coerceIn(0.01f, 0.99f)),
        Point((cx - rx).coerceIn(0.01f, 0.99f), cy.coerceIn(0.01f, 0.99f))
    )

    fun leafEar(cx: Float, cy: Float, r: Float): List<Point> = listOf(
        Point(cx.coerceIn(0.01f, 0.99f), (cy - r).coerceIn(0.01f, 0.99f)),
        Point((cx + r).coerceIn(0.01f, 0.99f), cy.coerceIn(0.01f, 0.99f)),
        Point(cx.coerceIn(0.01f, 0.99f), (cy + r).coerceIn(0.01f, 0.99f)),
        Point((cx - r).coerceIn(0.01f, 0.99f), cy.coerceIn(0.01f, 0.99f))
    )

    fun fanPoints(
        cx: Float,
        cy: Float,
        rInner: Float,
        rOuter: Float,
        startDeg: Double,
        endDeg: Double,
        steps: Int = 8
    ): List<Point> {
        val outer = (0..steps).map { i ->
            val angle = Math.toRadians(startDeg + (endDeg - startDeg) * i / steps)
            Point(
                x = (cx + rOuter * cos(angle)).toFloat().coerceIn(0.01f, 0.99f),
                y = (cy + rOuter * sin(angle)).toFloat().coerceIn(0.01f, 0.99f)
            )
        }
        val inner = (steps downTo 0).map { i ->
            val angle = Math.toRadians(startDeg + (endDeg - startDeg) * i / steps)
            Point(
                x = (cx + rInner * cos(angle)).toFloat().coerceIn(0.01f, 0.99f),
                y = (cy + rInner * sin(angle)).toFloat().coerceIn(0.01f, 0.99f)
            )
        }
        return outer + inner
    }

    fun createPage(
        id: String,
        title: String,
        category: String,
        iconEmoji: String,
        difficulty: String,
        regions: List<ColoringRegion>
    ): ColoringPage {
        val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) }
        return ColoringPage(
            id = id,
            title = title,
            category = category,
            iconEmoji = iconEmoji,
            difficulty = difficulty,
            regions = regions,
            outlines = outlines
        )
    }
}

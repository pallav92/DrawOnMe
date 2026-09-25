package com.draw.onme.domain.model

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ClosedAreaFloodFillerTest {

    @Test
    fun testFloodFill_insideClosedStrokesSquare() {
        // Draw a closed box with strokes from 200..600
        val squareStroke = Stroke(
            points = listOf(
                Point(200f, 200f),
                Point(600f, 200f),
                Point(600f, 600f),
                Point(200f, 600f),
                Point(200f, 200f)
            ),
            color = StrokeColor.Black,
            strokeWidth = 16f,
            tool = DrawingTool.PEN
        )

        val fillResult = ClosedAreaFloodFiller.floodFill(
            seed = Point(400f, 400f),
            outlines = emptyList(),
            strokes = listOf(squareStroke),
            fillColor = StrokeColor.Red
        )

        assertNotNull(fillResult)
        assertTrue(fillResult!!.spans.isNotEmpty())

        // Verify all spans are inside the box boundaries (with thickness margin)
        for (span in fillResult.spans) {
            assertTrue("Span Y (${span.y}) should be within [180..620]", span.y in 180f..620f)
            assertTrue("Span X1 (${span.x1}) should be within [180..620]", span.x1 >= 180f)
            assertTrue("Span X2 (${span.x2}) should be within [180..620]", span.x2 <= 620f)
        }
    }

    @Test
    fun testFloodFill_insideClosedOutline() {
        val outline = ColoringOutline(
            points = listOf(
                Point(0.2f, 0.2f),
                Point(0.8f, 0.2f),
                Point(0.8f, 0.8f),
                Point(0.2f, 0.8f)
            ),
            strokeWidth = 8f,
            isClosed = true
        )

        val fillResult = ClosedAreaFloodFiller.floodFill(
            seed = Point(500f, 500f),
            outlines = listOf(outline),
            strokes = emptyList(),
            fillColor = StrokeColor.Blue,
            regionId = "box"
        )

        assertNotNull(fillResult)
        assertTrue(fillResult!!.spans.isNotEmpty())
        for (span in fillResult.spans) {
            assertTrue(span.y in 190f..810f)
            assertTrue(span.x1 >= 190f)
            assertTrue(span.x2 <= 810f)
        }
    }

    @Test
    fun testFloodFill_directTouchOnStroke_forgivesTouchAndFillsInterior() {
        val squareStroke = Stroke(
            points = listOf(
                Point(300f, 300f),
                Point(500f, 300f),
                Point(500f, 500f),
                Point(300f, 500f),
                Point(300f, 300f)
            ),
            color = StrokeColor.Black,
            strokeWidth = 20f,
            tool = DrawingTool.PEN
        )

        // Seed on inner edge of boundary stroke at (400, 308)
        val fillResult = ClosedAreaFloodFiller.floodFill(
            seed = Point(400f, 308f),
            outlines = emptyList(),
            strokes = listOf(squareStroke),
            fillColor = StrokeColor.Yellow
        )

        assertNotNull("Should forgive touch and find open interior seed", fillResult)
        assertTrue(fillResult!!.spans.isNotEmpty())
    }

    @Test
    fun testFloodFill_openShape_rejectedWhenClosedAreaRequired() {
        // Only one unclosed line from 100 to 900
        val lineStroke = Stroke(
            points = listOf(Point(100f, 500f), Point(900f, 500f)),
            color = StrokeColor.Black,
            strokeWidth = 20f,
            tool = DrawingTool.PEN
        )

        // Tapping in open space that leaks to canvas edge should be rejected when requireClosed = true
        val fillResult = ClosedAreaFloodFiller.floodFill(
            seed = Point(500f, 200f),
            outlines = emptyList(),
            strokes = listOf(lineStroke),
            fillColor = StrokeColor.Green,
            requireClosed = true
        )

        assertNull("Unenclosed area leaking to canvas border should not fill", fillResult)
    }

    @Test
    fun testFloodFill_leakageStopsAtCanvasEdge_whenOpenAreaAllowed() {
        val lineStroke = Stroke(
            points = listOf(Point(100f, 500f), Point(900f, 500f)),
            color = StrokeColor.Black,
            strokeWidth = 20f,
            tool = DrawingTool.PEN
        )

        val fillResult = ClosedAreaFloodFiller.floodFill(
            seed = Point(500f, 200f),
            outlines = emptyList(),
            strokes = listOf(lineStroke),
            fillColor = StrokeColor.Green,
            requireClosed = false
        )

        assertNotNull(fillResult)
        assertTrue(fillResult!!.spans.isNotEmpty())
    }

    @Test
    fun testFloodFill_boxDrawnWithSmallGapAtCorner() {
        val top = Stroke(
            points = listOf(Point(200f, 200f), Point(400f, 200f)),
            color = StrokeColor.Black,
            strokeWidth = 8f,
            tool = DrawingTool.PEN
        )
        val right = Stroke(
            points = listOf(Point(400f, 224f), Point(400f, 400f)),
            color = StrokeColor.Black,
            strokeWidth = 8f,
            tool = DrawingTool.PEN
        )
        val bottom = Stroke(
            points = listOf(Point(400f, 400f), Point(200f, 400f)),
            color = StrokeColor.Black,
            strokeWidth = 8f,
            tool = DrawingTool.PEN
        )
        val left = Stroke(
            points = listOf(Point(200f, 400f), Point(200f, 200f)),
            color = StrokeColor.Black,
            strokeWidth = 8f,
            tool = DrawingTool.PEN
        )

        val fillResult = ClosedAreaFloodFiller.floodFill(
            seed = Point(300f, 300f),
            outlines = emptyList(),
            strokes = listOf(top, right, bottom, left),
            fillColor = StrokeColor.Red
        )

        assertNotNull("Box with slight corner gap should be filled", fillResult)
    }

    @Test
    fun testFloodFill_singleStrokeWithSmallGapAtStartEnd() {
        val stroke = Stroke(
            points = listOf(
                Point(200f, 200f),
                Point(400f, 200f),
                Point(400f, 400f),
                Point(200f, 400f),
                Point(200f, 230f)
            ),
            color = StrokeColor.Black,
            strokeWidth = 8f,
            tool = DrawingTool.PEN
        )

        val fillResult = ClosedAreaFloodFiller.floodFill(
            seed = Point(300f, 300f),
            outlines = emptyList(),
            strokes = listOf(stroke),
            fillColor = StrokeColor.Red
        )

        assertNotNull("Loop stroke with small end gap should be filled", fillResult)
    }

    @Test
    fun testFloodFill_boxDrawnWithOverlappingStrokes() {
        // Strokes that overshoot each other at corners (e.g. by 20 pixels)
        val top = Stroke(
            points = listOf(Point(180f, 200f), Point(420f, 200f)),
            color = StrokeColor.Black,
            strokeWidth = 16f,
            tool = DrawingTool.PEN
        )
        val right = Stroke(
            points = listOf(Point(400f, 180f), Point(400f, 420f)),
            color = StrokeColor.Black,
            strokeWidth = 16f,
            tool = DrawingTool.PEN
        )
        val bottom = Stroke(
            points = listOf(Point(420f, 400f), Point(180f, 400f)),
            color = StrokeColor.Black,
            strokeWidth = 16f,
            tool = DrawingTool.PEN
        )
        val left = Stroke(
            points = listOf(Point(200f, 420f), Point(200f, 180f)),
            color = StrokeColor.Black,
            strokeWidth = 16f,
            tool = DrawingTool.PEN
        )

        val fillResult = ClosedAreaFloodFiller.floodFill(
            seed = Point(300f, 300f),
            outlines = emptyList(),
            strokes = listOf(top, right, bottom, left),
            fillColor = StrokeColor.Blue
        )

        assertNotNull("Box with overlapping/overshooting corners should fill", fillResult)
        assertTrue(fillResult!!.spans.isNotEmpty())
    }

    @Test
    fun testFloodFill_tJunctionAndNearOvershoot() {
        // Triangle formed by a bottom horizontal line and two angled strokes that stop slightly short of the base
        val base = Stroke(
            points = listOf(Point(100f, 500f), Point(500f, 500f)),
            color = StrokeColor.Black,
            strokeWidth = 16f,
            tool = DrawingTool.PEN
        )
        val leftSide = Stroke(
            points = listOf(Point(300f, 200f), Point(160f, 480f)), // 20px above base
            color = StrokeColor.Black,
            strokeWidth = 16f,
            tool = DrawingTool.PEN
        )
        val rightSide = Stroke(
            points = listOf(Point(300f, 200f), Point(440f, 485f)), // 15px above base
            color = StrokeColor.Black,
            strokeWidth = 16f,
            tool = DrawingTool.PEN
        )

        val fillResult = ClosedAreaFloodFiller.floodFill(
            seed = Point(300f, 400f),
            outlines = emptyList(),
            strokes = listOf(base, leftSide, rightSide),
            fillColor = StrokeColor.Purple
        )

        assertNotNull("Triangle with T-junction near base should fill", fillResult)
        assertTrue(fillResult!!.spans.isNotEmpty())
    }
}

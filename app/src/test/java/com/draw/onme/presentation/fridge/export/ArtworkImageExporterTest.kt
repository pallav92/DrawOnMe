package com.draw.onme.presentation.fridge.export

import com.draw.onme.domain.model.DrawingTool
import com.draw.onme.domain.model.Point
import com.draw.onme.domain.model.Stroke
import com.draw.onme.domain.model.StrokeColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ArtworkImageExporterTest {

    @Test
    fun calculateBounds_returnsNullForEmptyStrokes() {
        val bounds = ArtworkImageExporter.calculateBounds(emptyList())
        assertNull(bounds)
    }

    @Test
    fun calculateBounds_returnsNullForOnlyEraserStrokes() {
        val eraserStroke = Stroke(
            points = listOf(Point(100f, 100f), Point(200f, 200f)),
            color = StrokeColor.Red,
            strokeWidth = 10f,
            tool = DrawingTool.ERASER
        )
        val bounds = ArtworkImageExporter.calculateBounds(listOf(eraserStroke))
        assertNull(bounds)
    }

    @Test
    fun calculateBounds_computesAccurateBoundingBoxWithStrokeWidth() {
        val penStroke = Stroke(
            points = listOf(Point(100f, 200f), Point(300f, 600f)),
            color = StrokeColor.Blue,
            strokeWidth = 20f,
            tool = DrawingTool.PEN
        )

        val bounds = ArtworkImageExporter.calculateBounds(listOf(penStroke))
        assertNotNull(bounds)
        bounds?.let {
            // minX = 100 - 10 = 90, maxX = 300 + 10 = 310 -> width = 220
            // minY = 200 - 10 = 190, maxY = 600 + 10 = 610 -> height = 420
            assertEquals(90f, it.minX, 0.001f)
            assertEquals(310f, it.maxX, 0.001f)
            assertEquals(190f, it.minY, 0.001f)
            assertEquals(610f, it.maxY, 0.001f)
            assertEquals(220f, it.width, 0.001f)
            assertEquals(420f, it.height, 0.001f)
            assertEquals(220f / 420f, it.aspectRatio, 0.001f)
        }
    }

    @Test
    fun calculateBounds_ignoresEraserPointsWhenPenStrokesArePresent() {
        val penStroke = Stroke(
            points = listOf(Point(100f, 100f), Point(200f, 200f)),
            color = StrokeColor.Green,
            strokeWidth = 10f,
            tool = DrawingTool.PEN
        )
        // Eraser extends far outside the pen stroke
        val distantEraserStroke = Stroke(
            points = listOf(Point(1000f, 1000f)),
            color = StrokeColor.Red,
            strokeWidth = 50f,
            tool = DrawingTool.ERASER
        )

        val bounds = ArtworkImageExporter.calculateBounds(listOf(penStroke, distantEraserStroke))
        assertNotNull(bounds)
        bounds?.let {
            // Should strictly bound the pen stroke
            assertEquals(95f, it.minX, 0.001f)
            assertEquals(205f, it.maxX, 0.001f)
            assertEquals(95f, it.minY, 0.001f)
            assertEquals(205f, it.maxY, 0.001f)
            assertTrue("Bounds maxX should not include distant eraser", it.maxX < 500f)
        }
    }

    @Test
    fun constants_haveExpectedValues() {
        assertEquals(1200, ArtworkImageExporter.DEFAULT_BITMAP_WIDTH)
        assertEquals(160f, ArtworkImageExporter.FOOTER_HEIGHT, 0.001f)
    }
}

package com.pallav.drawonme.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ArtworkCropperTest {

    @Test
    fun cropStrokes_normalizesCoordinatesToOriginWithPadding() {
        // A single pen stroke drawn at far coordinates (500, 600) to (600, 700)
        val stroke = Stroke(
            points = listOf(Point(500f, 600f), Point(600f, 700f)),
            color = StrokeColor.Red,
            strokeWidth = 10f,
            tool = DrawingTool.PEN
        )

        val padding = 20f
        val cropped = ArtworkCropper.cropStrokes(listOf(stroke), padding = padding)

        assertEquals(1, cropped.size)
        val croppedStroke = cropped.first()
        assertEquals(2, croppedStroke.points.size)

        // minX with halfWidth 5 is 495f; cropMinX = 495 - 20 = 475f
        // minY with halfWidth 5 is 595f; cropMinY = 595 - 20 = 575f
        val p1 = croppedStroke.points[0]
        val p2 = croppedStroke.points[1]

        assertEquals(500f - 475f, p1.x, 0.01f) // 25f
        assertEquals(600f - 575f, p1.y, 0.01f) // 25f
        assertEquals(600f - 475f, p2.x, 0.01f) // 125f
        assertEquals(700f - 575f, p2.y, 0.01f) // 125f
    }

    @Test
    fun cropStrokes_ignoresEraserStrokesWhenCalculatingBounds() {
        // Pen stroke near center (100, 100) to (120, 120)
        val penStroke = Stroke(
            points = listOf(Point(100f, 100f), Point(120f, 120f)),
            color = StrokeColor.Blue,
            strokeWidth = 6f,
            tool = DrawingTool.PEN
        )
        // Eraser stroke far away at (0, 0) and (2000, 2000)
        val farEraserStroke = Stroke(
            points = listOf(Point(0f, 0f), Point(2000f, 2000f)),
            color = StrokeColor.Black,
            strokeWidth = 40f,
            tool = DrawingTool.ERASER
        )

        val cropped = ArtworkCropper.cropStrokes(listOf(penStroke, farEraserStroke), padding = 10f)

        // Far eraser should be excluded because it does not intersect the pen bounding box
        assertEquals(1, cropped.size)
        assertEquals(DrawingTool.PEN, cropped.first().tool)
    }

    @Test
    fun cropStrokes_retainsAndTranslatesIntersectingEraserStrokes() {
        // Pen stroke from (100, 100) to (200, 100)
        val penStroke = Stroke(
            points = listOf(Point(100f, 100f), Point(200f, 100f)),
            color = StrokeColor.Green,
            strokeWidth = 8f,
            tool = DrawingTool.PEN
        )
        // Eraser stroke crossing the pen stroke at (150, 100)
        val eraserStroke = Stroke(
            points = listOf(Point(150f, 100f)),
            color = StrokeColor.Black,
            strokeWidth = 20f,
            tool = DrawingTool.ERASER
        )

        val cropped = ArtworkCropper.cropStrokes(listOf(penStroke, eraserStroke), padding = 10f)

        assertEquals(2, cropped.size)
        val croppedPen = cropped[0]
        val croppedEraser = cropped[1]

        assertEquals(DrawingTool.PEN, croppedPen.tool)
        assertEquals(DrawingTool.ERASER, croppedEraser.tool)

        // Verify the eraser point was shifted by the exact same crop delta as pen points
        val deltaX = penStroke.points[0].x - croppedPen.points[0].x
        val deltaY = penStroke.points[0].y - croppedPen.points[0].y

        assertEquals(eraserStroke.points[0].x - deltaX, croppedEraser.points[0].x, 0.01f)
        assertEquals(eraserStroke.points[0].y - deltaY, croppedEraser.points[0].y, 0.01f)
    }

    @Test
    fun cropStrokes_emptyOrEraserOnly_returnsEmptyList() {
        assertTrue(ArtworkCropper.cropStrokes(emptyList()).isEmpty())

        val eraserOnly = listOf(
            Stroke(
                points = listOf(Point(50f, 50f)),
                color = StrokeColor.Black,
                strokeWidth = 20f,
                tool = DrawingTool.ERASER
            )
        )
        assertTrue(ArtworkCropper.cropStrokes(eraserOnly).isEmpty())
    }

    @Test
    fun savedArtwork_createCropped_normalizesAndPreservesMetadata() {
        val stroke = Stroke(
            points = listOf(Point(400f, 300f), Point(450f, 350f)),
            color = StrokeColor.Yellow,
            strokeWidth = 8f,
            tool = DrawingTool.PEN
        )

        val artwork = SavedArtwork.createCropped(
            title = "Cropped Masterpiece",
            strokes = listOf(stroke),
            stencilId = "cute-cat"
        )

        assertEquals("Cropped Masterpiece", artwork.title)
        assertEquals("cute-cat", artwork.stencilId)
        assertNotNull(artwork.id)
        assertTrue(artwork.createdAt > 0)
        assertEquals(1, artwork.strokes.size)

        // Coordinates should be close to origin, not 400+
        val firstPoint = artwork.strokes[0].points[0]
        assertTrue("Point X should be normalized near origin", firstPoint.x < 100f)
        assertTrue("Point Y should be normalized near origin", firstPoint.y < 100f)
    }
}

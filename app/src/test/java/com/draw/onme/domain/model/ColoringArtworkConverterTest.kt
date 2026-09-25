package com.draw.onme.domain.model

import com.draw.onme.data.repository.InMemoryColoringBookRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ColoringArtworkConverterTest {

    @Test
    fun testToSavedArtwork_createsStrokesFromFillsAndOutlines() {
        val repo = InMemoryColoringBookRepository()
        val page = repo.getPageById("happy_puppy")
        assertNotNull(page)

        val fills = mapOf(
            "head" to StrokeColor.Yellow,
            "snout" to StrokeColor.White
        )

        val freehand = listOf(
            Stroke(
                points = listOf(Point(500f, 500f), Point(510f, 510f)),
                color = StrokeColor.Red,
                strokeWidth = 10f
            )
        )

        val savedArtwork = ColoringArtworkConverter.toSavedArtwork(
            page = page!!,
            fills = fills,
            freehandStrokes = freehand
        )

        assertEquals("Happy Puppy", savedArtwork.title)
        assertEquals("coloring_happy_puppy", savedArtwork.stencilId)
        assertTrue("Artwork must contain strokes", savedArtwork.strokes.isNotEmpty())

        val customFills = listOf(
            ClosedAreaFill(
                color = StrokeColor.Green,
                spans = listOf(FillSpan(y = 300f, x1 = 200f, x2 = 400f)),
                seedPoint = Point(300f, 300f)
            )
        )
        val artworkWithCustom = ColoringArtworkConverter.toSavedArtwork(
            page = page,
            fills = fills,
            freehandStrokes = freehand,
            customFills = customFills
        )
        assertTrue(artworkWithCustom.strokes.any { it.color == StrokeColor.Green })
    }
}

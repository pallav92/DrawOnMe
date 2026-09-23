package com.draw.onme.data.repository

import com.draw.onme.domain.model.DrawingTool
import com.draw.onme.domain.model.Point
import com.draw.onme.domain.model.SavedArtwork
import com.draw.onme.domain.model.Stroke
import com.draw.onme.domain.model.StrokeColor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files

class FileArtworkRepositoryTest {

    private lateinit var tempDir: File
    private lateinit var repository: FileArtworkRepository

    @Before
    fun setUp() {
        tempDir = Files.createTempDirectory("artworks_test").toFile()
        repository = FileArtworkRepository(tempDir)
    }

    @After
    fun tearDown() {
        tempDir.deleteRecursively()
    }

    @Test
    fun saveArtwork_writesToDiskAndEmitsInFlow() = runTest {
        val stroke = Stroke(
            points = listOf(Point(10f, 20f), Point(30f, 40f)),
            color = StrokeColor.Red,
            strokeWidth = 6f,
            tool = DrawingTool.PEN
        )
        val artwork = SavedArtwork(
            id = "art_1",
            title = "My House",
            strokes = listOf(stroke),
            stencilId = "cozy_house",
            magnetEmoji = "⭐️"
        )

        repository.saveArtwork(artwork)

        val file = File(tempDir, "art_1.json")
        assertTrue("JSON file must be written to disk", file.exists())

        val list = repository.observeArtworks().first()
        assertEquals(1, list.size)
        assertEquals("My House", list.first().title)
        assertEquals("cozy_house", list.first().stencilId)
    }

    @Test
    fun deleteArtwork_removesFromDiskAndFlow() = runTest {
        val artwork = SavedArtwork(
            id = "art_to_delete",
            title = "Doodle",
            strokes = emptyList()
        )

        repository.saveArtwork(artwork)
        assertEquals(1, repository.observeArtworks().first().size)

        repository.deleteArtwork("art_to_delete")

        val file = File(tempDir, "art_to_delete.json")
        assertTrue("File must be deleted from disk", !file.exists())
        assertEquals(0, repository.observeArtworks().first().size)
    }

    @Test
    fun getArtworkById_returnsArtworkIfPresent() = runTest {
        val artwork = SavedArtwork(
            id = "test_id",
            title = "Rocket Ship",
            strokes = listOf(
                Stroke(
                    points = listOf(Point(50f, 50f)),
                    color = StrokeColor.Blue,
                    strokeWidth = 12f,
                    tool = DrawingTool.PEN
                )
            ),
            magnetEmoji = "🚀"
        )

        repository.saveArtwork(artwork)

        val retrieved = repository.getArtworkById("test_id")
        assertNotNull(retrieved)
        assertEquals("Rocket Ship", retrieved?.title)
        assertEquals("🚀", retrieved?.magnetEmoji)

        val nonExistent = repository.getArtworkById("missing_id")
        assertNull(nonExistent)
    }

    @Test
    fun jsonRoundtrip_preservesAllStrokeDetails() {
        val stroke = Stroke(
            id = "stroke_123",
            points = listOf(Point(0.12f, 0.34f), Point(0.56f, 0.78f)),
            color = StrokeColor(0xFF8E24AA),
            strokeWidth = 8.5f,
            tool = DrawingTool.ERASER
        )
        val original = SavedArtwork(
            id = "roundtrip_art",
            title = "Happy Sailboat",
            createdAt = 1700000000000L,
            strokes = listOf(stroke),
            stencilId = "happy_sailboat",
            magnetEmoji = "⛵"
        )

        val json = FileArtworkRepository.artworkToJson(original)
        val restored = FileArtworkRepository.jsonToArtwork(json)

        assertEquals(original.id, restored.id)
        assertEquals(original.title, restored.title)
        assertEquals(original.createdAt, restored.createdAt)
        assertEquals(original.stencilId, restored.stencilId)
        assertEquals(original.magnetEmoji, restored.magnetEmoji)
        assertEquals(1, restored.strokes.size)

        val restoredStroke = restored.strokes.first()
        assertEquals(stroke.id, restoredStroke.id)
        assertEquals(stroke.color.argb, restoredStroke.color.argb)
        assertEquals(stroke.strokeWidth, restoredStroke.strokeWidth, 0.001f)
        assertEquals(stroke.tool, restoredStroke.tool)
        assertEquals(2, restoredStroke.points.size)
        assertEquals(0.12f, restoredStroke.points[0].x, 0.001f)
        assertEquals(0.34f, restoredStroke.points[0].y, 0.001f)
    }
}

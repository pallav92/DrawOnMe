package com.draw.onme.data.repository

import com.draw.onme.domain.model.DrawingTool
import com.draw.onme.domain.model.Point
import com.draw.onme.domain.model.Stroke
import com.draw.onme.domain.model.StrokeColor
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files

class FileBoardDraftRepositoryTest {

    private lateinit var tempDir: File
    private lateinit var repository: FileBoardDraftRepository

    @Before
    fun setUp() {
        tempDir = Files.createTempDirectory("board_drafts_test").toFile()
        repository = FileBoardDraftRepository(tempDir)
    }

    @After
    fun tearDown() {
        tempDir.deleteRecursively()
    }

    @Test
    fun getBoardStrokes_emptyInitially() = runTest {
        val strokes = repository.getBoardStrokes("magic_doodle")
        assertTrue(strokes.isEmpty())
    }

    @Test
    fun saveBoardStrokes_persistsAndLoadsAccurately() = runTest {
        val stroke = Stroke(
            points = listOf(Point(15f, 25f), Point(35f, 45f)),
            color = StrokeColor.Blue,
            strokeWidth = 12f,
            tool = DrawingTool.PEN
        )

        repository.saveBoardStrokes("magic_doodle", listOf(stroke))

        val loaded = repository.getBoardStrokes("magic_doodle")
        assertEquals(1, loaded.size)
        assertEquals(stroke.id, loaded.first().id)
        assertEquals(stroke.color, loaded.first().color)
        assertEquals(stroke.strokeWidth, loaded.first().strokeWidth)
        assertEquals(2, loaded.first().points.size)
    }

    @Test
    fun differentBoards_areCompletelyIsolated() = runTest {
        val strokeMagic = Stroke(
            points = listOf(Point(10f, 10f)),
            color = StrokeColor.Red,
            strokeWidth = 6f,
            tool = DrawingTool.PEN
        )
        val strokeStencil1 = Stroke(
            points = listOf(Point(50f, 50f)),
            color = StrokeColor.Green,
            strokeWidth = 8f,
            tool = DrawingTool.PEN
        )

        repository.saveBoardStrokes("magic_doodle", listOf(strokeMagic))
        repository.saveBoardStrokes("stencil_cozy-house", listOf(strokeStencil1))

        val magicLoaded = repository.getBoardStrokes("magic_doodle")
        val stencilLoaded = repository.getBoardStrokes("stencil_cozy-house")
        val otherStencilLoaded = repository.getBoardStrokes("stencil_zooming-car")

        assertEquals(1, magicLoaded.size)
        assertEquals(StrokeColor.Red, magicLoaded.first().color)

        assertEquals(1, stencilLoaded.size)
        assertEquals(StrokeColor.Green, stencilLoaded.first().color)

        assertTrue("Unvisited board must remain empty", otherStencilLoaded.isEmpty())
    }

    @Test
    fun clearBoard_removesFromCacheAndDisk() = runTest {
        val stroke = Stroke(
            points = listOf(Point(20f, 30f)),
            color = StrokeColor.Yellow,
            strokeWidth = 4f,
            tool = DrawingTool.PEN
        )

        repository.saveBoardStrokes("stencil_cozy-house", listOf(stroke))
        assertEquals(1, repository.getBoardStrokes("stencil_cozy-house").size)

        repository.clearBoard("stencil_cozy-house")
        assertTrue(repository.getBoardStrokes("stencil_cozy-house").isEmpty())

        // Also verify disk persistence reflects the clear
        val reloadedRepository = FileBoardDraftRepository(tempDir)
        assertTrue(reloadedRepository.getBoardStrokes("stencil_cozy-house").isEmpty())
    }

    @Test
    fun reloadFromDisk_survivesRepositoryRecreation() = runTest {
        val stroke = Stroke(
            points = listOf(Point(100f, 200f)),
            color = StrokeColor.Purple,
            strokeWidth = 14f,
            tool = DrawingTool.PEN
        )

        repository.saveBoardStrokes("magic_doodle", listOf(stroke))

        // New repository instance pointing to same directory simulating process restart
        val freshRepository = FileBoardDraftRepository(tempDir)
        val loaded = freshRepository.getBoardStrokes("magic_doodle")

        assertEquals(1, loaded.size)
        assertEquals(stroke.id, loaded.first().id)
        assertEquals(Point(100f, 200f), loaded.first().points.first())
    }
}

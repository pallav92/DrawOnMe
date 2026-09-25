package com.draw.onme.data.repository

import com.draw.onme.domain.model.ColoringDraft
import com.draw.onme.domain.model.DrawingTool
import com.draw.onme.domain.model.Point
import com.draw.onme.domain.model.Stroke
import com.draw.onme.domain.model.StrokeColor
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.io.File

class FileColoringDraftRepositoryTest {

    private lateinit var tempDir: File
    private lateinit var repository: FileColoringDraftRepository

    @Before
    fun setUp() {
        tempDir = File(System.getProperty("java.io.tmpdir"), "test_coloring_drafts_${System.currentTimeMillis()}")
        tempDir.mkdirs()
        repository = FileColoringDraftRepository(tempDir)
    }

    @After
    fun tearDown() {
        tempDir.deleteRecursively()
    }

    @Test
    fun testSaveAndGetDraft_restoresFillsAndStrokes() = runBlocking {
        val draft = ColoringDraft(
            pageId = "happy_puppy",
            fills = mapOf(
                "head" to StrokeColor.Yellow,
                "left_ear" to StrokeColor.Orange
            ),
            strokes = listOf(
                Stroke(
                    points = listOf(Point(100f, 100f), Point(120f, 130f)),
                    color = StrokeColor.Blue,
                    strokeWidth = 12f,
                    tool = DrawingTool.PEN
                )
            )
        )

        repository.saveDraft(draft)

        val loaded = repository.getDraft("happy_puppy")
        assertNotNull(loaded)
        assertEquals("happy_puppy", loaded?.pageId)
        assertEquals(2, loaded?.fills?.size)
        assertEquals(StrokeColor.Yellow.argb, loaded?.fills?.get("head")?.argb)
        assertEquals(1, loaded?.strokes?.size)
        assertEquals(2, loaded?.strokes?.first()?.points?.size)
    }

    @Test
    fun testClearDraft_removesDraft() = runBlocking {
        val draft = ColoringDraft(
            pageId = "magic_castle",
            fills = mapOf("gate" to StrokeColor.Purple)
        )
        repository.saveDraft(draft)
        assertNotNull(repository.getDraft("magic_castle"))

        repository.clearDraft("magic_castle")
        assertNull(repository.getDraft("magic_castle"))
    }

    @Test
    fun testSaveAndGetDraft_restoresCustomFills() = runBlocking {
        val draft = ColoringDraft(
            pageId = "magic_castle",
            customFills = listOf(
                com.draw.onme.domain.model.ClosedAreaFill(
                    color = StrokeColor.Green,
                    spans = listOf(com.draw.onme.domain.model.FillSpan(200f, 100f, 300f)),
                    seedPoint = Point(200f, 200f),
                    regionId = "tower"
                )
            )
        )
        repository.saveDraft(draft)
        val loaded = repository.getDraft("magic_castle")
        assertNotNull(loaded)
        assertEquals(1, loaded?.customFills?.size)
        assertEquals(StrokeColor.Green.argb, loaded?.customFills?.first()?.color?.argb)
        assertEquals(1, loaded?.customFills?.first()?.spans?.size)
        assertEquals(200f, loaded?.customFills?.first()?.spans?.first()?.y)
    }
}

package com.draw.onme.presentation.coloring

import com.draw.onme.data.repository.InMemoryColoringBookRepository
import com.draw.onme.domain.model.ColoringDraft
import com.draw.onme.domain.model.ColoringTool
import com.draw.onme.domain.model.Point
import com.draw.onme.domain.model.StrokeColor
import com.draw.onme.domain.repository.ColoringDraftRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ColoringViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val bookRepo = InMemoryColoringBookRepository()

    private class FakeColoringDraftRepository : ColoringDraftRepository {
        var savedDraft: ColoringDraft? = null

        override suspend fun getDraft(pageId: String): ColoringDraft? = savedDraft
        override suspend fun saveDraft(draft: ColoringDraft) { savedDraft = draft }
        override suspend fun clearDraft(pageId: String) { savedDraft = null }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testTapCanvas_fillsHitRegion() = runTest {
        val viewModel = ColoringViewModel("happy_puppy", bookRepo)
        // Forehead of puppy is at (500, 280)
        viewModel.onAction(ColoringAction.SelectColor(StrokeColor.Yellow))
        viewModel.onAction(ColoringAction.TapCanvas(Point(500f, 280f)))

        val state = viewModel.uiState.value
        assertEquals(StrokeColor.Yellow.argb, state.fills["head"]?.argb)
        assertTrue(state.canUndo)
        assertFalse(state.canRedo)
    }

    @Test
    fun testTapCanvas_missDoesNotTriggerFill() = runTest {
        val viewModel = ColoringViewModel("happy_puppy", bookRepo)
        // Corner (20, 20) is empty background
        viewModel.onAction(ColoringAction.TapCanvas(Point(20f, 20f)))

        val state = viewModel.uiState.value
        assertTrue(state.fills.isEmpty())
        assertFalse(state.canUndo)
    }

    @Test
    fun testUndoRedo_forBucketFills() = runTest {
        val viewModel = ColoringViewModel("happy_puppy", bookRepo)
        viewModel.onAction(ColoringAction.SelectColor(StrokeColor.Blue))
        viewModel.onAction(ColoringAction.TapCanvas(Point(500f, 280f))) // Forehead

        assertEquals(1, viewModel.uiState.value.fills.size)

        viewModel.onAction(ColoringAction.Undo)
        assertEquals(0, viewModel.uiState.value.fills.size)
        assertTrue(viewModel.uiState.value.canRedo)

        viewModel.onAction(ColoringAction.Redo)
        assertEquals(1, viewModel.uiState.value.fills.size)
        assertEquals(StrokeColor.Blue.argb, viewModel.uiState.value.fills["head"]?.argb)
    }

    @Test
    fun testMagicCrayonStroke_lifecycle_canDrawAnywhere() = runTest {
        val viewModel = ColoringViewModel("happy_puppy", bookRepo)
        viewModel.onAction(ColoringAction.SelectTool(ColoringTool.MAGIC_CRAYON))
        viewModel.onAction(ColoringAction.StartStroke(Point(500f, 280f)))

        assertNotNull(viewModel.uiState.value.currentStroke)
        // Crayon is unconstrained to allow drawing anywhere
        assertEquals(null, viewModel.uiState.value.activeRegionIdForStroke)

        viewModel.onAction(ColoringAction.AddPoint(Point(510f, 390f)))
        viewModel.onAction(ColoringAction.AddPoint(Point(520f, 400f)))
        viewModel.onAction(ColoringAction.EndStroke)

        val state = viewModel.uiState.value
        assertEquals(1, state.strokes.size)
        assertEquals(3, state.strokes.first().points.size)
        assertTrue(state.canUndo)
    }

    @Test
    fun testTemplate_supportsBothBucketAndCrayon() = runTest {
        val viewModel = ColoringViewModel("happy_puppy", bookRepo)
        val state = viewModel.uiState.value
        assertTrue("Templates must support bucket fill", state.supportsBucketFill)
        assertEquals(ColoringTool.BUCKET, state.selectedTool)

        // 1. Bucket fill on puppy forehead
        viewModel.onAction(ColoringAction.SelectColor(StrokeColor.Orange))
        viewModel.onAction(ColoringAction.TapCanvas(Point(500f, 280f)))
        assertEquals(StrokeColor.Orange.argb, viewModel.uiState.value.fills["head"]?.argb)

        // 2. Switch to crayon and draw
        viewModel.onAction(ColoringAction.SelectTool(ColoringTool.MAGIC_CRAYON))
        assertEquals(ColoringTool.MAGIC_CRAYON, viewModel.uiState.value.selectedTool)

        viewModel.onAction(ColoringAction.StartStroke(Point(200f, 200f)))
        viewModel.onAction(ColoringAction.AddPoint(Point(250f, 250f)))
        viewModel.onAction(ColoringAction.EndStroke)

        val updatedState = viewModel.uiState.value
        assertEquals(1, updatedState.fills.size)
        assertEquals(1, updatedState.strokes.size)
    }

    @Test
    fun testClearConfirmation_clearsFillsAndStrokes() = runTest {
        val viewModel = ColoringViewModel("happy_puppy", bookRepo)
        viewModel.onAction(ColoringAction.TapCanvas(Point(500f, 380f)))
        assertEquals(1, viewModel.uiState.value.fills.size)

        viewModel.onAction(ColoringAction.RequestClear)
        assertTrue(viewModel.uiState.value.showClearDialog)

        viewModel.onAction(ColoringAction.ConfirmClear)
        assertFalse(viewModel.uiState.value.showClearDialog)
        assertTrue(viewModel.uiState.value.fills.isEmpty())
    }

    @Test
    fun testDraftPersistence_restoresOnLoad() = runTest {
        val fakeDraftRepo = FakeColoringDraftRepository()
        fakeDraftRepo.savedDraft = ColoringDraft(
            pageId = "happy_puppy",
            fills = mapOf("head" to StrokeColor.Green)
        )

        val viewModel = ColoringViewModel("happy_puppy", bookRepo, fakeDraftRepo)
        advanceUntilIdle()

        assertEquals(StrokeColor.Green.argb, viewModel.uiState.value.fills["head"]?.argb)
    }

    @Test
    fun testSelectColor_prependsCustomColorToAvailableColors() = runTest {
        val viewModel = ColoringViewModel("happy_puppy", bookRepo)
        val customColor = StrokeColor(0xFF00E5FF) // Electric Cyan

        viewModel.onAction(ColoringAction.SelectColor(customColor))

        val state = viewModel.uiState.value
        assertEquals(customColor.argb, state.selectedColor.argb)
        assertEquals(customColor.argb, state.availableColors.first().argb)
    }

    @Test
    fun testBlankCanvas_usesCrayonsOnly_cannotSelectBucket() = runTest {
        val viewModel = ColoringViewModel(InMemoryColoringBookRepository.BLANK_CANVAS_ID, bookRepo)
        val initialPage = viewModel.uiState.value.page
        assertNotNull(initialPage)
        assertTrue(initialPage!!.outlines.isEmpty())
        assertTrue(initialPage.regions.isEmpty())

        val state = viewModel.uiState.value
        assertFalse("Blank canvas must not support bucket fill", state.supportsBucketFill)
        assertEquals(ColoringTool.MAGIC_CRAYON, state.selectedTool)

        // Attempting to select bucket should be ignored
        viewModel.onAction(ColoringAction.SelectTool(ColoringTool.BUCKET))
        assertEquals(ColoringTool.MAGIC_CRAYON, viewModel.uiState.value.selectedTool)

        // Drawing with crayon adds stroke
        viewModel.onAction(ColoringAction.StartStroke(Point(250f, 250f)))
        viewModel.onAction(ColoringAction.AddPoint(Point(500f, 500f)))
        viewModel.onAction(ColoringAction.EndStroke)

        val updatedState = viewModel.uiState.value
        assertEquals(1, updatedState.strokes.size)
        assertTrue(updatedState.canUndo)

        // Tapping canvas does not add any fills
        viewModel.onAction(ColoringAction.TapCanvas(Point(300f, 300f)))
        assertTrue(viewModel.uiState.value.fills.isEmpty())
    }
}

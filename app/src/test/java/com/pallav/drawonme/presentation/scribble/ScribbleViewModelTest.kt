package com.pallav.drawonme.presentation.scribble

import com.pallav.drawonme.domain.model.DrawingTool
import com.pallav.drawonme.domain.model.Point
import com.pallav.drawonme.domain.model.StrokeColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ScribbleViewModelTest {

    private lateinit var viewModel: ScribbleViewModel

    @Before
    fun setUp() {
        viewModel = ScribbleViewModel()
    }

    @Test
    fun initialState_hasDefaultValues() {
        val state = viewModel.uiState.value

        assertTrue(state.strokes.isEmpty())
        assertNull(state.currentStroke)
        assertEquals(DrawingTool.PEN, state.selectedTool)
        assertEquals(StrokeColor.Black, state.selectedColor)
        assertEquals(6f, state.strokeWidth)
        assertFalse(state.canUndo)
        assertFalse(state.canRedo)
        assertFalse(state.showClearDialog)
    }

    @Test
    fun drawingStroke_updatesCurrentStrokeAndCommitsOnEnd() {
        viewModel.onAction(ScribbleAction.StartStroke(Point(10f, 20f)))
        var state = viewModel.uiState.value
        assertNotNull(state.currentStroke)
        assertEquals(1, state.currentStroke?.points?.size)
        assertEquals(Point(10f, 20f), state.currentStroke?.points?.first())

        viewModel.onAction(ScribbleAction.AddPoint(Point(20f, 30f)))
        state = viewModel.uiState.value
        assertEquals(2, state.currentStroke?.points?.size)

        viewModel.onAction(ScribbleAction.EndStroke)
        state = viewModel.uiState.value
        assertNull(state.currentStroke)
        assertEquals(1, state.strokes.size)
        assertEquals(2, state.strokes.first().points.size)
        assertTrue(state.canUndo)
        assertFalse(state.canRedo)
    }

    @Test
    fun jitterPoints_filteredOut() {
        viewModel.onAction(ScribbleAction.StartStroke(Point(10f, 10f)))
        // Difference is 0.5px on each axis -> dx*dx + dy*dy = 0.5 < 4f
        viewModel.onAction(ScribbleAction.AddPoint(Point(10.5f, 10.5f)))

        val state = viewModel.uiState.value
        assertEquals(1, state.currentStroke?.points?.size)
    }

    @Test
    fun undo_revertsLastCommittedStroke() {
        // Draw stroke 1
        viewModel.onAction(ScribbleAction.StartStroke(Point(10f, 10f)))
        viewModel.onAction(ScribbleAction.AddPoint(Point(20f, 20f)))
        viewModel.onAction(ScribbleAction.EndStroke)

        // Draw stroke 2
        viewModel.onAction(ScribbleAction.StartStroke(Point(30f, 30f)))
        viewModel.onAction(ScribbleAction.AddPoint(Point(40f, 40f)))
        viewModel.onAction(ScribbleAction.EndStroke)

        assertEquals(2, viewModel.uiState.value.strokes.size)
        assertTrue(viewModel.uiState.value.canUndo)

        // First undo: reverts stroke 2
        viewModel.onAction(ScribbleAction.Undo)
        assertEquals(1, viewModel.uiState.value.strokes.size)
        assertTrue(viewModel.uiState.value.canUndo)
        assertTrue(viewModel.uiState.value.canRedo)

        // Second undo: reverts stroke 1
        viewModel.onAction(ScribbleAction.Undo)
        assertTrue(viewModel.uiState.value.strokes.isEmpty())
        assertFalse(viewModel.uiState.value.canUndo)
        assertTrue(viewModel.uiState.value.canRedo)
    }

    @Test
    fun redo_restoresUndoneStroke() {
        viewModel.onAction(ScribbleAction.StartStroke(Point(10f, 10f)))
        viewModel.onAction(ScribbleAction.AddPoint(Point(20f, 20f)))
        viewModel.onAction(ScribbleAction.EndStroke)

        viewModel.onAction(ScribbleAction.Undo)
        assertTrue(viewModel.uiState.value.strokes.isEmpty())

        viewModel.onAction(ScribbleAction.Redo)
        assertEquals(1, viewModel.uiState.value.strokes.size)
        assertTrue(viewModel.uiState.value.canUndo)
        assertFalse(viewModel.uiState.value.canRedo)
    }

    @Test
    fun newStroke_clearsRedoStack() {
        viewModel.onAction(ScribbleAction.StartStroke(Point(10f, 10f)))
        viewModel.onAction(ScribbleAction.AddPoint(Point(20f, 20f)))
        viewModel.onAction(ScribbleAction.EndStroke)

        viewModel.onAction(ScribbleAction.Undo)
        assertTrue(viewModel.uiState.value.canRedo)

        // Drawing a new stroke after undo should invalidate redo
        viewModel.onAction(ScribbleAction.StartStroke(Point(50f, 50f)))
        viewModel.onAction(ScribbleAction.AddPoint(Point(60f, 60f)))
        viewModel.onAction(ScribbleAction.EndStroke)

        assertFalse(viewModel.uiState.value.canRedo)
        assertEquals(1, viewModel.uiState.value.strokes.size)
    }

    @Test
    fun clearCanvas_clearsAllStrokesAndSupportsUndo() {
        viewModel.onAction(ScribbleAction.StartStroke(Point(10f, 10f)))
        viewModel.onAction(ScribbleAction.AddPoint(Point(20f, 20f)))
        viewModel.onAction(ScribbleAction.EndStroke)

        viewModel.onAction(ScribbleAction.ConfirmClearCanvas)
        assertTrue(viewModel.uiState.value.strokes.isEmpty())
        assertTrue(viewModel.uiState.value.canUndo)

        // Undo the clear action to restore previous drawings
        viewModel.onAction(ScribbleAction.Undo)
        assertEquals(1, viewModel.uiState.value.strokes.size)
    }

    @Test
    fun toolAndColorSelection_updatesState() {
        viewModel.onAction(ScribbleAction.SelectTool(DrawingTool.ERASER))
        assertEquals(DrawingTool.ERASER, viewModel.uiState.value.selectedTool)

        // Selecting a color switches tool back to PEN
        viewModel.onAction(ScribbleAction.SelectColor(StrokeColor.Blue))
        assertEquals(StrokeColor.Blue, viewModel.uiState.value.selectedColor)
        assertEquals(DrawingTool.PEN, viewModel.uiState.value.selectedTool)

        viewModel.onAction(ScribbleAction.SetStrokeWidth(12f))
        assertEquals(12f, viewModel.uiState.value.strokeWidth)
    }

    @Test
    fun clearDialog_requestAndDismiss() {
        // When canvas is empty, request clear does nothing
        viewModel.onAction(ScribbleAction.RequestClearCanvas)
        assertFalse(viewModel.uiState.value.showClearDialog)

        // Draw a stroke
        viewModel.onAction(ScribbleAction.StartStroke(Point(10f, 10f)))
        viewModel.onAction(ScribbleAction.EndStroke)

        viewModel.onAction(ScribbleAction.RequestClearCanvas)
        assertTrue(viewModel.uiState.value.showClearDialog)

        viewModel.onAction(ScribbleAction.DismissClearCanvas)
        assertFalse(viewModel.uiState.value.showClearDialog)
    }
}

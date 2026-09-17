package com.pallav.drawonme.presentation.scribble

import androidx.lifecycle.ViewModel
import com.pallav.drawonme.domain.model.DrawingTool
import com.pallav.drawonme.domain.model.Point
import com.pallav.drawonme.domain.model.Stroke
import com.pallav.drawonme.domain.model.StrokeColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing the state machine for the scribble board.
 * Implements Unidirectional Data Flow (UDF) with atomic undo/redo history snapshots.
 */
class ScribbleViewModel : ViewModel() {

    private val undoHistory: ArrayDeque<List<Stroke>> = ArrayDeque()
    private val redoHistory: ArrayDeque<List<Stroke>> = ArrayDeque()

    private val _uiState: MutableStateFlow<ScribbleUiState> = MutableStateFlow(ScribbleUiState())
    val uiState: StateFlow<ScribbleUiState> = _uiState.asStateFlow()

    fun onAction(action: ScribbleAction) {
        when (action) {
            is ScribbleAction.StartStroke -> handleStartStroke(action.point)
            is ScribbleAction.AddPoint -> handleAddPoint(action.point)
            is ScribbleAction.EndStroke -> handleEndStroke()
            is ScribbleAction.SelectTool -> handleSelectTool(action.tool)
            is ScribbleAction.SelectColor -> handleSelectColor(action.color)
            is ScribbleAction.SetStrokeWidth -> handleSetStrokeWidth(action.width)
            is ScribbleAction.Undo -> handleUndo()
            is ScribbleAction.Redo -> handleRedo()
            is ScribbleAction.RequestClearCanvas -> handleRequestClear()
            is ScribbleAction.ConfirmClearCanvas -> handleConfirmClear()
            is ScribbleAction.DismissClearCanvas -> handleDismissClear()
        }
    }

    private fun handleStartStroke(point: Point) {
        val currentState = _uiState.value
        val newStroke = Stroke(
            points = listOf(point),
            color = currentState.selectedColor,
            strokeWidth = currentState.strokeWidth,
            tool = currentState.selectedTool
        )
        _uiState.update { it.copy(currentStroke = newStroke) }
    }

    private fun handleAddPoint(point: Point) {
        val active = _uiState.value.currentStroke ?: return
        val lastPoint = active.points.lastOrNull()
        if (lastPoint != null) {
            val dx = point.x - lastPoint.x
            val dy = point.y - lastPoint.y
            // Filter jitter and redundant points within 2 pixels
            if (dx * dx + dy * dy < 4f) {
                return
            }
        }
        _uiState.update {
            it.copy(currentStroke = active.copy(points = active.points + point))
        }
    }

    private fun handleEndStroke() {
        val active = _uiState.value.currentStroke ?: return
        if (active.points.isEmpty()) {
            _uiState.update { it.copy(currentStroke = null) }
            return
        }

        pushToUndoHistory(_uiState.value.strokes)
        redoHistory.clear()

        val updatedStrokes = _uiState.value.strokes + active
        _uiState.update {
            it.copy(
                strokes = updatedStrokes,
                currentStroke = null,
                canUndo = true,
                canRedo = false
            )
        }
    }

    private fun handleSelectTool(tool: DrawingTool) {
        _uiState.update { it.copy(selectedTool = tool) }
    }

    private fun handleSelectColor(color: StrokeColor) {
        _uiState.update {
            it.copy(
                selectedColor = color,
                selectedTool = DrawingTool.PEN // Automatically switch back to Pen when color is selected
            )
        }
    }

    private fun handleSetStrokeWidth(width: Float) {
        _uiState.update { it.copy(strokeWidth = width) }
    }

    private fun handleUndo() {
        if (undoHistory.isEmpty()) return

        val previousSnapshot = undoHistory.removeLast()
        redoHistory.addLast(_uiState.value.strokes)

        _uiState.update {
            it.copy(
                strokes = previousSnapshot,
                currentStroke = null,
                canUndo = undoHistory.isNotEmpty(),
                canRedo = true
            )
        }
    }

    private fun handleRedo() {
        if (redoHistory.isEmpty()) return

        val nextSnapshot = redoHistory.removeLast()
        pushToUndoHistory(_uiState.value.strokes)

        _uiState.update {
            it.copy(
                strokes = nextSnapshot,
                currentStroke = null,
                canUndo = true,
                canRedo = redoHistory.isNotEmpty()
            )
        }
    }

    private fun handleRequestClear() {
        if (_uiState.value.strokes.isNotEmpty() || _uiState.value.currentStroke != null) {
            _uiState.update { it.copy(showClearDialog = true) }
        }
    }

    private fun handleConfirmClear() {
        if (_uiState.value.strokes.isNotEmpty()) {
            pushToUndoHistory(_uiState.value.strokes)
            redoHistory.clear()
        }
        _uiState.update {
            it.copy(
                strokes = emptyList(),
                currentStroke = null,
                canUndo = undoHistory.isNotEmpty(),
                canRedo = false,
                showClearDialog = false
            )
        }
    }

    private fun handleDismissClear() {
        _uiState.update { it.copy(showClearDialog = false) }
    }

    private fun pushToUndoHistory(snapshot: List<Stroke>) {
        if (undoHistory.size >= MAX_HISTORY_SIZE) {
            undoHistory.removeFirst()
        }
        undoHistory.addLast(snapshot)
    }

    companion object {
        private const val MAX_HISTORY_SIZE: Int = 50
    }
}

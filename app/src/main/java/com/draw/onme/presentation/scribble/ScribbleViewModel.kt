package com.draw.onme.presentation.scribble

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.draw.onme.domain.model.DrawingTool
import com.draw.onme.domain.model.Point
import com.draw.onme.domain.model.Stroke
import com.draw.onme.domain.model.StrokeColor
import com.draw.onme.domain.repository.BoardDraftRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing the state machine for an isolated scribble board.
 * Implements Unidirectional Data Flow (UDF) with atomic undo/redo history snapshots,
 * and automatically persists strokes to [BoardDraftRepository] keyed by [boardId].
 *
 * @param boardId Unique board identifier (e.g. "magic_doodle", "stencil_cozy-house").
 * @param boardDraftRepository Optional repository for loading and saving board strokes.
 */
class ScribbleViewModel(
    val boardId: String = BOARD_MAGIC_DOODLE,
    private val boardDraftRepository: BoardDraftRepository? = null
) : ViewModel() {

    private val undoHistory: ArrayDeque<List<Stroke>> = ArrayDeque()
    private val redoHistory: ArrayDeque<List<Stroke>> = ArrayDeque()

    private val _uiState: MutableStateFlow<ScribbleUiState> = MutableStateFlow(ScribbleUiState())
    val uiState: StateFlow<ScribbleUiState> = _uiState.asStateFlow()

    init {
        if (boardDraftRepository != null) {
            viewModelScope.launch {
                val saved = boardDraftRepository.getBoardStrokes(boardId)
                if (saved.isNotEmpty()) {
                    _uiState.update { it.copy(strokes = saved) }
                }
            }
        }
    }

    fun onAction(action: ScribbleAction) {
        when (action) {
            is ScribbleAction.StartStroke -> handleStartStroke(action.point)
            is ScribbleAction.AddPoint -> handleAddPoint(action.point)
            is ScribbleAction.EndStroke -> handleEndStroke()
            is ScribbleAction.CancelStroke -> handleCancelStroke()
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
        persistBoardStrokes(updatedStrokes)
    }

    private fun handleCancelStroke() {
        if (_uiState.value.currentStroke != null) {
            _uiState.update { it.copy(currentStroke = null) }
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
        persistBoardStrokes(previousSnapshot)
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
        persistBoardStrokes(nextSnapshot)
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
        persistBoardStrokes(emptyList())
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

    private fun persistBoardStrokes(strokes: List<Stroke>) {
        if (boardDraftRepository != null) {
            viewModelScope.launch {
                boardDraftRepository.saveBoardStrokes(boardId, strokes)
            }
        }
    }

    companion object {
        private const val MAX_HISTORY_SIZE: Int = 50

        const val BOARD_MAGIC_DOODLE: String = "magic_doodle"

        fun stencilBoardId(stencilId: String): String = "stencil_$stencilId"

        fun provideFactory(
            boardId: String,
            repository: BoardDraftRepository? = null
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ScribbleViewModel(boardId = boardId, boardDraftRepository = repository) as T
            }
        }
    }
}

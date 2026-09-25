package com.draw.onme.presentation.coloring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.draw.onme.domain.model.ClosedAreaFill
import com.draw.onme.domain.model.ClosedAreaFloodFiller
import com.draw.onme.domain.model.ColoringDraft
import com.draw.onme.domain.model.ColoringTool
import com.draw.onme.domain.model.DrawingTool
import com.draw.onme.domain.model.Point
import com.draw.onme.domain.model.PointInPolygon
import com.draw.onme.domain.model.Stroke
import com.draw.onme.domain.model.StrokeColor
import com.draw.onme.domain.repository.ColoringBookRepository
import com.draw.onme.domain.repository.ColoringDraftRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Snapshot for atomic undo/redo of coloring operations (fills, custom closed area fills, and strokes).
 */
data class ColoringSnapshot(
    val fills: Map<String, StrokeColor>,
    val customFills: List<ClosedAreaFill>,
    val strokes: List<Stroke>
)

/**
 * ViewModel managing the state machine for the Coloring Book Studio.
 * Handles Tap-to-Fill bucket hits on arbitrary closed areas (outlines and user strokes),
 * unconstrained freehand crayon drawing anywhere on the canvas,
 * atomic undo/redo history, and auto-saving drafts.
 *
 * @param pageId ID of the coloring page being worked on.
 * @param bookRepository Repository providing coloring page templates.
 * @param draftRepository Optional repository for loading and saving coloring drafts.
 */
class ColoringViewModel(
    val pageId: String,
    private val bookRepository: ColoringBookRepository,
    private val draftRepository: ColoringDraftRepository? = null
) : ViewModel() {

    private val undoHistory: ArrayDeque<ColoringSnapshot> = ArrayDeque()
    private val redoHistory: ArrayDeque<ColoringSnapshot> = ArrayDeque()

    private val loadedPage = bookRepository.getPageById(pageId)

    private val _uiState: MutableStateFlow<ColoringUiState> = MutableStateFlow(
        ColoringUiState(
            page = loadedPage,
            selectedTool = if (loadedPage != null && loadedPage.regions.isNotEmpty()) {
                ColoringTool.BUCKET
            } else {
                ColoringTool.MAGIC_CRAYON
            }
        )
    )
    val uiState: StateFlow<ColoringUiState> = _uiState.asStateFlow()

    init {
        if (draftRepository != null) {
            viewModelScope.launch {
                val saved = draftRepository.getDraft(pageId)
                if (saved != null) {
                    _uiState.update { current ->
                        current.copy(
                            fills = saved.fills,
                            customFills = saved.customFills,
                            strokes = saved.strokes,
                            canUndo = false,
                            canRedo = false
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: ColoringAction) {
        when (action) {
            is ColoringAction.TapCanvas -> handleTapCanvas(action.point)
            is ColoringAction.StartStroke -> handleStartStroke(action.point)
            is ColoringAction.AddPoint -> handleAddPoint(action.point)
            is ColoringAction.EndStroke -> handleEndStroke()
            is ColoringAction.CancelStroke -> handleCancelStroke()
            is ColoringAction.SelectTool -> handleSelectTool(action.tool)
            is ColoringAction.SelectColor -> handleSelectColor(action.color)
            is ColoringAction.SetStrokeWidth -> handleSetStrokeWidth(action.width)
            is ColoringAction.Undo -> handleUndo()
            is ColoringAction.Redo -> handleRedo()
            is ColoringAction.RequestClear -> handleRequestClear()
            is ColoringAction.ConfirmClear -> handleConfirmClear()
            is ColoringAction.DismissClear -> handleDismissClear()
        }
    }

    private fun handleTapCanvas(point: Point) {
        val page = _uiState.value.page ?: return
        // Pre-made templates support bucket tap-to-fill; user creations use crayons only
        if (page.regions.isEmpty()) return

        val newColor = _uiState.value.selectedColor
        val normalized = Point(
            x = (point.x / CANONICAL_SIZE).coerceIn(0f, 1f),
            y = (point.y / CANONICAL_SIZE).coerceIn(0f, 1f)
        )

        // Check if tap point falls inside a predefined vector template region
        val hitRegion = page.regions.lastOrNull { region ->
            PointInPolygon.contains(normalized, region.boundaryPoints)
        } ?: return

        pushToUndoHistory()
        redoHistory.clear()

        val currentFills = _uiState.value.fills
        val updatedFills = currentFills + (hitRegion.id to newColor)

        _uiState.update {
            it.copy(
                fills = updatedFills,
                canUndo = true,
                canRedo = false
            )
        }
        persistDraft()
    }

    private fun handleStartStroke(point: Point) {
        val currentState = _uiState.value
        val newStroke = Stroke(
            points = listOf(point),
            color = currentState.selectedColor,
            strokeWidth = currentState.strokeWidth,
            tool = DrawingTool.PEN
        )

        _uiState.update {
            it.copy(
                currentStroke = newStroke,
                activeRegionIdForStroke = null // Freehand drawing anywhere on canvas!
            )
        }
    }

    private fun handleAddPoint(point: Point) {
        val active = _uiState.value.currentStroke ?: return
        val lastPoint = active.points.lastOrNull()
        if (lastPoint != null) {
            val dx = point.x - lastPoint.x
            val dy = point.y - lastPoint.y
            if (dx * dx + dy * dy < 4f) return
        }
        _uiState.update {
            it.copy(currentStroke = active.copy(points = active.points + point))
        }
    }

    private fun handleEndStroke() {
        val active = _uiState.value.currentStroke ?: return
        if (active.points.isEmpty()) {
            _uiState.update { it.copy(currentStroke = null, activeRegionIdForStroke = null) }
            return
        }

        pushToUndoHistory()
        redoHistory.clear()

        val updatedStrokes = _uiState.value.strokes + active
        _uiState.update {
            it.copy(
                strokes = updatedStrokes,
                currentStroke = null,
                activeRegionIdForStroke = null,
                canUndo = true,
                canRedo = false
            )
        }
        persistDraft()
    }

    private fun handleCancelStroke() {
        _uiState.update {
            it.copy(currentStroke = null, activeRegionIdForStroke = null)
        }
    }

    private fun handleSelectTool(tool: ColoringTool) {
        if (!_uiState.value.supportsBucketFill && tool == ColoringTool.BUCKET) {
            return
        }
        _uiState.update { it.copy(selectedTool = tool) }
    }

    private fun handleSelectColor(color: StrokeColor) {
        _uiState.update { state ->
            val updatedColors = if (state.availableColors.any { it.argb == color.argb }) {
                state.availableColors
            } else {
                listOf(color) + state.availableColors
            }
            state.copy(
                selectedColor = color,
                availableColors = updatedColors
            )
        }
    }

    private fun handleSetStrokeWidth(width: Float) {
        _uiState.update { it.copy(strokeWidth = width) }
    }

    private fun handleUndo() {
        if (undoHistory.isEmpty()) return

        val previous = undoHistory.removeLast()
        val current = _uiState.value
        redoHistory.addLast(ColoringSnapshot(current.fills, current.customFills, current.strokes))

        _uiState.update {
            it.copy(
                fills = previous.fills,
                customFills = previous.customFills,
                strokes = previous.strokes,
                currentStroke = null,
                activeRegionIdForStroke = null,
                canUndo = undoHistory.isNotEmpty(),
                canRedo = true
            )
        }
        persistDraft()
    }

    private fun handleRedo() {
        if (redoHistory.isEmpty()) return

        val next = redoHistory.removeLast()
        val current = _uiState.value
        undoHistory.addLast(ColoringSnapshot(current.fills, current.customFills, current.strokes))

        _uiState.update {
            it.copy(
                fills = next.fills,
                customFills = next.customFills,
                strokes = next.strokes,
                currentStroke = null,
                activeRegionIdForStroke = null,
                canUndo = true,
                canRedo = redoHistory.isNotEmpty()
            )
        }
        persistDraft()
    }

    private fun handleRequestClear() {
        val s = _uiState.value
        if (s.fills.isNotEmpty() || s.customFills.isNotEmpty() || s.strokes.isNotEmpty()) {
            _uiState.update { it.copy(showClearDialog = true) }
        }
    }

    private fun handleConfirmClear() {
        val s = _uiState.value
        if (s.fills.isNotEmpty() || s.customFills.isNotEmpty() || s.strokes.isNotEmpty()) {
            pushToUndoHistory()
            redoHistory.clear()
        }

        _uiState.update {
            it.copy(
                fills = emptyMap(),
                customFills = emptyList(),
                strokes = emptyList(),
                currentStroke = null,
                activeRegionIdForStroke = null,
                canUndo = undoHistory.isNotEmpty(),
                canRedo = false,
                showClearDialog = false
            )
        }
        persistDraft()
    }

    private fun handleDismissClear() {
        _uiState.update { it.copy(showClearDialog = false) }
    }

    private fun pushToUndoHistory() {
        if (undoHistory.size >= MAX_HISTORY_SIZE) {
            undoHistory.removeFirst()
        }
        val current = _uiState.value
        undoHistory.addLast(ColoringSnapshot(current.fills, current.customFills, current.strokes))
    }

    private fun persistDraft() {
        if (draftRepository != null) {
            val state = _uiState.value
            viewModelScope.launch {
                draftRepository.saveDraft(
                    ColoringDraft(
                        pageId = pageId,
                        fills = state.fills,
                        strokes = state.strokes,
                        customFills = state.customFills
                    )
                )
            }
        }
    }

    companion object {
        const val CANONICAL_SIZE: Float = 1000f
        private const val MAX_HISTORY_SIZE: Int = 30

        fun provideFactory(
            pageId: String,
            bookRepository: ColoringBookRepository,
            draftRepository: ColoringDraftRepository? = null
        ): androidx.lifecycle.ViewModelProvider.Factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ColoringViewModel(pageId, bookRepository, draftRepository) as T
            }
        }
    }
}

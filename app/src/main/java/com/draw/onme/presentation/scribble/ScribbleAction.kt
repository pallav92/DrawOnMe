package com.draw.onme.presentation.scribble

import com.draw.onme.domain.model.DrawingTool
import com.draw.onme.domain.model.Point
import com.draw.onme.domain.model.StrokeColor

/**
 * Discrete actions dispatched from the UI to the ScribbleViewModel.
 * Adheres strictly to Unidirectional Data Flow (UDF).
 */
sealed interface ScribbleAction {
    data class StartStroke(val point: Point) : ScribbleAction
    data class AddPoint(val point: Point) : ScribbleAction
    data object EndStroke : ScribbleAction
    data object CancelStroke : ScribbleAction
    data class SelectTool(val tool: DrawingTool) : ScribbleAction
    data class SelectColor(val color: StrokeColor) : ScribbleAction
    data class SetStrokeWidth(val width: Float) : ScribbleAction
    data object Undo : ScribbleAction
    data object Redo : ScribbleAction
    data object RequestClearCanvas : ScribbleAction
    data object ConfirmClearCanvas : ScribbleAction
    data object DismissClearCanvas : ScribbleAction
}

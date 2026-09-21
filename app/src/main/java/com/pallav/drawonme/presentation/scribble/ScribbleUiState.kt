package com.pallav.drawonme.presentation.scribble

import androidx.compose.runtime.Immutable
import com.pallav.drawonme.domain.model.DrawingTool
import com.pallav.drawonme.domain.model.Stroke
import com.pallav.drawonme.domain.model.StrokeColor

/**
 * Immutable UI State representing the scribble board screen.
 * Annotated with @Immutable to guarantee Compose compiler stability and prevent unnecessary recompositions.
 */
@Immutable
data class ScribbleUiState(
    val strokes: List<Stroke> = emptyList(),
    val currentStroke: Stroke? = null,
    val selectedTool: DrawingTool = DrawingTool.PEN,
    val selectedColor: StrokeColor = StrokeColor.Red,
    val strokeWidth: Float = 6f,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val availableColors: List<StrokeColor> = StrokeColor.DefaultPalette,
    val availableStrokeWidths: List<Float> = listOf(3f, 6f, 12f, 20f),
    val showClearDialog: Boolean = false
)

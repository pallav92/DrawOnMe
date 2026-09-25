package com.draw.onme.presentation.coloring

import com.draw.onme.domain.model.ClosedAreaFill
import com.draw.onme.domain.model.ColoringPage
import com.draw.onme.domain.model.ColoringTool
import com.draw.onme.domain.model.Point
import com.draw.onme.domain.model.Stroke
import com.draw.onme.domain.model.StrokeColor

/**
 * UI State for the Coloring Book Studio screen.
 * Implements Unidirectional Data Flow (UDF) with immutable data modeling.
 */
data class ColoringUiState(
    val page: ColoringPage? = null,
    val fills: Map<String, StrokeColor> = emptyMap(),
    val customFills: List<ClosedAreaFill> = emptyList(),
    val strokes: List<Stroke> = emptyList(),
    val currentStroke: Stroke? = null,
    val activeRegionIdForStroke: String? = null,
    val selectedTool: ColoringTool = ColoringTool.BUCKET,
    val selectedColor: StrokeColor = StrokeColor.Red,
    val strokeWidth: Float = 16f,
    val availableColors: List<StrokeColor> = QuickPalette,
    val availableStrokeWidths: List<Float> = listOf(8f, 16f, 26f, 40f),
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val showClearDialog: Boolean = false
) {
    /**
     * Percentage (0.0f..1.0f) of regions that have been colored.
     */
    val completionFraction: Float
        get() {
            val total = page?.regions?.size ?: 0
            if (total == 0) return 0f
            val colored = fills.size.coerceAtMost(total)
            return colored.toFloat() / total
        }

    val isAllRegionsFilled: Boolean
        get() = page != null && fills.size >= page.regions.size

    /**
     * Whether the current page has predefined vector regions supporting Paint Bucket fill.
     * True for curated templates; false for user-created blank canvases (which use crayons only).
     */
    val supportsBucketFill: Boolean
        get() = page != null && page.regions.isNotEmpty()

    companion object {
        /**
         * Curated lightweight child-friendly palette fitting cleanly on mobile screens.
         */
        val QuickPalette: List<StrokeColor> = listOf(
            StrokeColor(0xFFE53935), // Cherry Red
            StrokeColor(0xFFFB8C00), // Tangerine Orange
            StrokeColor(0xFFFDD835), // Sunny Yellow
            StrokeColor(0xFF43A047), // Grass Green
            StrokeColor(0xFF00ACC1), // Sky Cyan
            StrokeColor(0xFF1E88E5), // Ocean Blue
            StrokeColor(0xFF8E24AA), // Magic Purple
            StrokeColor(0xFFFFFFFF), // Chalk White
            StrokeColor(0xFF212121)  // Classic Charcoal
        )

        /**
         * Extended child-friendly palette for coloring books.
         */
        val ExtendedPalette: List<StrokeColor> = listOf(
            StrokeColor(0xFFE53935), // Cherry Red
            StrokeColor(0xFFFB8C00), // Tangerine Orange
            StrokeColor(0xFFFDD835), // Sunny Yellow
            StrokeColor(0xFF43A047), // Grass Green
            StrokeColor(0xFF00ACC1), // Sky Cyan
            StrokeColor(0xFF1E88E5), // Ocean Blue
            StrokeColor(0xFF8E24AA), // Magic Purple
            StrokeColor(0xFFEC407A), // Bubblegum Pink
            StrokeColor(0xFF8D6E63), // Teddy Brown
            StrokeColor(0xFFFFFFFF), // Chalk White
            StrokeColor(0xFF212121)  // Classic Charcoal
        )
    }
}

/**
 * Discrete user intentions/events dispatched to [ColoringViewModel].
 */
sealed interface ColoringAction {
    data class TapCanvas(val point: Point) : ColoringAction
    data class StartStroke(val point: Point) : ColoringAction
    data class AddPoint(val point: Point) : ColoringAction
    data object EndStroke : ColoringAction
    data object CancelStroke : ColoringAction
    data class SelectTool(val tool: ColoringTool) : ColoringAction
    data class SelectColor(val color: StrokeColor) : ColoringAction
    data class SetStrokeWidth(val width: Float) : ColoringAction
    data object Undo : ColoringAction
    data object Redo : ColoringAction
    data object RequestClear : ColoringAction
    data object ConfirmClear : ColoringAction
    data object DismissClear : ColoringAction
}

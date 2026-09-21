package com.pallav.drawonme.domain.model

import java.util.UUID

/**
 * Represents a 2D coordinate on the drawing canvas.
 * Pure Kotlin data class with zero Android framework dependencies.
 */
data class Point(
    val x: Float,
    val y: Float
)

/**
 * Tools available on the scribble board.
 */
enum class DrawingTool {
    PEN,
    ERASER,
    HAND
}

/**
 * Represents an ARGB color value for drawing strokes.
 * Stored as a Long (0xAARRGGBB) to remain completely framework-independent.
 */
data class StrokeColor(
    val argb: Long
) {
    companion object {
        val Black: StrokeColor = StrokeColor(0xFF000000)
        val Red: StrokeColor = StrokeColor(0xFFE53935)
        val Blue: StrokeColor = StrokeColor(0xFF1E88E5)
        val Green: StrokeColor = StrokeColor(0xFF43A047)
        val Yellow: StrokeColor = StrokeColor(0xFFFDD835)
        val Purple: StrokeColor = StrokeColor(0xFF8E24AA)
        val Orange: StrokeColor = StrokeColor(0xFFFB8C00)
        val White: StrokeColor = StrokeColor(0xFFFFFFFF)

        val DefaultPalette: List<StrokeColor> = listOf(
            Red,
            Blue,
            Green,
            Yellow,
            Orange,
            Purple,
            Black
        )
    }
}

/**
 * Represents a single stroke drawn on the scribble board.
 *
 * @property id Unique identifier for the stroke.
 * @property points Ordered sequence of touch points forming the stroke path.
 * @property color Color of the stroke.
 * @property strokeWidth Stroke width in points.
 * @property tool The tool used to draw this stroke.
 */
data class Stroke(
    val id: String = UUID.randomUUID().toString(),
    val points: List<Point>,
    val color: StrokeColor,
    val strokeWidth: Float,
    val tool: DrawingTool = DrawingTool.PEN
)

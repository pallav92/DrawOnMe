package com.draw.onme.presentation.scribble.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.draw.onme.domain.model.Point

/**
 * State holder managing pan and zoom transformations for the drawing board.
 *
 * Workspace specification:
 * - Total canvas workspace is 5x the window size of the device (spanning from -2W to +3W horizontally,
 *   and -2H to +3H vertically, with the initial screen centered at (0,0)).
 * - Zoom range: 0.2x (20%, whole 5x board in view) to 5.0x (500% high-precision zoom). Default 1.0x (100%).
 * - Pan translation is clamped to keep the viewport within the 5x workspace.
 */
@Stable
class CanvasTransformState(
    initialZoom: Float = 1.0f,
    initialPan: Offset = Offset.Zero
) {
    var zoom: Float by mutableFloatStateOf(initialZoom.coerceIn(MIN_ZOOM, MAX_ZOOM))
        private set

    var pan: Offset by mutableStateOf(initialPan)
        private set

    var viewportSize: IntSize by mutableStateOf(IntSize.Zero)

    /**
     * Converts a raw touch coordinate in screen space to world canvas space.
     */
    fun screenToWorld(screenOffset: Offset): Point {
        val currentZoom = zoom
        val currentPan = pan
        return Point(
            x = (screenOffset.x - currentPan.x) / currentZoom,
            y = (screenOffset.y - currentPan.y) / currentZoom
        )
    }

    /**
     * Converts a canvas world point back to screen space coordinate.
     */
    fun worldToScreen(worldPoint: Point): Offset {
        return Offset(
            x = worldPoint.x * zoom + pan.x,
            y = worldPoint.y * zoom + pan.y
        )
    }

    /**
     * Translates the canvas by [delta] in screen pixels, bounded by the 5x canvas workspace.
     */
    fun panBy(delta: Offset) {
        val newPan = pan + delta
        pan = clampPan(newPan, zoom)
    }

    /**
     * Scales the canvas by [zoomFactor] around the given [centroid] in screen pixels.
     * The world position under [centroid] remains stationary.
     */
    fun zoomBy(zoomFactor: Float, centroid: Offset) {
        val oldZoom = zoom
        val targetZoom = (oldZoom * zoomFactor).coerceIn(MIN_ZOOM, MAX_ZOOM)
        if (targetZoom == oldZoom) return

        // Invariant: centroid = (centroid - oldPan) / oldZoom * targetZoom + newPan
        // Therefore: newPan = centroid - (centroid - pan) * (targetZoom / oldZoom)
        val newPan = centroid - (centroid - pan) * (targetZoom / oldZoom)
        zoom = targetZoom
        pan = clampPan(newPan, targetZoom)
    }

    /**
     * Increments zoom by +25% centered at the viewport center.
     */
    fun zoomIn() {
        val center = Offset(
            x = viewportSize.width / 2f,
            y = viewportSize.height / 2f
        )
        zoomBy(ZOOM_STEP_FACTOR, center)
    }

    /**
     * Decrements zoom by -20% (reciprocal of 1.25) centered at the viewport center.
     */
    fun zoomOut() {
        val center = Offset(
            x = viewportSize.width / 2f,
            y = viewportSize.height / 2f
        )
        zoomBy(1f / ZOOM_STEP_FACTOR, center)
    }

    /**
     * Resets zoom back to 1.0x (100%) and pan back to Offset.Zero (re-centering initial viewport).
     */
    fun reset() {
        zoom = 1.0f
        pan = clampPan(Offset.Zero, 1.0f)
    }

    /**
     * Programmatically sets the pan offset and zoom level.
     * Used for adapting canvas views (e.g. centering and scaling stencils to fit orientation changes).
     */
    fun setTransform(newPan: Offset, newZoom: Float) {
        zoom = newZoom.coerceIn(MIN_ZOOM, MAX_ZOOM)
        pan = newPan
    }

    /**
     * Clamps the pan offset so that the visible viewport stays within the 5x device window workspace.
     * Workspace bounds: [-2W, -2H] to [+3W, +3H] in world coordinates.
     */
    fun clampPan(candidatePan: Offset, currentZoom: Float): Offset {
        val width = viewportSize.width.toFloat()
        val height = viewportSize.height.toFloat()
        if (width <= 0f || height <= 0f) {
            return candidatePan
        }

        // Left edge: -pan.x / zoom >= -2 * width  =>  pan.x <= 2 * width * zoom
        // Right edge: (width - pan.x) / zoom <= 3 * width  =>  pan.x >= width - 3 * width * zoom
        val maxPanX = 2f * width * currentZoom
        val minPanX = width - 3f * width * currentZoom

        val clampedX = if (minPanX > maxPanX) {
            (width - 5f * width * currentZoom) / 2f
        } else {
            candidatePan.x.coerceIn(minPanX, maxPanX)
        }

        // Top edge: -pan.y / zoom >= -2 * height  =>  pan.y <= 2 * height * zoom
        // Bottom edge: (height - pan.y) / zoom <= 3 * height  =>  pan.y >= height - 3 * height * zoom
        val maxPanY = 2f * height * currentZoom
        val minPanY = height - 3f * height * currentZoom

        val clampedY = if (minPanY > maxPanY) {
            (height - 5f * height * currentZoom) / 2f
        } else {
            candidatePan.y.coerceIn(minPanY, maxPanY)
        }

        return Offset(clampedX, clampedY)
    }

    companion object {
        const val MIN_ZOOM: Float = 0.20f // 20%: View entire 5x workspace
        const val MAX_ZOOM: Float = 5.00f // 500%: Deep precision zoom
        const val ZOOM_STEP_FACTOR: Float = 1.25f // 25% zoom step

        val Saver: androidx.compose.runtime.saveable.Saver<CanvasTransformState, List<Float>> =
            androidx.compose.runtime.saveable.Saver(
                save = { state -> listOf(state.zoom, state.pan.x, state.pan.y) },
                restore = { list ->
                    CanvasTransformState(
                        initialZoom = list.getOrElse(0) { 1.0f },
                        initialPan = Offset(list.getOrElse(1) { 0f }, list.getOrElse(2) { 0f })
                    )
                }
            )
    }
}

/**
 * Creates and remembers a [CanvasTransformState] instance across recompositions and configuration changes.
 */
@Composable
fun rememberCanvasTransformState(
    initialZoom: Float = 1.0f,
    initialPan: Offset = Offset.Zero
): CanvasTransformState {
    return androidx.compose.runtime.saveable.rememberSaveable(saver = CanvasTransformState.Saver) {
        CanvasTransformState(
            initialZoom = initialZoom,
            initialPan = initialPan
        )
    }
}

package com.pallav.drawonme.presentation.stencil.drawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.pallav.drawonme.domain.model.Point
import com.pallav.drawonme.presentation.scribble.model.CanvasTransformState
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests verifying that stencil tracing guidelines and user drawings remain
 * mathematically invariant and perfectly aligned across screen orientation changes.
 */
class StencilCoordinateAlignmentTest {

    @Test
    fun canonicalCanvasSize_is1000() {
        assertEquals(1000f, STENCIL_CANVAS_SIZE, 0.001f)
    }

    @Test
    fun orientationChange_drawingStaysAlignedOverStencil() {
        // Device dimensions: 1080 x 2400 (Portrait) and 2400 x 1080 (Landscape)
        val portraitWidth = 1080f
        val portraitHeight = 2400f
        val landscapeWidth = 2400f
        val landscapeHeight = 1080f

        // 1. Portrait setup: scale = min(W, H) * 0.92 = 1080 * 0.92 = 993.6
        val portraitScale = minOf(portraitWidth, portraitHeight) * 0.92f
        val portraitOffsetX = (portraitWidth - portraitScale) / 2f
        val portraitOffsetY = (portraitHeight - portraitScale) / 2f
        val portraitZoom = portraitScale / STENCIL_CANVAS_SIZE
        val portraitPan = Offset(portraitOffsetX, portraitOffsetY)

        val portraitTransform = CanvasTransformState(
            initialZoom = portraitZoom,
            initialPan = portraitPan
        ).apply {
            viewportSize = IntSize(portraitWidth.toInt(), portraitHeight.toInt())
        }

        // 2. Landscape setup: scale = H * 0.76 = 1080 * 0.76 = 820.8
        val landscapeScale = landscapeHeight * 0.76f
        val landscapeOffsetX = (landscapeWidth - landscapeScale) / 2f
        val landscapeOffsetY = (landscapeHeight - landscapeScale) / 2f
        val landscapeZoom = landscapeScale / STENCIL_CANVAS_SIZE
        val landscapePan = Offset(landscapeOffsetX, landscapeOffsetY)

        val landscapeTransform = CanvasTransformState(
            initialZoom = landscapeZoom,
            initialPan = landscapePan
        ).apply {
            viewportSize = IntSize(landscapeWidth.toInt(), landscapeHeight.toInt())
        }

        // Test multiple normalized points on the stencil character (e.g. eye, nose, hand, foot)
        val stencilNormalizedPoints = listOf(
            Point(0.5f, 0.5f),   // Center
            Point(0.25f, 0.30f), // Left eye
            Point(0.75f, 0.30f), // Right eye
            Point(0.50f, 0.65f), // Mouth
            Point(0.10f, 0.85f), // Bottom left
            Point(0.90f, 0.85f)  // Bottom right
        )

        for (normalizedPt in stencilNormalizedPoints) {
            // In canonical world space [0..1000]
            val worldPt = Point(
                x = normalizedPt.x * STENCIL_CANVAS_SIZE,
                y = normalizedPt.y * STENCIL_CANVAS_SIZE
            )

            // Child traces directly over the stencil point in Portrait
            val screenTouchPortrait = portraitTransform.worldToScreen(worldPt)
            val capturedStrokePoint = portraitTransform.screenToWorld(screenTouchPortrait)

            // Captured stroke point must equal the canonical world point
            assertEquals(worldPt.x, capturedStrokePoint.x, 0.01f)
            assertEquals(worldPt.y, capturedStrokePoint.y, 0.01f)

            // User now rotates to Landscape midway through!
            // The stencil outline landmark on the landscape screen:
            val stencilScreenPosLandscape = landscapeTransform.worldToScreen(worldPt)

            // The user's portrait-drawn stroke rendered on the landscape screen:
            val strokeScreenPosLandscape = landscapeTransform.worldToScreen(capturedStrokePoint)

            // The stroke must render EXACTLY over the stencil landmark in landscape!
            assertEquals(
                "Stroke X shifted away from stencil landmark after rotating to landscape",
                stencilScreenPosLandscape.x,
                strokeScreenPosLandscape.x,
                0.01f
            )
            assertEquals(
                "Stroke Y shifted away from stencil landmark after rotating to landscape",
                stencilScreenPosLandscape.y,
                strokeScreenPosLandscape.y,
                0.01f
            )

            // If user now draws another stroke in Landscape over the same landmark:
            val landscapeTouch = landscapeTransform.screenToWorld(stencilScreenPosLandscape)
            assertEquals(worldPt.x, landscapeTouch.x, 0.01f)
            assertEquals(worldPt.y, landscapeTouch.y, 0.01f)

            // Rotating back to Portrait:
            val strokeRenderedInPortrait = portraitTransform.worldToScreen(landscapeTouch)
            val stencilRenderedInPortrait = portraitTransform.worldToScreen(worldPt)

            assertEquals(
                "Landscape-drawn stroke shifted away from stencil landmark after rotating back to portrait",
                stencilRenderedInPortrait.x,
                strokeRenderedInPortrait.x,
                0.01f
            )
            assertEquals(
                "Landscape-drawn stroke shifted away from stencil landmark after rotating back to portrait",
                stencilRenderedInPortrait.y,
                strokeRenderedInPortrait.y,
                0.01f
            )
        }
    }

    @Test
    fun inPlaceTransformUpdate_newTouchesDrawExactlyAtFingerPosition() {
        // Single instance of CanvasTransformState across orientation change
        val transform = CanvasTransformState(
            initialZoom = 993.6f / STENCIL_CANVAS_SIZE,
            initialPan = Offset(43.2f, 703.2f)
        ).apply {
            viewportSize = IntSize(1080, 2400)
        }

        // 1. In portrait: User draws a point at screen position (540, 1200)
        val touchPortrait = Offset(540f, 1200f)
        val worldPointPortrait = transform.screenToWorld(touchPortrait)
        // Verify roundtrip
        val screenRenderPortrait = transform.worldToScreen(worldPointPortrait)
        assertEquals(touchPortrait.x, screenRenderPortrait.x, 0.01f)
        assertEquals(touchPortrait.y, screenRenderPortrait.y, 0.01f)

        // 2. Rotate to landscape: update the SAME instance in-place
        val landscapePan = Offset(789.6f, 129.6f)
        val landscapeZoom = 820.8f / STENCIL_CANVAS_SIZE
        transform.setTransform(landscapePan, landscapeZoom)
        transform.viewportSize = IntSize(2400, 1080)

        // Verify that existing stroke renders on landscape screen correctly
        val renderedLandscape = transform.worldToScreen(worldPointPortrait)
        assertEquals(1200f, renderedLandscape.x, 0.5f)
        assertEquals(540f, renderedLandscape.y, 0.5f)

        // 3. User draws NEXT stroke in landscape at screen position (1200, 540)
        val touchLandscape = Offset(1200f, 540f)
        val worldPointLandscape = transform.screenToWorld(touchLandscape)
        val screenRenderLandscape = transform.worldToScreen(worldPointLandscape)

        // The new stroke must render EXACTLY where the user touched in landscape!
        assertEquals(touchLandscape.x, screenRenderLandscape.x, 0.01f)
        assertEquals(touchLandscape.y, screenRenderLandscape.y, 0.01f)
        // And world points match
        assertEquals(worldPointPortrait.x, worldPointLandscape.x, 0.5f)
        assertEquals(worldPointPortrait.y, worldPointLandscape.y, 0.5f)
    }

    @Test
    fun twoFingerPanAndZoom_maintainsLockstepAlignment() {
        val transform = CanvasTransformState(
            initialZoom = 1.0f,
            initialPan = Offset(100f, 200f)
        ).apply {
            viewportSize = IntSize(1080, 2400)
        }

        val testStencilPoint = Point(300f, 400f)
        val initialScreenPos = transform.worldToScreen(testStencilPoint)

        // Two-finger zoom in by 2x centered at screen position (500, 500)
        transform.zoomBy(2.0f, Offset(500f, 500f))
        // Two-finger pan by (50, -80)
        transform.panBy(Offset(50f, -80f))

        // Screen position after pan/zoom
        val zoomedScreenPos = transform.worldToScreen(testStencilPoint)

        // If user touches right at this zoomed screen position:
        val worldFromTouch = transform.screenToWorld(zoomedScreenPos)

        assertEquals("World X diverged after pan/zoom", testStencilPoint.x, worldFromTouch.x, 0.01f)
        assertEquals("World Y diverged after pan/zoom", testStencilPoint.y, worldFromTouch.y, 0.01f)
    }
}

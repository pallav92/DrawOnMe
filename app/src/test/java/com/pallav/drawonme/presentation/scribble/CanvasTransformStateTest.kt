package com.pallav.drawonme.presentation.scribble

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.pallav.drawonme.domain.model.Point
import com.pallav.drawonme.presentation.scribble.model.CanvasTransformState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CanvasTransformStateTest {

    private lateinit var transformState: CanvasTransformState

    @Before
    fun setUp() {
        transformState = CanvasTransformState()
        transformState.viewportSize = IntSize(1000, 2000)
    }

    @Test
    fun initialState_hasDefaultZoomAndZeroPan() {
        assertEquals(1.0f, transformState.zoom, 0.001f)
        assertEquals(Offset.Zero, transformState.pan)
    }

    @Test
    fun screenToWorld_and_worldToScreen_areInverses() {
        val testScreenPoint = Offset(300f, 400f)
        val worldPoint = transformState.screenToWorld(testScreenPoint)
        assertEquals(Point(300f, 400f), worldPoint)

        val backToScreen = transformState.worldToScreen(worldPoint)
        assertEquals(testScreenPoint.x, backToScreen.x, 0.001f)
        assertEquals(testScreenPoint.y, backToScreen.y, 0.001f)

        // Now test with pan and zoom
        transformState.panBy(Offset(100f, 200f))
        transformState.zoomBy(1.5f, Offset(500f, 1000f))

        val world2 = transformState.screenToWorld(Offset(600f, 800f))
        val screen2 = transformState.worldToScreen(world2)
        assertEquals(600f, screen2.x, 0.01f)
        assertEquals(800f, screen2.y, 0.01f)
    }

    @Test
    fun panBy_clampsTo5xWorkspace() {
        // At zoom = 1.0 and viewport = (1000, 2000):
        // maxPanX = 2 * 1000 = 2000, minPanX = 1000 - 3 * 1000 = -2000
        // maxPanY = 2 * 2000 = 4000, minPanY = 2000 - 3 * 2000 = -4000

        transformState.panBy(Offset(5000f, 10000f))
        assertEquals(2000f, transformState.pan.x, 0.01f)
        assertEquals(4000f, transformState.pan.y, 0.01f)

        transformState.panBy(Offset(-10000f, -20000f))
        assertEquals(-2000f, transformState.pan.x, 0.01f)
        assertEquals(-4000f, transformState.pan.y, 0.01f)
    }

    @Test
    fun zoom_clampedBetweenMinAndMax() {
        // Test zooming in to max
        repeat(20) {
            transformState.zoomIn()
        }
        assertEquals(CanvasTransformState.MAX_ZOOM, transformState.zoom, 0.001f)

        // Test zooming out to min
        repeat(40) {
            transformState.zoomOut()
        }
        assertEquals(CanvasTransformState.MIN_ZOOM, transformState.zoom, 0.001f)
    }

    @Test
    fun zoomBy_keepsCentroidInvariantInWorldSpace() {
        val centroid = Offset(400f, 600f)
        val worldBefore = transformState.screenToWorld(centroid)

        transformState.zoomBy(1.8f, centroid)

        val worldAfter = transformState.screenToWorld(centroid)
        assertEquals(worldBefore.x, worldAfter.x, 0.01f)
        assertEquals(worldBefore.y, worldAfter.y, 0.01f)
    }

    @Test
    fun reset_restoresDefaultView() {
        transformState.panBy(Offset(500f, 800f))
        transformState.zoomIn()
        transformState.zoomIn()
        assertTrue(transformState.zoom > 1.0f)

        transformState.reset()
        assertEquals(1.0f, transformState.zoom, 0.001f)
        assertEquals(0f, transformState.pan.x, 0.01f)
        assertEquals(0f, transformState.pan.y, 0.01f)
    }
}

package com.draw.onme.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PointInPolygonTest {

    @Test
    fun testSquare_pointInside_returnsTrue() {
        val square = listOf(
            Point(0.2f, 0.2f),
            Point(0.8f, 0.2f),
            Point(0.8f, 0.8f),
            Point(0.2f, 0.8f)
        )
        val center = Point(0.5f, 0.5f)
        assertTrue(PointInPolygon.contains(center, square))
    }

    @Test
    fun testSquare_pointOutside_returnsFalse() {
        val square = listOf(
            Point(0.2f, 0.2f),
            Point(0.8f, 0.2f),
            Point(0.8f, 0.8f),
            Point(0.2f, 0.8f)
        )
        assertFalse(PointInPolygon.contains(Point(0.1f, 0.5f), square))
        assertFalse(PointInPolygon.contains(Point(0.9f, 0.5f), square))
        assertFalse(PointInPolygon.contains(Point(0.5f, 0.1f), square))
        assertFalse(PointInPolygon.contains(Point(0.5f, 0.9f), square))
    }

    @Test
    fun testConcavePolygon_pointInNotch_returnsFalse() {
        // U-shape or V-notch polygon
        val vShape = listOf(
            Point(0.0f, 0.0f),
            Point(0.5f, 0.5f),
            Point(1.0f, 0.0f),
            Point(1.0f, 1.0f),
            Point(0.0f, 1.0f)
        )
        // Point in the upper notch between the two arms
        assertFalse(PointInPolygon.contains(Point(0.5f, 0.2f), vShape))

        // Point inside the bottom base
        assertTrue(PointInPolygon.contains(Point(0.5f, 0.8f), vShape))
    }

    @Test
    fun testInsufficientPoints_returnsFalse() {
        assertFalse(PointInPolygon.contains(Point(0.5f, 0.5f), emptyList()))
        assertFalse(PointInPolygon.contains(Point(0.5f, 0.5f), listOf(Point(0f, 0f), Point(1f, 1f))))
    }
}

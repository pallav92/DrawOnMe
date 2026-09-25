package com.draw.onme.domain.model

/**
 * Geometric utility providing point-in-polygon hit testing using the Ray-Casting algorithm.
 * Pure Kotlin implementation with zero Android framework dependencies.
 */
object PointInPolygon {

    /**
     * Determines whether [point] lies inside the polygon defined by [polygon] vertices.
     * Works for both convex and concave simple polygons.
     * Coordinates are expected in consistent coordinate space (e.g. normalized [0.0..1.0]
     * or canonical [0.0..1000.0]).
     *
     * @param point The test point (x, y).
     * @param polygon Ordered list of vertices forming the polygon perimeter.
     * @return True if the point is strictly inside or on the boundary of the polygon.
     */
    fun contains(point: Point, polygon: List<Point>): Boolean {
        if (polygon.size < 3) return false

        var inside = false
        var j = polygon.size - 1

        for (i in polygon.indices) {
            val pi = polygon[i]
            val pj = polygon[j]

            // Check if ray crosses the edge (pi, pj)
            val intersects = ((pi.y > point.y) != (pj.y > point.y)) &&
                (point.x < (pj.x - pi.x) * (point.y - pi.y) / (pj.y - pi.y) + pi.x)

            if (intersects) {
                inside = !inside
            }
            j = i
        }

        return inside
    }
}

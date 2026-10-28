package com.draw.onme.domain.model

import com.draw.onme.data.repository.InMemoryColoringBookRepository
import kotlin.math.hypot
import org.junit.Assert.assertTrue
import org.junit.Test

class RegionLabelPlacementTest {

    private fun distance(a: Point, b: Point): Float =
        hypot((a.x - b.x).toDouble(), (a.y - b.y).toDouble()).toFloat()

    private fun circlePoints(cx: Float, cy: Float, r: Float, steps: Int = 16): List<Point> =
        (0 until steps).map { i ->
            val angle = 2.0 * Math.PI * i / steps
            Point(
                x = (cx + r * kotlin.math.cos(angle)).toFloat(),
                y = (cy + r * kotlin.math.sin(angle)).toFloat()
            )
        }

    @Test
    fun testConcentricCircles_labelsSeparate() {
        val outer = ColoringRegion("outer", "Outer", circlePoints(0.5f, 0.5f, 0.3f))
        val inner = ColoringRegion("inner", "Inner", circlePoints(0.5f, 0.5f, 0.1f))

        val points = RegionLabelPlacement.computeLabelPoints(listOf(outer, inner))

        assertTrue(
            "Concentric labels must not print on top of each other",
            distance(points.getValue("outer"), points.getValue("inner")) > 0.03f
        )
    }

    @Test
    fun testThreeNestedRings_allThreeLabelsSeparate() {
        val outer = ColoringRegion("outer", "Outer", circlePoints(0.5f, 0.5f, 0.3f))
        val mid = ColoringRegion("mid", "Mid", circlePoints(0.5f, 0.5f, 0.2f))
        val inner = ColoringRegion("inner", "Inner", circlePoints(0.5f, 0.5f, 0.1f))

        val points = RegionLabelPlacement.computeLabelPoints(listOf(outer, mid, inner))
        val pairs = listOf("outer" to "mid", "mid" to "inner", "outer" to "inner")
        for ((a, b) in pairs) {
            assertTrue(
                "Labels for $a and $b must not collide",
                distance(points.getValue(a), points.getValue(b)) > 0.02f
            )
        }
    }

    @Test
    fun testIsolatedRegion_keepsPlainCentroid() {
        val solo = ColoringRegion("solo", "Solo", circlePoints(0.3f, 0.3f, 0.1f))
        val points = RegionLabelPlacement.computeLabelPoints(listOf(solo))
        assertTrue(distance(points.getValue("solo"), Point(0.3f, 0.3f)) < 0.001f)
    }

    @Test
    fun testKittenConcentricBodyRings_labelsDoNotCollide() {
        val kitten = InMemoryColoringBookRepository().getPageById("playful_kitten")!!
        val points = RegionLabelPlacement.computeLabelPoints(kitten.regions, kitten.outlines)

        assertTrue(distance(points.getValue("body"), points.getValue("body_mid")) > 0.02f)
        assertTrue(distance(points.getValue("body_mid"), points.getValue("tummy")) > 0.02f)
        assertTrue(distance(points.getValue("body"), points.getValue("tummy")) > 0.02f)
    }

    @Test
    fun testPuppyFaceCluster_everyPairSeparates() {
        // head, face_patch, snout, and nose all sit near the same spot on the puppy's face -
        // the exact cluster that was printing overlapping/garbled digits.
        val puppy = InMemoryColoringBookRepository().getPageById("happy_puppy")!!
        val points = RegionLabelPlacement.computeLabelPoints(puppy.regions, puppy.outlines)
        val cluster = listOf("head", "face_patch", "snout", "nose")

        for (i in cluster.indices) {
            for (j in i + 1 until cluster.size) {
                val a = points.getValue(cluster[i])
                val b = points.getValue(cluster[j])
                assertTrue(
                    "Labels for ${cluster[i]} and ${cluster[j]} must not collide",
                    distance(a, b) > 0.015f
                )
            }
        }
    }

    @Test
    fun testRaceCarConcentricWheelAndRim_labelsDoNotCollide() {
        val raceCar = InMemoryColoringBookRepository().getPageById("vehicle_race_car")!!
        val points = RegionLabelPlacement.computeLabelPoints(raceCar.regions, raceCar.outlines)

        assertTrue(distance(points.getValue("wheel_front"), points.getValue("rim_front")) > 0.02f)
        assertTrue(distance(points.getValue("wheel_rear"), points.getValue("rim_rear")) > 0.02f)
    }

    @Test
    fun testEveryPage_noTwoLabelsAreExactlyCoincident() {
        for (page in InMemoryColoringBookRepository().getColoringPages()) {
            if (page.regions.size < 2) continue
            val points = RegionLabelPlacement.computeLabelPoints(page.regions, page.outlines)
            val values = points.values.toList()
            for (i in values.indices) {
                for (j in i + 1 until values.size) {
                    assertTrue(
                        "Page ${page.id}: two region labels must never land on the exact same point",
                        distance(values[i], values[j]) > 0.0001f
                    )
                }
            }
        }
    }

    @Test
    fun testLabelAvoidsDecorativeLineThroughRegionCenter() {
        // A line drawn straight through the middle of the region - e.g. a whisker or a mouth
        // curve - used to sit right under a centroid-based label. The label should now dodge it.
        val square = listOf(Point(0.3f, 0.3f), Point(0.7f, 0.3f), Point(0.7f, 0.7f), Point(0.3f, 0.7f))
        val region = ColoringRegion("face", "Face", square)
        val lineThroughCenter = ColoringOutline(
            points = listOf(Point(0.3f, 0.5f), Point(0.7f, 0.5f)),
            isClosed = false
        )

        val centroidDistanceToLine = 0f // the plain centroid (0.5, 0.5) sits exactly on this line
        val points = RegionLabelPlacement.computeLabelPoints(listOf(region), listOf(lineThroughCenter))
        val label = points.getValue("face")

        val distanceToLine = RegionLabelPlacement.distanceToNearestLine(label, listOf(lineThroughCenter))
        assertTrue(
            "Label should move well clear of the line through the region's centroid",
            distanceToLine > centroidDistanceToLine + 0.05f
        )
    }

    @Test
    fun testLabelAvoidsOwnRegionBoundaryLine() {
        // Even with no decorative detail lines, a region's own outline (drawn as an outline too)
        // should never have a label printed directly on top of it.
        val boundary = circlePoints(0.5f, 0.5f, 0.2f)
        val region = ColoringRegion("circle", "Circle", boundary)
        val ownOutline = ColoringOutline(boundary, isClosed = true)

        val points = RegionLabelPlacement.computeLabelPoints(listOf(region), listOf(ownOutline))
        val label = points.getValue("circle")
        val distanceToBoundary = RegionLabelPlacement.distanceToNearestLine(label, listOf(ownOutline))

        assertTrue("Label must clear its own region's boundary stroke", distanceToBoundary > 0.015f)
    }

    @Test
    fun testOverlappingSiblingRegions_labelNeverPrintsOnSmallerRegionsTerritory() {
        // The exact bug from the Yellow School Bus: "hood" is a smaller rectangle that overlaps
        // the right edge of the larger "bus_body" rectangle. Since tapping resolves to the
        // smallest region containing the point, bus_body's label must never land inside hood's
        // boundary - printing it there would read as "the same box" and tapping it would
        // actually fill the wrong region.
        val busBody = ColoringRegion("bus_body", "Bus Body", rectPoints(0.10f, 0.36f, 0.88f, 0.72f))
        val hood = ColoringRegion("hood", "Hood", rectPoints(0.74f, 0.52f, 0.90f, 0.72f))
        val outlines = listOf(
            ColoringOutline(busBody.boundaryPoints, isClosed = true),
            ColoringOutline(hood.boundaryPoints, isClosed = true)
        )

        val points = RegionLabelPlacement.computeLabelPoints(listOf(busBody, hood), outlines)
        val bodyLabel = points.getValue("bus_body")
        val hoodLabel = points.getValue("hood")

        assertTrue(
            "bus_body's label must not land inside hood's overlapping boundary",
            !PointInPolygon.contains(bodyLabel, hood.boundaryPoints)
        )
        assertTrue("hood's label must land inside its own boundary", PointInPolygon.contains(hoodLabel, hood.boundaryPoints))
    }

    @Test
    fun testSchoolBusHoodAndBody_realPageLabelsDoNotCollideOrOverlapTerritory() {
        val bus = InMemoryColoringBookRepository().getPageById("vehicle_school_bus")!!
        val points = RegionLabelPlacement.computeLabelPoints(bus.regions, bus.outlines)
        val bodyLabel = points.getValue("bus_body")
        val hood = bus.regions.first { it.id == "hood" }

        assertTrue(
            "bus_body's label must not land inside hood's overlapping territory",
            !PointInPolygon.contains(bodyLabel, hood.boundaryPoints)
        )
        assertTrue(distance(bodyLabel, points.getValue("hood")) > 0.02f)
    }

    private fun rectPoints(x1: Float, y1: Float, x2: Float, y2: Float): List<Point> =
        listOf(Point(x1, y1), Point(x2, y1), Point(x2, y2), Point(x1, y2))

    @Test
    fun testFormulaRacerCockpitCluster_labelsClearLinesAndEachOther() {
        // The cockpit/engine/number-disk cluster from the user's screenshot, where digits were
        // printing on top of the hood and wheel-well lines.
        val raceCar = InMemoryColoringBookRepository().getPageById("vehicle_race_car")!!
        val points = RegionLabelPlacement.computeLabelPoints(raceCar.regions, raceCar.outlines)

        for (region in raceCar.regions) {
            val label = points.getValue(region.id)
            val distanceToLine = RegionLabelPlacement.distanceToNearestLine(label, raceCar.outlines)
            assertTrue(
                "Label for ${region.id} on Formula Racer should not print on top of a drawn line",
                distanceToLine > 0f
            )
        }
    }
}

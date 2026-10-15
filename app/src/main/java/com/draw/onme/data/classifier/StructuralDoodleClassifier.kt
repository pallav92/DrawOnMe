package com.draw.onme.data.classifier

import com.draw.onme.domain.classifier.DoodleClassifier
import com.draw.onme.domain.model.DoodleCategory
import com.draw.onme.domain.model.DoodlePrediction
import com.draw.onme.domain.model.Point
import com.draw.onme.domain.model.Stroke
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Pure Kotlin, zero-dependency structural and morphological feature analysis doodle classifier.
 *
 * Evaluates dense point profiles, vertical slice width distributions, and loop topologies.
 * Serves as a 0MB instant fallback while the Google ML Kit AutoDraw model initializes.
 */
class StructuralDoodleClassifier(
    val categories: List<DoodleCategory> = DoodleCategoryCatalog.CATEGORIES
) : DoodleClassifier {

    override suspend fun classify(strokes: List<Stroke>): List<DoodlePrediction> = withContext(Dispatchers.Default) {
        if (strokes.isEmpty()) {
            return@withContext emptyList()
        }

        // Interpolate points between vertices for dense profile analysis
        val densePoints = mutableListOf<Point>()
        for (stroke in strokes) {
            val pts = stroke.points
            if (pts.isEmpty()) continue
            if (pts.size == 1) {
                densePoints.add(pts[0])
                continue
            }
            for (i in 0 until pts.size - 1) {
                val p1 = pts[i]
                val p2 = pts[i + 1]
                val dist = Math.hypot((p2.x - p1.x).toDouble(), (p2.y - p1.y).toDouble()).toFloat()
                val steps = max(1, (dist / 4f).toInt())
                for (s in 0 until steps) {
                    val t = s.toFloat() / steps
                    densePoints.add(Point(p1.x + (p2.x - p1.x) * t, p1.y + (p2.y - p1.y) * t))
                }
            }
            densePoints.add(pts.last())
        }

        if (densePoints.size < 4) {
            return@withContext emptyList()
        }

        var minX = Float.MAX_VALUE
        var maxX = Float.MIN_VALUE
        var minY = Float.MAX_VALUE
        var maxY = Float.MIN_VALUE

        for (p in densePoints) {
            if (p.x < minX) minX = p.x
            if (p.x > maxX) maxX = p.x
            if (p.y < minY) minY = p.y
            if (p.y > maxY) maxY = p.y
        }

        val origWidth = max(1f, maxX - minX)
        val origHeight = max(1f, maxY - minY)
        val aspectRatio = origWidth / origHeight

        // Normalize points to [0..100] coordinate grid
        val normalizedPoints = densePoints.map { p ->
            Point(
                x = ((p.x - minX) / origWidth) * 100f,
                y = ((p.y - minY) / origHeight) * 100f
            )
        }

        // Compute 10 vertical slice width and center-of-mass profiles
        val sliceWidths = FloatArray(10)
        val sliceCenters = FloatArray(10) { 50f }
        val sliceCounts = IntArray(10)

        for (i in 0 until 10) {
            val yStart = i * 10f
            val yEnd = (i + 1) * 10f
            val ptsInSlice = normalizedPoints.filter { it.y in yStart..yEnd }
            sliceCounts[i] = ptsInSlice.size
            if (ptsInSlice.isNotEmpty()) {
                val sMin = ptsInSlice.minOf { it.x }
                val sMax = ptsInSlice.maxOf { it.x }
                sliceWidths[i] = sMax - sMin
                sliceCenters[i] = (sMin + sMax) / 2f
            }
        }

        val topAvgWidth = (sliceWidths[1] + sliceWidths[2] + sliceWidths[3] + sliceWidths[4]) / 4f
        val botAvgWidth = (sliceWidths[7] + sliceWidths[8] + sliceWidths[9]) / 3f
        val midAvgWidth = (sliceWidths[3] + sliceWidths[4] + sliceWidths[5] + sliceWidths[6]) / 4f

        val predictions = mutableListOf<DoodlePrediction>()

        // 1. TREE: Wide foliage in upper/middle half + narrow trunk in bottom third
        val isTrunkCylindrical = abs(sliceWidths[7] - sliceWidths[9]) < 20f
        val hasFoliageDrop = sliceWidths[5] > sliceWidths[7] * 1.25f || strokes.size >= 2
        if (topAvgWidth > max(1f, botAvgWidth) * 1.35f && botAvgWidth <= 45f && isTrunkCylindrical && hasFoliageDrop && abs(sliceCenters[8] - 50f) < 35f) {
            val conf = min(0.94f, 0.82f + (topAvgWidth / (botAvgWidth + topAvgWidth + 1f)) * 0.15f)
            predictions.add(DoodlePrediction(DoodleCategoryCatalog.findById("tree"), conf))
        }

        // 2. CLOUD: Horizontal profile, wide fluffy lobes across middle, flat/soft base
        if (aspectRatio in 1.15f..3.6f && midAvgWidth > 45f && (topAvgWidth >= botAvgWidth * 0.6f || strokes.size <= 3)) {
            val conf = min(0.93f, 0.82f + (origWidth / (origWidth + origHeight)) * 0.12f)
            predictions.add(DoodlePrediction(DoodleCategoryCatalog.findById("cloud"), conf))
        }

        // 3. SUN: Symmetrical circular aspect ratio, radial rays / spokes or round circle
        if (aspectRatio in 0.78f..1.28f && midAvgWidth > 45f && abs(topAvgWidth - botAvgWidth) < 22f) {
            val symmetry = 1f - (abs(sliceCenters[4] - 50f) / 50f)
            if (symmetry > 0.6f) {
                val conf = min(0.92f, 0.82f + symmetry * 0.12f)
                predictions.add(DoodlePrediction(DoodleCategoryCatalog.findById("sun"), conf))
            }
        }

        // 4. HOUSE: Triangle/pointed roof at top (expanding width) + boxy walls at bottom
        val isRoofExpanding = sliceWidths[0] < sliceWidths[2] && sliceWidths[1] <= sliceWidths[3]
        val isWallsBoxy = abs(sliceWidths[6] - sliceWidths[8]) < 24f && sliceWidths[7] > 45f
        if (aspectRatio in 0.7f..1.4f && isRoofExpanding && isWallsBoxy) {
            val conf = min(0.91f, 0.81f + (sliceWidths[7] / 100f) * 0.12f)
            predictions.add(DoodlePrediction(DoodleCategoryCatalog.findById("house"), conf))
        }

        // 5. CAR: Elongated horizontally, upper cabin narrower than wide chassis
        if (aspectRatio in 1.35f..3.4f && botAvgWidth > topAvgWidth * 1.25f && botAvgWidth > 60f) {
            val conf = min(0.90f, 0.79f + (botAvgWidth / 100f) * 0.12f)
            predictions.add(DoodlePrediction(DoodleCategoryCatalog.findById("car"), conf))
        }

        // 6. APPLE: Round body (slices 2..8) + single narrow stem at top (slice 0..1), round base
        val hasStem = sliceWidths[0] in 2f..30f && sliceCounts[0] > 0 && sliceWidths[2] > sliceWidths[0] * 1.4f
        val isBottomRound = sliceWidths[9] > 20f || sliceWidths[8] > 30f
        val isBodyRound = midAvgWidth > 45f && abs(sliceWidths[4] - sliceWidths[6]) < 25f
        if (aspectRatio in 0.70f..1.35f && hasStem && isBodyRound && isBottomRound) {
            predictions.add(DoodlePrediction(DoodleCategoryCatalog.findById("apple"), 0.90f))
        }

        // 7. HEART: Cleft/dip at top (split mass) + sharp point tapering at bottom
        val isBottomPointed = sliceWidths[9] < 25f && sliceWidths[8] < sliceWidths[5] * 0.75f
        val isTopWide = sliceWidths[1] > 40f
        val isHeartTapering = sliceWidths[5] >= sliceWidths[7] && sliceWidths[7] >= sliceWidths[9]
        if (aspectRatio in 0.75f..1.40f && isBottomPointed && isTopWide && isHeartTapering) {
            predictions.add(DoodlePrediction(DoodleCategoryCatalog.findById("heart"), 0.95f))
        }

        // 8. FLOWER: Multi-stroke petals surrounding center or round head with long stem
        if (strokes.size in 3..14 && botAvgWidth in 4f..35f && topAvgWidth > 45f) {
            predictions.add(DoodlePrediction(DoodleCategoryCatalog.findById("flower"), 0.88f))
        }

        // 9. STAR: Multiple crossing lines or pointed star polygon
        if (strokes.size in 3..10 && aspectRatio in 0.8f..1.25f) {
            predictions.add(DoodlePrediction(DoodleCategoryCatalog.findById("star"), 0.87f))
        }

        // 10. SMILEY FACE: Circular outline with internal features (eyes & mouth)
        if (aspectRatio in 0.85f..1.18f && strokes.size in 3..6 && midAvgWidth > 65f) {
            predictions.add(DoodlePrediction(DoodleCategoryCatalog.findById("smiley_face"), 0.88f))
        }

        predictions.sortedByDescending { it.confidence }
    }
}

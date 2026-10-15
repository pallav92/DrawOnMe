package com.draw.onme.data.classifier

import com.draw.onme.domain.model.Point
import com.draw.onme.domain.model.Stroke
import com.draw.onme.domain.model.StrokeColor
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StructuralDoodleClassifierTest {

    private val classifier = StructuralDoodleClassifier()

    @Test
    fun `empty strokes returns empty predictions`() = runTest {
        val result = classifier.classify(emptyList())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `structural engine identifies tree doodle`() = runTest {
        val canopyPoints = listOf(
            Point(150f, 50f), Point(100f, 80f), Point(80f, 130f),
            Point(100f, 180f), Point(200f, 180f), Point(220f, 130f),
            Point(200f, 80f), Point(150f, 50f)
        )
        val canopy = Stroke(points = canopyPoints, color = StrokeColor.Green, strokeWidth = 8f)
        val trunk = Stroke(
            points = listOf(Point(150f, 180f), Point(150f, 300f)),
            color = StrokeColor.Black,
            strokeWidth = 10f
        )
        val predictions = classifier.classify(listOf(canopy, trunk))
        assertTrue("Predictions should not be empty", predictions.isNotEmpty())
        assertEquals("tree", predictions.first().category.id)
        assertTrue(predictions.first().confidence >= 0.80f)
    }

    @Test
    fun `structural engine identifies cloud doodle`() = runTest {
        val cloudPoints = listOf(
            Point(100f, 180f), Point(150f, 120f), Point(220f, 100f),
            Point(300f, 100f), Point(380f, 130f), Point(440f, 180f),
            Point(400f, 240f), Point(140f, 240f), Point(100f, 180f)
        )
        val cloudStroke = Stroke(points = cloudPoints, color = StrokeColor.Blue, strokeWidth = 8f)
        val predictions = classifier.classify(listOf(cloudStroke))
        assertTrue("Predictions should not be empty", predictions.isNotEmpty())
        assertEquals("cloud", predictions.first().category.id)
        assertTrue(predictions.first().confidence >= 0.80f)
    }

    @Test
    fun `structural engine identifies heart doodle`() = runTest {
        val heartPoints = listOf(
            Point(150f, 70f), Point(120f, 40f), Point(80f, 50f), Point(60f, 90f),
            Point(90f, 150f), Point(150f, 220f),
            Point(210f, 150f), Point(240f, 90f), Point(220f, 50f), Point(180f, 40f), Point(150f, 70f)
        )
        val heart = Stroke(points = heartPoints, color = StrokeColor.Red, strokeWidth = 6f)
        val predictions = classifier.classify(listOf(heart))
        assertTrue(predictions.isNotEmpty())
        assertEquals("heart", predictions.first().category.id)
        assertTrue(predictions.first().confidence >= 0.80f)
    }

    @Test
    fun `DoodleLogger logs non-confident and confident predictions safely`() {
        val nonConfident = listOf(
            com.draw.onme.domain.model.DoodlePrediction(
                category = com.draw.onme.domain.model.DoodleCategory("apple", "Apple", "🍎"),
                confidence = 0.54f
            ),
            com.draw.onme.domain.model.DoodlePrediction(
                category = com.draw.onme.domain.model.DoodleCategory("circle", "Circle", "⭕"),
                confidence = 0.22f
            )
        )
        DoodleLogger.logPredictions(nonConfident)

        val confident = listOf(
            com.draw.onme.domain.model.DoodlePrediction(
                category = com.draw.onme.domain.model.DoodleCategory("tree", "Tree", "🌳"),
                confidence = 0.92f
            )
        )
        DoodleLogger.logPredictions(confident)

        DoodleLogger.logPredictions(emptyList())
    }
}

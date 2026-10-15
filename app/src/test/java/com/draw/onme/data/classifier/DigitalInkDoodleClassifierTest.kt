package com.draw.onme.data.classifier

import com.draw.onme.domain.classifier.DoodleClassifier
import com.draw.onme.domain.model.DoodleCategory
import com.draw.onme.domain.model.DoodlePrediction
import com.draw.onme.domain.model.Point
import com.draw.onme.domain.model.Stroke
import com.draw.onme.domain.model.StrokeColor
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DigitalInkDoodleClassifierTest {

    @Test
    fun `empty strokes returns empty predictions`() = runTest {
        val classifier = DigitalInkDoodleClassifier()
        val result = classifier.classify(emptyList())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `delegates to fallback classifier when model is not yet loaded`() = runTest {
        val expectedPrediction = DoodlePrediction(
            category = DoodleCategory("apple", "Apple", "🍎"),
            confidence = 0.91f
        )
        val fakeFallback = object : DoodleClassifier {
            override suspend fun classify(strokes: List<Stroke>): List<DoodlePrediction> {
                return listOf(expectedPrediction)
            }
        }

        val classifier = DigitalInkDoodleClassifier(fallbackClassifier = fakeFallback)
        val stroke = Stroke(
            points = listOf(Point(10f, 10f), Point(50f, 50f)),
            color = StrokeColor.Red,
            strokeWidth = 6f
        )
        val result = classifier.classify(listOf(stroke))

        assertEquals(1, result.size)
        assertEquals("apple", result.first().category.id)
        assertEquals(0.91f, result.first().confidence)
    }
}

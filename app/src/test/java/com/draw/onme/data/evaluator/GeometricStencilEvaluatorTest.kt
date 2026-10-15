package com.draw.onme.data.evaluator

import com.draw.onme.domain.model.Point
import com.draw.onme.domain.model.Stencil
import com.draw.onme.domain.model.StencilPartStatus
import com.draw.onme.domain.model.StencilPath
import com.draw.onme.domain.model.Stroke
import com.draw.onme.domain.model.StrokeColor
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GeometricStencilEvaluatorTest {

    private val evaluator = GeometricStencilEvaluator(hitTolerancePx = 50f)

    private val sampleHouseStencil = Stencil(
        id = "cozy-house",
        title = "Cozy House",
        category = "Buildings",
        description = "A cute house",
        iconEmoji = "🏠",
        difficulty = "Easy",
        paths = listOf(
            StencilPath(
                points = listOf(Point(0.2f, 0.4f), Point(0.5f, 0.1f), Point(0.8f, 0.4f)),
                label = "Roof"
            ),
            StencilPath(
                points = listOf(Point(0.2f, 0.4f), Point(0.2f, 0.9f), Point(0.8f, 0.9f), Point(0.8f, 0.4f)),
                label = "Walls"
            ),
            StencilPath(
                points = listOf(Point(0.45f, 0.65f), Point(0.45f, 0.9f), Point(0.55f, 0.9f), Point(0.55f, 0.65f)),
                label = "Door"
            )
        )
    )

    @Test
    fun `empty user strokes returns score zero with invitation message`() = runTest {
        val result = evaluator.evaluate(sampleHouseStencil, emptyList(), canvasSize = 1000f)

        assertEquals(0f, result.overallScore, 0.001f)
        assertFalse(result.isGood)
        assertTrue(result.pupComment.contains("Start drawing along the lines"))
    }

    @Test
    fun `accurate trace covering all parts yields high score and praise`() = runTest {
        // User strokes precisely matching all stencil paths scaled to 1000f
        val userStrokes = sampleHouseStencil.paths.map { path ->
            Stroke(
                points = path.points.map { pt -> Point(pt.x * 1000f, pt.y * 1000f) },
                color = StrokeColor.Black,
                strokeWidth = 6f
            )
        }

        val result = evaluator.evaluate(sampleHouseStencil, userStrokes, canvasSize = 1000f)

        assertTrue(result.overallScore >= 0.80f)
        assertTrue(result.isGood)
        assertTrue(result.pupComment.contains("Woof-tastic!"))
        assertTrue(result.partFeedbacks.all { it.status == StencilPartStatus.EXCELLENT })
    }

    @Test
    fun `partial trace missing door prompts Pup to give specific tip`() = runTest {
        // Trace roof and walls, but omit door
        val userStrokes = listOf(
            Stroke(
                points = sampleHouseStencil.paths[0].points.map { Point(it.x * 1000f, it.y * 1000f) },
                color = StrokeColor.Black,
                strokeWidth = 6f
            ),
            Stroke(
                points = sampleHouseStencil.paths[1].points.map { Point(it.x * 1000f, it.y * 1000f) },
                color = StrokeColor.Black,
                strokeWidth = 6f
            )
        )

        val result = evaluator.evaluate(sampleHouseStencil, userStrokes, canvasSize = 1000f)

        assertFalse(result.isGood)
        assertNotNull(result.suggestedImprovementPart)
        assertEquals("Door", result.suggestedImprovementPart)
        assertTrue(result.pupComment.contains("Door"))
    }
}

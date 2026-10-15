package com.draw.onme.data.evaluator

import com.draw.onme.domain.evaluator.StencilEvaluator
import com.draw.onme.domain.model.Point
import com.draw.onme.domain.model.Stencil
import com.draw.onme.domain.model.StencilEvaluation
import com.draw.onme.domain.model.StencilPartFeedback
import com.draw.onme.domain.model.StencilPartStatus
import com.draw.onme.domain.model.Stroke
import kotlin.math.sqrt

/**
 * High-performance geometric evaluator that compares user drawing strokes
 * against normalized vector stencil paths.
 *
 * Runs completely on-device in < 5ms with zero external dependencies.
 */
class GeometricStencilEvaluator(
    private val hitTolerancePx: Float = 55f
) : StencilEvaluator {

    override suspend fun evaluate(
        stencil: Stencil,
        strokes: List<Stroke>,
        canvasSize: Float
    ): StencilEvaluation {
        val userPoints = strokes.flatMap { it.points }

        if (userPoints.isEmpty() || stencil.paths.isEmpty()) {
            return StencilEvaluation(
                stencilId = stencil.id,
                overallScore = 0f,
                isGood = false,
                partFeedbacks = emptyList(),
                pupComment = "Woof! Start drawing along the lines and I'll cheer you on! 🐾",
                suggestedImprovementPart = stencil.paths.firstOrNull()?.label
            )
        }

        val partFeedbacks = mutableListOf<StencilPartFeedback>()
        var totalWeightedCoverage = 0f
        var totalPointsWeight = 0

        for (stencilPath in stencil.paths) {
            if (stencilPath.points.isEmpty()) continue

            val scaledStencilPoints = stencilPath.points.map { pt ->
                Point(x = pt.x * canvasSize, y = pt.y * canvasSize)
            }

            var coveredPoints = 0
            for (targetPt in scaledStencilPoints) {
                val isCovered = userPoints.any { userPt ->
                    distance(userPt, targetPt) <= hitTolerancePx
                }
                if (isCovered) {
                    coveredPoints++
                }
            }

            val coverageRatio = (coveredPoints.toFloat() / scaledStencilPoints.size).coerceIn(0f, 1f)
            val status = when {
                coverageRatio >= 0.82f -> StencilPartStatus.EXCELLENT
                coverageRatio >= 0.55f -> StencilPartStatus.GOOD
                coverageRatio >= 0.20f -> StencilPartStatus.NEEDS_WORK
                else -> StencilPartStatus.MISSING
            }

            val label = stencilPath.label ?: "Outline"
            partFeedbacks.add(
                StencilPartFeedback(
                    partLabel = label,
                    coverageScore = coverageRatio,
                    status = status
                )
            )

            val weight = scaledStencilPoints.size
            totalWeightedCoverage += coverageRatio * weight
            totalPointsWeight += weight
        }

        val overallScore = if (totalPointsWeight > 0) {
            (totalWeightedCoverage / totalPointsWeight).coerceIn(0f, 1f)
        } else {
            0f
        }

        val isGood = overallScore >= StencilEvaluation.GOOD_SCORE_THRESHOLD

        // Identify parts for constructive coaching
        val missingPart = partFeedbacks.firstOrNull { it.status == StencilPartStatus.MISSING }
        val needsWorkPart = partFeedbacks.firstOrNull { it.status == StencilPartStatus.NEEDS_WORK }
        val excellentPart = partFeedbacks.firstOrNull { it.status == StencilPartStatus.EXCELLENT }

        val suggestedPart = missingPart?.partLabel ?: needsWorkPart?.partLabel

        val pupComment = buildPupComment(
            stencilTitle = stencil.title,
            isGood = isGood,
            overallScore = overallScore,
            missingPart = missingPart?.partLabel,
            needsWorkPart = needsWorkPart?.partLabel,
            excellentPart = excellentPart?.partLabel
        )

        return StencilEvaluation(
            stencilId = stencil.id,
            overallScore = overallScore,
            isGood = isGood,
            partFeedbacks = partFeedbacks,
            pupComment = pupComment,
            suggestedImprovementPart = suggestedPart
        )
    }

    private fun buildPupComment(
        stencilTitle: String,
        isGood: Boolean,
        overallScore: Float,
        missingPart: String?,
        needsWorkPart: String?,
        excellentPart: String?
    ): String {
        return if (isGood) {
            val scorePct = (overallScore * 100).toInt()
            if (excellentPart != null) {
                "Woof-tastic! 🌟 ($scorePct%) Your $stencilTitle looks pawsome! The $excellentPart is super neat!"
            } else {
                "Woof-tastic! 🌟 ($scorePct%) Your $stencilTitle looks fantastic! Spot on lines!"
            }
        } else {
            when {
                missingPart != null -> {
                    "Looking good! 🐾 Pup's Tip: Don't forget to trace the $missingPart!"
                }
                needsWorkPart != null -> {
                    "Great effort! 🐾 Pup's Tip: Try tracing the $needsWorkPart a bit closer to the lines!"
                }
                else -> {
                    "Keep going! 🐾 Follow the dashed lines to finish your $stencilTitle!"
                }
            }
        }
    }

    private fun distance(p1: Point, p2: Point): Float {
        val dx = p1.x - p2.x
        val dy = p1.y - p2.y
        return sqrt(dx * dx + dy * dy)
    }
}

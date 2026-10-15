package com.draw.onme.domain.evaluator

import com.draw.onme.domain.model.Stencil
import com.draw.onme.domain.model.StencilEvaluation
import com.draw.onme.domain.model.Stroke

/**
 * Contract for evaluating user tracing strokes against a target [Stencil].
 */
interface StencilEvaluator {
    /**
     * Evaluates user strokes against reference stencil paths.
     *
     * @param stencil The reference stencil template.
     * @param strokes The strokes drawn by the user.
     * @param canvasSize Reference coordinate space dimension (default 1000f).
     * @return Detailed evaluation including overall score, part feedback, and Pup's tips.
     */
    suspend fun evaluate(
        stencil: Stencil,
        strokes: List<Stroke>,
        canvasSize: Float = 1000f
    ): StencilEvaluation
}

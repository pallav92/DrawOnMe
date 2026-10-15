package com.draw.onme.domain.model

/**
 * Qualitative status for individual stencil segments.
 */
enum class StencilPartStatus {
    EXCELLENT,
    GOOD,
    NEEDS_WORK,
    MISSING
}

/**
 * Feedback for a single labeled part of a stencil (e.g. "Roof", "Wheels", "Door").
 *
 * @property partLabel Human-friendly name of the segment.
 * @property coverageScore Ratio of stencil path covered by user strokes (0.0f to 1.0f).
 * @property status Qualitative ranking of the segment.
 */
data class StencilPartFeedback(
    val partLabel: String,
    val coverageScore: Float,
    val status: StencilPartStatus
)

/**
 * Comprehensive evaluation of user drawing compared against a reference stencil.
 *
 * @property stencilId The ID of the stencil being evaluated.
 * @property overallScore Match score between 0.0f and 1.0f (100%).
 * @property isGood True if the overall score meets or exceeds the 80% threshold.
 * @property partFeedbacks Detailed breakdown for each labeled part of the stencil.
 * @property pupComment Playful feedback message synthesized by Pup.
 * @property suggestedImprovementPart Optional label of the part Pup recommends practicing next.
 */
data class StencilEvaluation(
    val stencilId: String,
    val overallScore: Float,
    val isGood: Boolean,
    val partFeedbacks: List<StencilPartFeedback>,
    val pupComment: String,
    val suggestedImprovementPart: String? = null
) {
    companion object {
        const val GOOD_SCORE_THRESHOLD: Float = 0.80f
    }
}

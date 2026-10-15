package com.draw.onme.domain.model

/**
 * Represents a recognized doodle category.
 *
 * @property id Identifier key matching model label (e.g. "tree", "cloud", "apple").
 * @property displayName User-friendly capitalized name (e.g. "Tree", "Cloud").
 * @property emoji Expressive emoji representing the object (e.g. "🌳", "☁️").
 */
data class DoodleCategory(
    val id: String,
    val displayName: String,
    val emoji: String
)

/**
 * Represents a prediction result from the on-device doodle classifier.
 *
 * @property category The recognized category.
 * @property confidence Float confidence score between 0.0f and 1.0f.
 */
data class DoodlePrediction(
    val category: DoodleCategory,
    val confidence: Float
) {
    /**
     * True if the model confidence reaches or exceeds the 80% decision threshold.
     */
    val isConfidentGuess: Boolean
        get() = confidence >= CONFIDENCE_DECISION_THRESHOLD

    companion object {
        const val CONFIDENCE_DECISION_THRESHOLD: Float = 0.80f
    }
}

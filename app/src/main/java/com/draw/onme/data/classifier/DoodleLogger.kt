package com.draw.onme.data.classifier

import com.draw.onme.domain.model.DoodlePrediction
import java.util.Locale

/**
 * Dedicated logger for on-device Doodle AI model inferences and Pup decision events.
 *
 * Filter in Android Studio Logcat using: `tag:PupAI`
 * Or in terminal via: `adb logcat -s PupAI`
 */
object DoodleLogger {
    const val TAG = "PupAI"

    fun d(message: String) {
        try {
            android.util.Log.d(TAG, message)
        } catch (_: Throwable) {
            println("[$TAG] $message")
        }
    }

    /**
     * Logs classification results, explicitly highlighting non-confident guesses (< 80%)
     * and displaying the candidate score breakdown.
     */
    fun logPredictions(
        predictions: List<DoodlePrediction>,
        dismissedCategories: Set<String> = emptySet()
    ) {
        if (predictions.isEmpty()) {
            d("No predictions (empty or unrecognized strokes)")
            return
        }

        val top = predictions.first()
        val scorePercent = String.format(Locale.US, "%.1f%%", top.confidence * 100f)

        if (top.confidence < DoodlePrediction.CONFIDENCE_DECISION_THRESHOLD) {
            d("🤔 [NOT CONFIDENT] Guessed '${top.category.displayName}' ${top.category.emoji} with score $scorePercent (below 80% threshold)")
        } else if (top.category.id in dismissedCategories) {
            d("🙈 [DISMISSED] Guessed '${top.category.displayName}' ${top.category.emoji} with score $scorePercent (user previously dismissed this)")
        } else {
            d("🎉 [CONFIDENT GUESS] Pup identified '${top.category.displayName}' ${top.category.emoji} with score $scorePercent (>= 80%)!")
        }

        val top5 = predictions.take(5).joinToString(", ") {
            val pct = String.format(Locale.US, "%.1f%%", it.confidence * 100f)
            "${it.category.displayName} ${it.category.emoji}: $pct"
        }
        d("📊 Top candidates: $top5")
    }
}

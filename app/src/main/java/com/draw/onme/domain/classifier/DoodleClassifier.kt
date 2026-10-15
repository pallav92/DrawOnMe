package com.draw.onme.domain.classifier

import com.draw.onme.domain.model.DoodlePrediction
import com.draw.onme.domain.model.Stroke

/**
 * On-device classification contract to identify what object a user has drawn.
 */
interface DoodleClassifier {
    /**
     * Analyzes the provided strokes and returns a ranked list of predictions.
     *
     * @param strokes List of drawn strokes on the canvas.
     * @return Ranked list of predictions sorted by confidence descending.
     */
    suspend fun classify(strokes: List<Stroke>): List<DoodlePrediction>
}

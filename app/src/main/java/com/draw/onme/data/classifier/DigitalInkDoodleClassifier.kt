package com.draw.onme.data.classifier

import com.draw.onme.domain.classifier.DoodleClassifier
import com.draw.onme.domain.model.DoodlePrediction
import com.draw.onme.domain.model.Stroke
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.common.RecognitionCandidate
import com.google.mlkit.vision.digitalink.common.RecognitionResult
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognitionModelIdentifier
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognizer
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.recognition.Ink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

/**
 * On-device sketch recognition classifier powered by Google ML Kit Digital Ink Recognition.
 *
 * Uses the official Google AutoDraw model tag (`zxx-Zsym-x-autodraw`), which processes
 * raw vector strokes and temporal curvature rather than downscaled 28x28 bitmaps.
 *
 * Runs 100% on-device and offline once the model is downloaded.
 * Gracefully falls back to [fallbackClassifier] if the model is still downloading or offline.
 */
class DigitalInkDoodleClassifier(
    private val fallbackClassifier: DoodleClassifier? = null
) : DoodleClassifier {

    private val modelIdentifier: DigitalInkRecognitionModelIdentifier? by lazy {
        try {
            DigitalInkRecognitionModelIdentifier.fromLanguageTag(AUTODRAW_TAG)
        } catch (e: Throwable) {
            DoodleLogger.d("Failed to resolve AutoDraw model identifier: ${e.message}")
            null
        }
    }

    private val model: DigitalInkRecognitionModel? by lazy {
        try {
            modelIdentifier?.let { DigitalInkRecognitionModel.builder(it).build() }
        } catch (e: Throwable) {
            null
        }
    }

    private var recognizer: DigitalInkRecognizer? = null
    private var isDownloading: Boolean = false

    init {
        checkAndDownloadModel()
    }

    /**
     * Checks if the AutoDraw model is downloaded on the device.
     * If not, triggers a background download via Google ML Kit's RemoteModelManager.
     */
    fun checkAndDownloadModel() {
        try {
            val targetModel = model ?: return
            val remoteModelManager = RemoteModelManager.getInstance()

            remoteModelManager.isModelDownloaded(targetModel)
                .addOnSuccessListener { isDownloaded ->
                    if (isDownloaded) {
                        initRecognizer(targetModel)
                        DoodleLogger.d("ML Kit AutoDraw model is downloaded and ready for on-device recognition.")
                    } else if (!isDownloading) {
                        isDownloading = true
                        DoodleLogger.d("ML Kit AutoDraw model not found. Initiating background download (~20MB)...")
                        val conditions = DownloadConditions.Builder().build()
                        remoteModelManager.download(targetModel, conditions)
                            .addOnSuccessListener {
                                isDownloading = false
                                initRecognizer(targetModel)
                                DoodleLogger.d("ML Kit AutoDraw model downloaded successfully! High-accuracy sketch recognition active.")
                            }
                            .addOnFailureListener { error ->
                                isDownloading = false
                                DoodleLogger.d("ML Kit AutoDraw model download failed: ${error.message}. Using fallback classifier.")
                            }
                    }
                }
                .addOnFailureListener { error ->
                    DoodleLogger.d("Error checking ML Kit model status: ${error.message}")
                }
        } catch (e: Throwable) {
            DoodleLogger.d("ML Kit unavailable in current environment: ${e.message}. Using fallback.")
        }
    }

    @Synchronized
    private fun initRecognizer(targetModel: DigitalInkRecognitionModel) {
        if (recognizer == null) {
            try {
                val options = DigitalInkRecognizerOptions.builder(targetModel).build()
                recognizer = DigitalInkRecognition.getClient(options)
            } catch (e: Throwable) {
                DoodleLogger.d("Error initializing DigitalInkRecognizer: ${e.message}")
            }
        }
    }

    override suspend fun classify(strokes: List<Stroke>): List<DoodlePrediction> = withContext(Dispatchers.Default) {
        if (strokes.isEmpty()) {
            return@withContext emptyList()
        }

        val activeRecognizer = recognizer
        if (activeRecognizer != null) {
            val mlKitPredictions = classifyWithMlKit(activeRecognizer, strokes)
            if (mlKitPredictions.isNotEmpty()) {
                return@withContext mlKitPredictions
            }
        } else {
            // Check if model became available in the background
            model?.let { checkAndDownloadModel() }
        }

        // Delegate to fallback classifier if ML Kit is not ready yet
        fallbackClassifier?.classify(strokes) ?: emptyList()
    }

    private suspend fun classifyWithMlKit(
        recognizer: DigitalInkRecognizer,
        strokes: List<Stroke>
    ): List<DoodlePrediction> {
        val inkBuilder = Ink.builder()
        var timeMs = 0L

        for (stroke in strokes) {
            if (stroke.points.isEmpty()) continue
            val strokeBuilder = Ink.Stroke.builder()
            for (p in stroke.points) {
                strokeBuilder.addPoint(Ink.Point.create(p.x, p.y, timeMs))
                timeMs += 10L
            }
            inkBuilder.addStroke(strokeBuilder.build())
        }

        val ink = inkBuilder.build()
        if (ink.strokes.isEmpty()) {
            return emptyList()
        }

        return suspendCancellableCoroutine { continuation ->
            recognizer.recognize(ink)
                .addOnSuccessListener { result: RecognitionResult ->
                    val candidates: List<RecognitionCandidate> = result.candidates
                    if (candidates.isEmpty()) {
                        continuation.resume(emptyList())
                        return@addOnSuccessListener
                    }

                    // Map ML Kit candidates into domain predictions
                    // In AutoDraw, candidates are ranked by probability
                    val predictions = candidates.mapIndexed { index, candidate ->
                        val category = DoodleCategoryCatalog.findById(candidate.text)
                        val confidence = when {
                            candidate.score != null -> {
                                val raw = candidate.score!!
                                if (raw in 0f..1f) raw else (1f / (1f + kotlin.math.abs(raw)))
                            }
                            index == 0 -> 0.88f // Top candidate reaches decision threshold
                            index == 1 -> 0.65f
                            index == 2 -> 0.45f
                            else -> (0.40f - (index * 0.05f)).coerceAtLeast(0.10f)
                        }
                        DoodlePrediction(category = category, confidence = confidence)
                    }

                    val top = predictions.firstOrNull()
                    if (top != null) {
                        val scorePct = String.format(Locale.US, "%.1f%%", top.confidence * 100f)
                        if (top.confidence < DoodlePrediction.CONFIDENCE_DECISION_THRESHOLD) {
                            DoodleLogger.d("✍️ [ML Kit AutoDraw NOT CONFIDENT] Guess: '${top.category.displayName}' ${top.category.emoji} | Score: $scorePct")
                        } else {
                            DoodleLogger.d("✍️ [ML Kit AutoDraw CONFIDENT] Guess: '${top.category.displayName}' ${top.category.emoji} | Score: $scorePct")
                        }
                    }

                    continuation.resume(predictions)
                }
                .addOnFailureListener { error ->
                    DoodleLogger.d("ML Kit recognition failed: ${error.message}")
                    continuation.resume(emptyList())
                }
        }
    }

    fun close() {
        recognizer?.close()
        recognizer = null
    }

    companion object {
        const val AUTODRAW_TAG = "zxx-Zsym-x-autodraw"
    }
}

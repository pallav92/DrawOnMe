package com.draw.onme.data.classifier

import android.content.Context
import com.draw.onme.domain.classifier.DoodleClassifier

/**
 * Thread-safe provider for the on-device doodle classifier instance.
 * Initializes Google ML Kit's vector AutoDraw engine with a lightweight,
 * zero-dependency pure Kotlin structural analyzer as an instant fallback.
 */
object DoodleClassifierProvider {

    @Volatile
    private var instance: DoodleClassifier? = null

    fun get(context: Context): DoodleClassifier {
        return instance ?: synchronized(this) {
            instance ?: run {
                val fallback = StructuralDoodleClassifier()
                DigitalInkDoodleClassifier(fallbackClassifier = fallback).also {
                    instance = it
                }
            }
        }
    }
}

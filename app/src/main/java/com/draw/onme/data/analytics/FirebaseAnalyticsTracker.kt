package com.draw.onme.data.analytics

import android.content.Context
import android.os.Bundle
import com.draw.onme.domain.analytics.AnalyticsTracker
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Concrete implementation of [AnalyticsTracker] routing telemetry directly to [FirebaseAnalytics].
 */
class FirebaseAnalyticsTracker(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsTracker {

    override fun trackScreenView(screenName: String, screenClass: String?) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass ?: screenName)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    override fun trackScreenEngagement(
        screenName: String,
        durationSeconds: Long,
        params: Map<String, Any>
    ) {
        val bundle = mapToBundle(params).apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putLong("duration_seconds", durationSeconds)
            putLong("duration_ms", durationSeconds * 1000L)
        }
        firebaseAnalytics.logEvent(EVENT_SCREEN_ENGAGEMENT, bundle)
    }

    override fun trackShare(artworkId: String, hasStencil: Boolean, strokeCount: Int) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "artwork")
            putString(FirebaseAnalytics.Param.ITEM_ID, artworkId)
            putBoolean("has_stencil", hasStencil)
            putInt("stroke_count", strokeCount)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle)
    }

    override fun trackPrint(artworkId: String, hasStencil: Boolean, strokeCount: Int) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, artworkId)
            putBoolean("has_stencil", hasStencil)
            putInt("stroke_count", strokeCount)
        }
        firebaseAnalytics.logEvent(EVENT_PRINT_ARTWORK, bundle)
    }

    override fun trackBlankPress(target: String, screenName: String) {
        val bundle = Bundle().apply {
            putString("target", target)
            putString("screen_name", screenName)
        }
        firebaseAnalytics.logEvent(EVENT_BLANK_PRESS, bundle)
    }

    override fun trackEvent(eventName: String, params: Map<String, Any>) {
        val bundle = mapToBundle(params)
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    private fun mapToBundle(params: Map<String, Any>): Bundle {
        val bundle = Bundle()
        for ((key, value) in params) {
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Double -> bundle.putDouble(key, value)
                is Float -> bundle.putFloat(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                else -> bundle.putString(key, value.toString())
            }
        }
        return bundle
    }

    companion object {
        const val EVENT_SCREEN_ENGAGEMENT = "screen_engagement"
        const val EVENT_PRINT_ARTWORK = "print_artwork"
        const val EVENT_BLANK_PRESS = "blank_press"

        @Volatile
        private var instance: FirebaseAnalyticsTracker? = null

        fun getInstance(context: Context): FirebaseAnalyticsTracker {
            return instance ?: synchronized(this) {
                instance ?: FirebaseAnalyticsTracker(
                    FirebaseAnalytics.getInstance(context.applicationContext)
                ).also { instance = it }
            }
        }
    }
}

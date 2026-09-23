package com.draw.onme.presentation.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Automatically records screen view on entry and tracks active duration on exit/backgrounding.
 *
 * @param screenName Canonical name of the screen.
 * @param params Additional metadata associated with this screen view.
 */
@Composable
fun TrackScreenEngagement(
    screenName: String,
    params: Map<String, Any> = emptyMap()
) {
    val tracker = LocalAnalyticsTracker.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentParams by rememberUpdatedState(params)

    DisposableEffect(screenName, lifecycleOwner) {
        tracker.trackScreenView(screenName)

        var enterTimestamp = System.currentTimeMillis()
        var hasLoggedForCurrentWindow = false

        val flushDuration = {
            if (!hasLoggedForCurrentWindow) {
                val now = System.currentTimeMillis()
                val elapsedSeconds = maxOf(1L, (now - enterTimestamp) / 1000L)
                tracker.trackScreenEngagement(screenName, elapsedSeconds, currentParams)
                hasLoggedForCurrentWindow = true
            }
        }

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    enterTimestamp = System.currentTimeMillis()
                    hasLoggedForCurrentWindow = false
                }
                Lifecycle.Event.ON_PAUSE -> {
                    flushDuration()
                }
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            flushDuration()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

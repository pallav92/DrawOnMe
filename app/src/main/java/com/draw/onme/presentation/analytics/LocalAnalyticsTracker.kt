package com.draw.onme.presentation.analytics

import androidx.compose.runtime.staticCompositionLocalOf
import com.draw.onme.domain.analytics.AnalyticsTracker
import com.draw.onme.domain.analytics.NoOpAnalyticsTracker

/**
 * CompositionLocal providing access to [AnalyticsTracker] throughout the Compose hierarchy.
 */
val LocalAnalyticsTracker = staticCompositionLocalOf<AnalyticsTracker> {
    NoOpAnalyticsTracker
}

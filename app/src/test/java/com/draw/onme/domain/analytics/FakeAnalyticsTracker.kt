package com.draw.onme.domain.analytics

/**
 * In-memory test double for [AnalyticsTracker] verifying telemetry logging in unit tests.
 */
class FakeAnalyticsTracker : AnalyticsTracker {

    data class ScreenViewEvent(val screenName: String, val screenClass: String?)
    data class ScreenEngagementEvent(val screenName: String, val durationSeconds: Long, val params: Map<String, Any>)
    data class ShareEvent(val artworkId: String, val hasStencil: Boolean, val strokeCount: Int)
    data class PrintEvent(val artworkId: String, val hasStencil: Boolean, val strokeCount: Int)
    data class BlankPressEvent(val target: String, val screenName: String)
    data class CustomEvent(val eventName: String, val params: Map<String, Any>)

    val screenViews = mutableListOf<ScreenViewEvent>()
    val screenEngagements = mutableListOf<ScreenEngagementEvent>()
    val shares = mutableListOf<ShareEvent>()
    val prints = mutableListOf<PrintEvent>()
    val blankPresses = mutableListOf<BlankPressEvent>()
    val customEvents = mutableListOf<CustomEvent>()

    override fun trackScreenView(screenName: String, screenClass: String?) {
        screenViews.add(ScreenViewEvent(screenName, screenClass))
    }

    override fun trackScreenEngagement(
        screenName: String,
        durationSeconds: Long,
        params: Map<String, Any>
    ) {
        screenEngagements.add(ScreenEngagementEvent(screenName, durationSeconds, params))
    }

    override fun trackShare(artworkId: String, hasStencil: Boolean, strokeCount: Int) {
        shares.add(ShareEvent(artworkId, hasStencil, strokeCount))
    }

    override fun trackPrint(artworkId: String, hasStencil: Boolean, strokeCount: Int) {
        prints.add(PrintEvent(artworkId, hasStencil, strokeCount))
    }

    override fun trackBlankPress(target: String, screenName: String) {
        blankPresses.add(BlankPressEvent(target, screenName))
    }

    override fun trackEvent(eventName: String, params: Map<String, Any>) {
        customEvents.add(CustomEvent(eventName, params))
    }

    fun clear() {
        screenViews.clear()
        screenEngagements.clear()
        shares.clear()
        prints.clear()
        blankPresses.clear()
        customEvents.clear()
    }
}

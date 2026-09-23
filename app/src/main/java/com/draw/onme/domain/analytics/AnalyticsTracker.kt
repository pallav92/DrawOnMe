package com.draw.onme.domain.analytics

/**
 * Domain contract for logging user analytics, screen engagement, and interaction telemetry.
 *
 * Adheres to Clean Architecture by remaining completely free of Android framework dependencies.
 */
interface AnalyticsTracker {
    /**
     * Logs navigation to a screen destination.
     *
     * @param screenName Canonical name of the screen (e.g. "onboarding", "free_scribble").
     * @param screenClass Optional screen class name.
     */
    fun trackScreenView(screenName: String, screenClass: String? = null)

    /**
     * Logs the active duration spent on a screen upon exit or backgrounding.
     *
     * @param screenName Canonical name of the screen.
     * @param durationSeconds Active time spent in seconds.
     * @param params Optional additional context (e.g. stencilId).
     */
    fun trackScreenEngagement(
        screenName: String,
        durationSeconds: Long,
        params: Map<String, Any> = emptyMap()
    )

    /**
     * Logs an artwork share action via Android system share sheet.
     *
     * @param artworkId Identifier of the shared artwork.
     * @param hasStencil True if the artwork was created using a stencil template.
     * @param strokeCount Total number of drawing strokes in the artwork.
     */
    fun trackShare(artworkId: String, hasStencil: Boolean, strokeCount: Int)

    /**
     * Logs an artwork print action via Android PrintManager.
     *
     * @param artworkId Identifier of the printed artwork.
     * @param hasStencil True if the artwork was created using a stencil template.
     * @param strokeCount Total number of drawing strokes in the artwork.
     */
    fun trackPrint(artworkId: String, hasStencil: Boolean, strokeCount: Int)

    /**
     * Logs a blank press or dead click where user interacted with an empty state or inactive area.
     *
     * @param target Specific interaction target (e.g. "empty_canvas_save", "empty_magic_wand", "empty_fridge_tap").
     * @param screenName Screen where the blank press occurred.
     */
    fun trackBlankPress(target: String, screenName: String)

    /**
     * Logs a custom domain analytics event with structured parameters.
     *
     * @param eventName Name of the event.
     * @param params Key-value parameters.
     */
    fun trackEvent(eventName: String, params: Map<String, Any> = emptyMap())
}

/**
 * Safe no-op implementation of [AnalyticsTracker] for unit tests, previews, and fallbacks.
 */
object NoOpAnalyticsTracker : AnalyticsTracker {
    override fun trackScreenView(screenName: String, screenClass: String?) = Unit
    override fun trackScreenEngagement(
        screenName: String,
        durationSeconds: Long,
        params: Map<String, Any>
    ) = Unit
    override fun trackShare(artworkId: String, hasStencil: Boolean, strokeCount: Int) = Unit
    override fun trackPrint(artworkId: String, hasStencil: Boolean, strokeCount: Int) = Unit
    override fun trackBlankPress(target: String, screenName: String) = Unit
    override fun trackEvent(eventName: String, params: Map<String, Any>) = Unit
}

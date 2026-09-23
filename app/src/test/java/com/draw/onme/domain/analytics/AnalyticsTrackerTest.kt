package com.draw.onme.domain.analytics

import com.draw.onme.presentation.navigation.AppScreen
import com.draw.onme.presentation.navigation.analyticsName
import com.draw.onme.presentation.navigation.analyticsParams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AnalyticsTrackerTest {

    private lateinit var tracker: FakeAnalyticsTracker

    @Before
    fun setUp() {
        tracker = FakeAnalyticsTracker()
    }

    @Test
    fun `trackScreenView records correct screen names`() {
        tracker.trackScreenView("onboarding")
        tracker.trackScreenView("free_scribble")

        assertEquals(2, tracker.screenViews.size)
        assertEquals("onboarding", tracker.screenViews[0].screenName)
        assertEquals("free_scribble", tracker.screenViews[1].screenName)
    }

    @Test
    fun `trackScreenEngagement records screen name and duration`() {
        tracker.trackScreenEngagement("free_scribble", 45L, mapOf("strokes" to 12))

        assertEquals(1, tracker.screenEngagements.size)
        val event = tracker.screenEngagements.first()
        assertEquals("free_scribble", event.screenName)
        assertEquals(45L, event.durationSeconds)
        assertEquals(12, event.params["strokes"])
    }

    @Test
    fun `trackShare records artwork details`() {
        tracker.trackShare(artworkId = "art_123", hasStencil = true, strokeCount = 8)

        assertEquals(1, tracker.shares.size)
        val share = tracker.shares.first()
        assertEquals("art_123", share.artworkId)
        assertTrue(share.hasStencil)
        assertEquals(8, share.strokeCount)
    }

    @Test
    fun `trackPrint records artwork details`() {
        tracker.trackPrint(artworkId = "art_456", hasStencil = false, strokeCount = 15)

        assertEquals(1, tracker.prints.size)
        val print = tracker.prints.first()
        assertEquals("art_456", print.artworkId)
        assertEquals(false, print.hasStencil)
        assertEquals(15, print.strokeCount)
    }

    @Test
    fun `trackSaveToGallery records artwork details`() {
        tracker.trackSaveToGallery(artworkId = "art_789", hasStencil = true, strokeCount = 20)

        assertEquals(1, tracker.savesToGallery.size)
        val save = tracker.savesToGallery.first()
        assertEquals("art_789", save.artworkId)
        assertEquals(true, save.hasStencil)
        assertEquals(20, save.strokeCount)
    }

    @Test
    fun `trackBlankPress records target and screen name`() {
        tracker.trackBlankPress(target = "empty_canvas_save", screenName = "free_scribble")
        tracker.trackBlankPress(target = "empty_magic_wand", screenName = "stencil_drawing")
        tracker.trackBlankPress(target = "empty_fridge_tap", screenName = "fridge_gallery")

        assertEquals(3, tracker.blankPresses.size)
        assertEquals("empty_canvas_save", tracker.blankPresses[0].target)
        assertEquals("free_scribble", tracker.blankPresses[0].screenName)

        assertEquals("empty_magic_wand", tracker.blankPresses[1].target)
        assertEquals("stencil_drawing", tracker.blankPresses[1].screenName)

        assertEquals("empty_fridge_tap", tracker.blankPresses[2].target)
        assertEquals("fridge_gallery", tracker.blankPresses[2].screenName)
    }

    @Test
    fun `AppScreen analyticsName mapping is complete and consistent`() {
        assertEquals("splash", AppScreen.Splash.analyticsName)
        assertEquals("onboarding", AppScreen.Onboarding.analyticsName)
        assertEquals("free_scribble", AppScreen.FreeScribble.analyticsName)
        assertEquals("stencil_gallery", AppScreen.StencilGallery.analyticsName)
        assertEquals("stencil_drawing", AppScreen.StencilDrawing("house").analyticsName)
        assertEquals("fridge_gallery", AppScreen.FridgeGallery.analyticsName)

        assertEquals("house", AppScreen.StencilDrawing("house").analyticsParams["stencil_id"])
        assertTrue(AppScreen.FreeScribble.analyticsParams.isEmpty())
    }

    @Test
    fun `NoOpAnalyticsTracker executes safely without exceptions`() {
        NoOpAnalyticsTracker.trackScreenView("test")
        NoOpAnalyticsTracker.trackScreenEngagement("test", 10L)
        NoOpAnalyticsTracker.trackShare("1", false, 1)
        NoOpAnalyticsTracker.trackPrint("1", false, 1)
        NoOpAnalyticsTracker.trackBlankPress("target", "screen")
        NoOpAnalyticsTracker.trackEvent("event")
    }
}

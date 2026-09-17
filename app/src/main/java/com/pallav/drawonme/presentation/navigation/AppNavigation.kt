package com.pallav.drawonme.presentation.navigation

/**
 * Destinations within the DrawOnMe application.
 */
sealed interface AppScreen {
    /**
     * Home / mode selection landing screen.
     */
    data object Onboarding : AppScreen

    /**
     * Open-ended freehand scribble board.
     */
    data object FreeScribble : AppScreen

    /**
     * Cartoon stencil character gallery.
     */
    data object StencilGallery : AppScreen

    /**
     * Guided tracing canvas for a specific stencil.
     */
    data class StencilDrawing(val stencilId: String) : AppScreen
}

package com.draw.onme.presentation.navigation

/**
 * Destinations within the DrawOnMe application.
 */
sealed interface AppScreen {
    /**
     * Whimsical animated intro splash screen.
     */
    data object Splash : AppScreen

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

    /**
     * Virtual refrigerator door gallery displaying child's pinned artworks.
     */
    data object FridgeGallery : AppScreen

    companion object {
        fun serialize(screen: AppScreen): String = when (screen) {
            is Splash -> "splash"
            is Onboarding -> "onboarding"
            is FreeScribble -> "free_scribble"
            is StencilGallery -> "stencil_gallery"
            is StencilDrawing -> "stencil_drawing:${screen.stencilId}"
            is FridgeGallery -> "fridge_gallery"
        }

        fun deserialize(value: String): AppScreen {
            return when {
                value == "splash" -> Splash
                value == "onboarding" -> Onboarding
                value == "free_scribble" -> FreeScribble
                value == "stencil_gallery" -> StencilGallery
                value.startsWith("stencil_drawing:") -> StencilDrawing(value.removePrefix("stencil_drawing:"))
                value == "fridge_gallery" -> FridgeGallery
                else -> Onboarding
            }
        }

        val BackStackSaver: androidx.compose.runtime.saveable.Saver<androidx.compose.runtime.MutableState<List<AppScreen>>, ArrayList<String>> =
            androidx.compose.runtime.saveable.Saver(
                save = { state -> ArrayList(state.value.map { serialize(it) }) },
                restore = { savedList -> androidx.compose.runtime.mutableStateOf(savedList.map { deserialize(it) }) }
            )
    }
}

val AppScreen.analyticsName: String
    get() = when (this) {
        is AppScreen.Splash -> "splash"
        is AppScreen.Onboarding -> "onboarding"
        is AppScreen.FreeScribble -> "free_scribble"
        is AppScreen.StencilGallery -> "stencil_gallery"
        is AppScreen.StencilDrawing -> "stencil_drawing"
        is AppScreen.FridgeGallery -> "fridge_gallery"
    }

val AppScreen.analyticsParams: Map<String, Any>
    get() = when (this) {
        is AppScreen.StencilDrawing -> mapOf("stencil_id" to stencilId)
        else -> emptyMap()
    }

package com.pallav.drawonme.presentation.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaverScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppNavigationTest {

    private val dummySaverScope = SaverScope { true }

    @Test
    fun serializeAndDeserialize_allAppScreens() {
        val testScreens = listOf(
            AppScreen.Splash,
            AppScreen.Onboarding,
            AppScreen.FreeScribble,
            AppScreen.StencilGallery,
            AppScreen.StencilDrawing("cozy-house"),
            AppScreen.StencilDrawing("rocket-blast-123"),
            AppScreen.FridgeGallery
        )

        for (screen in testScreens) {
            val serialized = AppScreen.serialize(screen)
            val deserialized = AppScreen.deserialize(serialized)
            assertEquals("Failed for screen: $screen", screen, deserialized)
        }
    }

    @Test
    fun deserialize_unknownString_defaultsToOnboarding() {
        val result = AppScreen.deserialize("invalid_screen_key_xyz")
        assertEquals(AppScreen.Onboarding, result)
    }

    @Test
    fun backStackSaver_savesAndRestoresBackStack() {
        val initialStack = listOf(
            AppScreen.Splash,
            AppScreen.Onboarding,
            AppScreen.StencilGallery,
            AppScreen.StencilDrawing("cute-cat")
        )
        val stackState = mutableStateOf(initialStack)

        val saved = with(AppScreen.BackStackSaver) { dummySaverScope.save(stackState) }
        assertTrue(saved != null)
        assertEquals(4, saved!!.size)
        assertEquals("splash", saved[0])
        assertEquals("onboarding", saved[1])
        assertEquals("stencil_gallery", saved[2])
        assertEquals("stencil_drawing:cute-cat", saved[3])

        val restoredState = AppScreen.BackStackSaver.restore(saved)
        assertTrue(restoredState != null)
        assertEquals(initialStack, restoredState!!.value)
    }
}

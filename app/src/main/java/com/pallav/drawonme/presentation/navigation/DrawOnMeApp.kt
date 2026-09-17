package com.pallav.drawonme.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.pallav.drawonme.presentation.onboarding.OnboardingScreen
import com.pallav.drawonme.presentation.scribble.ScribbleScreen
import com.pallav.drawonme.presentation.stencil.drawing.StencilDrawingScreen
import com.pallav.drawonme.presentation.stencil.gallery.StencilGalleryScreen

/**
 * Root composable hosting the app navigation stack between Onboarding,
 * Free Scribble notepad, Stencil Gallery, and Stencil Drawing Studio.
 */
@Composable
fun DrawOnMeApp(
    modifier: Modifier = Modifier
) {
    var backStack by remember { mutableStateOf(listOf<AppScreen>(AppScreen.Onboarding)) }
    val currentScreen = backStack.last()

    val canGoBack = backStack.size > 1
    val popBack: () -> Unit = {
        if (canGoBack) {
            backStack = backStack.dropLast(1)
        }
    }

    BackHandler(enabled = canGoBack) {
        popBack()
    }

    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        when (currentScreen) {
            is AppScreen.Onboarding -> {
                OnboardingScreen(
                    onSelectFreeScribble = {
                        backStack = backStack + AppScreen.FreeScribble
                    },
                    onSelectStencils = {
                        backStack = backStack + AppScreen.StencilGallery
                    }
                )
            }

            is AppScreen.FreeScribble -> {
                ScribbleScreen(
                    onNavigateBack = popBack
                )
            }

            is AppScreen.StencilGallery -> {
                StencilGalleryScreen(
                    onSelectStencil = { stencilId ->
                        backStack = backStack + AppScreen.StencilDrawing(stencilId)
                    },
                    onNavigateBack = popBack
                )
            }

            is AppScreen.StencilDrawing -> {
                StencilDrawingScreen(
                    stencilId = currentScreen.stencilId,
                    onNavigateBack = popBack
                )
            }
        }
    }
}

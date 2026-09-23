package com.draw.onme.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.draw.onme.data.repository.FileArtworkRepository
import com.draw.onme.data.repository.FileBoardDraftRepository
import com.draw.onme.domain.repository.ArtworkRepository
import com.draw.onme.domain.repository.BoardDraftRepository
import com.draw.onme.presentation.fridge.FridgeGalleryScreen
import com.draw.onme.presentation.onboarding.OnboardingScreen
import com.draw.onme.presentation.scribble.ScribbleScreen
import com.draw.onme.presentation.splash.SplashScreen
import com.draw.onme.presentation.stencil.drawing.StencilDrawingScreen
import com.draw.onme.presentation.stencil.gallery.StencilGalleryScreen
import java.io.File

/**
 * Root composable hosting the app navigation stack between Onboarding,
 * Free Scribble notepad, Stencil Gallery, Stencil Drawing Studio, and My Fridge Door.
 */
@Composable
fun DrawOnMeApp(
    modifier: Modifier = Modifier,
    artworkRepository: ArtworkRepository? = null,
    boardDraftRepository: BoardDraftRepository? = null
) {
    val context = LocalContext.current
    val repository: ArtworkRepository = remember {
        artworkRepository ?: FileArtworkRepository(File(context.filesDir, "saved_artworks"))
    }
    val draftRepository: BoardDraftRepository = remember {
        boardDraftRepository ?: FileBoardDraftRepository(File(context.filesDir, "board_drafts"))
    }

    var backStack by androidx.compose.runtime.saveable.rememberSaveable(saver = AppScreen.BackStackSaver) {
        mutableStateOf(listOf<AppScreen>(AppScreen.Splash))
    }
    val currentScreen = backStack.last()

    val canGoBack = backStack.size > 1 && currentScreen !is AppScreen.Splash
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
            is AppScreen.Splash -> {
                SplashScreen(
                    onSplashFinished = {
                        backStack = listOf(AppScreen.Onboarding)
                    }
                )
            }

            is AppScreen.Onboarding -> {
                OnboardingScreen(
                    onSelectFreeScribble = {
                        backStack = backStack + AppScreen.FreeScribble
                    },
                    onSelectStencils = {
                        backStack = backStack + AppScreen.StencilGallery
                    },
                    onSelectFridge = {
                        backStack = backStack + AppScreen.FridgeGallery
                    }
                )
            }

            is AppScreen.FreeScribble -> {
                ScribbleScreen(
                    onNavigateBack = popBack,
                    artworkRepository = repository,
                    boardDraftRepository = draftRepository
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
                    onNavigateBack = popBack,
                    artworkRepository = repository,
                    boardDraftRepository = draftRepository
                )
            }

            is AppScreen.FridgeGallery -> {
                FridgeGalleryScreen(
                    repository = repository,
                    onNavigateBack = popBack,
                    onStartNewDrawing = {
                        backStack = backStack + AppScreen.FreeScribble
                    }
                )
            }
        }
    }
}

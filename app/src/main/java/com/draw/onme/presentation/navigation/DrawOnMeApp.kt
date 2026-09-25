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
import com.draw.onme.data.repository.FileColoringDraftRepository
import com.draw.onme.data.repository.InMemoryColoringBookRepository
import com.draw.onme.domain.repository.ArtworkRepository
import com.draw.onme.domain.repository.BoardDraftRepository
import com.draw.onme.domain.repository.ColoringBookRepository
import com.draw.onme.domain.repository.ColoringDraftRepository
import com.draw.onme.presentation.coloring.gallery.ColoringGalleryScreen
import com.draw.onme.presentation.coloring.studio.ColoringStudioScreen
import com.draw.onme.presentation.fridge.FridgeGalleryScreen
import com.draw.onme.presentation.onboarding.OnboardingScreen
import com.draw.onme.presentation.scribble.ScribbleScreen
import com.draw.onme.presentation.splash.SplashScreen
import com.draw.onme.presentation.stencil.drawing.StencilDrawingScreen
import com.draw.onme.presentation.stencil.gallery.StencilGalleryScreen
import java.io.File

import androidx.compose.runtime.CompositionLocalProvider
import com.draw.onme.data.analytics.FirebaseAnalyticsTracker
import com.draw.onme.domain.analytics.AnalyticsTracker
import com.draw.onme.presentation.analytics.LocalAnalyticsTracker
import com.draw.onme.presentation.analytics.TrackScreenEngagement

/**
 * Root composable hosting the app navigation stack between Onboarding,
 * Free Scribble notepad, Stencil Gallery, Coloring Studio, and My Fridge Door.
 */
@Composable
fun DrawOnMeApp(
    modifier: Modifier = Modifier,
    artworkRepository: ArtworkRepository? = null,
    boardDraftRepository: BoardDraftRepository? = null,
    coloringBookRepository: ColoringBookRepository? = null,
    coloringDraftRepository: ColoringDraftRepository? = null,
    analyticsTracker: AnalyticsTracker? = null
) {
    val context = LocalContext.current
    val repository: ArtworkRepository = remember {
        artworkRepository ?: FileArtworkRepository(File(context.filesDir, "saved_artworks"))
    }
    val draftRepository: BoardDraftRepository = remember {
        boardDraftRepository ?: FileBoardDraftRepository(File(context.filesDir, "board_drafts"))
    }
    val coloringBookRepo: ColoringBookRepository = remember {
        coloringBookRepository ?: InMemoryColoringBookRepository()
    }
    val coloringDraftRepo: ColoringDraftRepository = remember {
        coloringDraftRepository ?: FileColoringDraftRepository(File(context.filesDir, "coloring_drafts"))
    }
    val tracker: AnalyticsTracker = remember {
        analyticsTracker ?: FirebaseAnalyticsTracker.getInstance(context)
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

    CompositionLocalProvider(
        LocalAnalyticsTracker provides tracker
    ) {
        TrackScreenEngagement(
            screenName = currentScreen.analyticsName,
            params = currentScreen.analyticsParams
        )

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
                        tracker.trackEvent("mode_selected", mapOf("mode" to "free_scribble"))
                        backStack = backStack + AppScreen.FreeScribble
                    },
                    onSelectColoringBook = {
                        tracker.trackEvent("mode_selected", mapOf("mode" to "coloring_book"))
                        backStack = backStack + AppScreen.ColoringGallery
                    },
                    onSelectStencils = {
                        tracker.trackEvent("mode_selected", mapOf("mode" to "stencils"))
                        backStack = backStack + AppScreen.StencilGallery
                    },
                    onSelectFridge = {
                        tracker.trackEvent("mode_selected", mapOf("mode" to "fridge"))
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

            is AppScreen.ColoringGallery -> {
                ColoringGalleryScreen(
                    repository = coloringBookRepo,
                    onSelectPage = { pageId ->
                        tracker.trackEvent("coloring_page_selected", mapOf("page_id" to pageId))
                        backStack = backStack + AppScreen.ColoringStudio(pageId)
                    },
                    onNavigateBack = popBack
                )
            }

            is AppScreen.ColoringStudio -> {
                ColoringStudioScreen(
                    pageId = currentScreen.pageId,
                    onNavigateBack = popBack,
                    repository = coloringBookRepo,
                    draftRepository = coloringDraftRepo,
                    artworkRepository = repository
                )
            }

            is AppScreen.StencilGallery -> {
                StencilGalleryScreen(
                    onSelectStencil = { stencilId ->
                        tracker.trackEvent("stencil_selected", mapOf("stencil_id" to stencilId))
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
}

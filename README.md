# DrawOnMe 🎨

A delightful, safe, and intuitive Android drawing and tracing app designed specifically for budding young artists (ages 3–8) and their parents. Built 100% with modern **Jetpack Compose**, **Kotlin Coroutines**, and **Clean Architecture**.

---

## 🌟 Highlights & Key Features

- **🚀 Dual-Layer Splash Experience**:
  Zero-flicker native Android 12+ cold start (`androidx.core:core-splashscreen`) paired with a whimsical animated Jetpack Compose intro featuring bouncing palette animations, floating color bubbles, self-drawing rainbow brush underlines, and instant-skip touch interaction.
- **🌈 "First Touch" Rainbow Splash Pad**:
  An interactive, tactile canvas directly on the onboarding screen where kids can touch and drag to paint glowing rainbow trails with multi-touch support, multi-layer neon glow, and particle sparkles before making any navigation choices.
- **🎨 Magic Doodle (Freehand Studio)**:
  A responsive blank canvas with low-latency touch pointer tracking and quadratic Bézier curve smoothing. Includes Pen and Eraser tools, 8 vibrant child-friendly colors (starting with default Red), 4 stroke widths, and multi-level undo/redo history.
- **🖐️ Pan, Zoom & Infinite Workspace Navigation**:
  Intuitive two-finger pan & pinch-to-zoom (`0.5x` to `4.0x`) with smooth inertia, a 5x device workspace boundary, HUD zoom indicator, and a dedicated Hand tool for 1- or 2-finger panning.
- **🎈 2D Movable FAB & Collapsible Toolbar**:
  A draggable floating action button that moves freely in 2D across the canvas and smoothly flings/snaps to the nearest left or right border. Expands into a streamlined floating tool palette with directional collapsing to maximize drawing space.
- **🧭 Canonical Square Canvas & Orientation Resilience**:
  Stencil Studio templates and user strokes are mapped to a canonical 1000×1000 square coordinate space. Rotating between portrait and landscape maintains exact 1:1 stroke alignment over stencil guides with zero drift and uninterrupted touch precision.
- **✨ Cartoon & Geometric Stencil Studio**:
  8 large, recognizable geometric tracing templates rendered in soft pastel lavender that teach kids how to draw using basic shapes (triangles, rectangles, circles, ovals, and arcs):
  - **Vehicles & Things**: 🏠 Cozy House, 🚗 Zooming Car, 🚀 Space Rocket, ⛵ Happy Sailboat
  - **Animals & Friends**: ☀️ Smiling Sun, 🧸 Cute Teddy Bear, 🐱 Playful Kitty, 🐭 Playful Mouse
- **🪄 Magic Wand Reveal & Celebration**:
  Tapping the Magic Wand button auto-hides tracing guidelines to unveil the child's clean hand-drawn artwork, triggering an animated confetti particle explosion with a *"You Did It! 🌟"* trophy badge.
- **💾 Isolated Board Draft Persistence**:
  File-backed draft persistence (`FileBoardDraftRepository`) that safely auto-saves in-progress artwork per board (Free Doodle and individual Stencils), surviving app switches, rotation, and process death.
- **🖼️ "My Fridge" Virtual Art Gallery with Smart Cropping**:
  A refrigerator door where saved drawings are displayed on pinned paper sheets held up by colorful fridge magnets (`⭐️`, `🍓`, `🚀`, `🍀`, `🧁`, `🦕`, `🧸`, `🎨`). Powered by `ArtworkCropper` to dynamically crop tight bounds and center masterpieces without empty canvas margins.
- **🛡️ 100% Offline & Kid-Safe**:
  Zero ads, zero third-party trackers, zero internet permissions requested, and zero data collection. All drawings and settings remain safely on the local device.

---

## 📐 Architecture & Engineering Standards

DrawOnMe strictly follows **Clean Architecture**, **Unidirectional Data Flow (UDF)**, and **Single Source of Truth (SSOT)** principles:

```
app/src/main/java/com/pallav/drawonme/
 ├── domain/                             # Pure Kotlin business entities & repository contracts (Zero Android dependencies)
 │    ├── model/                         # Stroke, Point, DrawingTool, Stencil, SavedArtwork, ArtworkCropper
 │    └── repository/                    # StencilRepository, ArtworkRepository, BoardDraftRepository
 ├── data/                               # Data implementations and offline storage
 │    └── repository/                    # FileArtworkRepository, FileBoardDraftRepository, InMemoryStencilRepository
 └── presentation/                       # Declarative Jetpack Compose UI & State Management
      ├── fridge/                        # FridgeGalleryScreen & Refrigerator Canvas UI
      ├── navigation/                    # AppNavigation state machine, backstack persistence, DrawOnMeApp
      ├── onboarding/                    # OnboardingScreen, RainbowSplashPad, SparkleParticleSystem
      ├── scribble/                      # ScribbleScreen, ScribbleViewModel, ScribbleCanvas, DrawingToolbar, CanvasTransformState
      ├── splash/                        # SplashScreen with animated palette & floating bubbles
      └── stencil/                       # StencilGalleryScreen, StencilDrawingScreen, CelebrationOverlay
```

### Layer Isolation & Principles
- **Domain Layer**: Contains pure Kotlin data structures, geometry utilities (`ArtworkCropper`), and repository interfaces. Completely free of `android.*` dependencies for fast, deterministic JVM unit testing.
- **Data Layer**: Manages asynchronous file I/O on `Dispatchers.IO` using app-private storage (`context.filesDir/saved_artworks/` and `context.filesDir/drafts/`) with structured JSON serialization, emitting reactive updates through `StateFlow`.
- **Presentation Layer**: Built with Jetpack Compose and Material 3. Implements MVI/UDF where screens collect immutable state flows via `collectAsStateWithLifecycle()` and dispatch user actions to ViewModels.
- **Coordinate Transformation Pipeline**: Decouples touch screen pixels from canvas coordinates using `CanvasTransformState`, mapping gesture events through canonical world transformations (`screenToWorld` / `worldToScreen`) to support arbitrary pan, zoom, and orientation shifts.

---

## 🛠️ Tech Stack & Dependencies

- **Language**: Kotlin 2.2.10
- **UI Toolkit**: Jetpack Compose (BOM `2026.02.01`) + Material 3
- **Build System**: Gradle 9.3.2 with Android Gradle Plugin (AGP) 9.3.2 & Version Catalog (`gradle/libs.versions.toml`)
- **Target SDK**: Android 37 (Min SDK: 24)
- **Concurrency & Reactive Streams**: Kotlin Coroutines & Flow 1.10.1
- **Architecture Components**: AndroidX Lifecycle Runtime & ViewModel Compose 2.11.0
- **System Integration**: AndroidX Core SplashScreen 1.0.1
- **JSON Serialization**: Org.JSON 20240303
- **Testing**: JUnit 4, Kotlinx Coroutines Test, AndroidX Test JUnit

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Ladybug / Meerkat (or newer)
- **JDK**: Version 17 or higher (e.g. bundled JetBrains Runtime)
- **Android SDK**: API level 37 with Build Tools & Platform Tools

### Building & Running

1. **Clone the repository**:
   ```bash
   git clone https://github.com/pallav92/DrawOnMe.git
   cd DrawOnMe
   ```

2. **Run JVM unit tests**:
   ```bash
   ./gradlew test
   ```

3. **Assemble debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```
   The APK will be generated at `app/build/outputs/apk/debug/app-debug.apk`.

4. **Install and run on an Android device or emulator**:
   ```bash
   ./gradlew installDebug
   ```

---

## 🧪 Testing

DrawOnMe includes automated unit tests covering domain models, data persistence, gesture transformations, and UI state management:

| Test Suite | Scope |
| :--- | :--- |
| `FileArtworkRepositoryTest` | Tests JSON serialization, disk file write/read, StateFlow emissions, and artwork deletion |
| `FileBoardDraftRepositoryTest` | Tests isolated board draft persistence, auto-saving, and clearing across board IDs |
| `InMemoryStencilRepositoryTest` | Validates stencil templates, ID lookups, and verifies all normalized vector points lie within $[0.0, 1.0]$ |
| `ArtworkCropperTest` | Tests bounding box computation, aspect ratio preservation, edge padding, and empty stroke edge cases |
| `AppNavigationTest` | Tests navigation backstack state machine, route transitions, and pop behavior |
| `CanvasTransformStateTest` | Tests pan clamping, zoom limits, matrix conversions, coordinate roundtrips, and programmatic `setTransform` |
| `ScribbleViewModelTest` | Tests stroke lifecycle, Pen/Eraser modes, color palette, stroke widths, and undo/redo stacks |
| `StencilCoordinateAlignmentTest` | Validates canonical square coordinates across portrait/landscape rotations, touch point registration, and pan/zoom sync |

Run all tests with:
```bash
./gradlew test
```

---

## 📄 License

This project is licensed under the Apache License, Version 2.0 - see the [LICENSE](LICENSE) file for details.
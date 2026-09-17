# DrawOnMe 🎨

A delightful, safe, and intuitive Android drawing and tracing app designed specifically for budding young artists (ages 3–8) and their parents. Built 100% with modern **Jetpack Compose**, **Kotlin Coroutines**, and **Clean Architecture**.

---

## 🌟 Highlights & Key Features

- **🌈 "First Touch" Rainbow Splash Pad**:
  An interactive, tactile canvas directly on the onboarding screen where kids can touch and drag to paint glowing rainbow trails with multi-touch support before making any navigation choices.
- **🎨 Magic Doodle (Freehand Studio)**:
  A responsive blank canvas with low-latency touch pointer tracking and quadratic Bézier curve smoothing. Includes Pen and Eraser tools, 8 vibrant child-friendly colors, 4 stroke widths, and multi-level undo/redo.
- **✨ Cartoon & Geometric Stencil Studio**:
  8 large, recognizable geometric tracing templates that teach kids how to draw using basic shapes (triangles, rectangles, circles, ovals, and arcs):
  - **Vehicles & Things**: 🏠 Cozy House, 🚗 Zooming Car, 🚀 Space Rocket, ⛵ Happy Sailboat
  - **Animals & Friends**: ☀️ Smiling Sun, 🧸 Cute Teddy Bear, 🐱 Playful Kitty, 🐭 Playful Mouse
- **🪄 Magic Wand Reveal & Celebration**:
  Tapping the Magic Wand button auto-hides tracing guidelines to unveil the child's clean hand-drawn artwork, triggering an animated confetti particle explosion with a *"You Did It! 🌟"* trophy badge.
- **🖼️ "My Fridge" Virtual Art Gallery**:
  A refrigerator door where saved drawings are displayed on pinned paper sheets held up by colorful fridge magnets (`⭐️`, `🍓`, `🚀`, `🍀`, `🧁`, `🦕`, `🧸`, `🎨`). Includes vector stroke thumbnail rendering, fullscreen masterpiece inspection, and a parent-safe 2-step deletion flow.
- **📌 Pin to Fridge Action**:
  One-tap pinning to save creations from both the Free Doodle notepad and the Stencil Tracing Studio into local offline storage.
- **🛡️ 100% Offline & Kid-Safe**:
  Zero ads, zero third-party trackers, zero internet permissions requested, and zero data collection. All drawings and settings remain safely on the local device.

---

## 📐 Architecture & Engineering Standards

DrawOnMe strictly follows **Clean Architecture**, **Unidirectional Data Flow (UDF)**, and **Single Source of Truth (SSOT)** principles:

```
app/
 ├── domain/                   # Pure Kotlin business entities & repository contracts (Zero Android dependencies)
 │    ├── model/               # Stroke, Point, DrawingTool, Stencil, SavedArtwork
 │    └── repository/          # StencilRepository, ArtworkRepository
 ├── data/                     # Data implementations and offline storage
 │    └── repository/          # FileArtworkRepository (JSON persistence), InMemoryStencilRepository
 └── presentation/             # Declarative Jetpack Compose UI & State Management
      ├── fridge/              # FridgeGalleryScreen & Refrigerator Canvas UI
      ├── navigation/          # AppNavigation state machine & DrawOnMeApp entry
      ├── onboarding/          # OnboardingScreen & RainbowSplashPad
      ├── scribble/            # ScribbleScreen, Canvas, Toolbar, ScribbleViewModel
      └── stencil/             # StencilGalleryScreen, StencilDrawingScreen, CelebrationOverlay
```

### Layer Isolation
- **Domain Layer**: Contains immutable domain models and repository interfaces. Pure Kotlin with no platform dependencies (`android.*`), ensuring high reusability and fast JVM unit testing.
- **Data Layer**: Manages file I/O on `Dispatchers.IO` storing drawings in app-private storage (`context.filesDir/saved_artworks/`) via structured JSON serialization, emitting reactive updates through `StateFlow`.
- **Presentation Layer**: Built with Jetpack Compose and Material 3. Follows state hoisting patterns; screens observe immutable UI state flows with `collectAsStateWithLifecycle()` and send user actions back to ViewModels or navigation handlers.

---

## 🛠️ Tech Stack & Dependencies

- **Language**: Kotlin 2.2.10
- **UI Toolkit**: Jetpack Compose (BOM `2026.02.01`) + Material 3
- **Build System**: Gradle 9.3.2 with version catalog (`gradle/libs.versions.toml`)
- **Target SDK**: Android 37 (Min SDK: 24)
- **Concurrency & Reactive Streams**: Kotlin Coroutines & Flow 1.10.1
- **Architecture Components**: AndroidX Lifecycle Runtime & ViewModel Compose 2.11.0
- **Testing**: JUnit 4, Kotlinx Coroutines Test, Org.JSON

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Ladybug / Meerkat or newer
- **JDK**: Version 11 or higher (e.g. bundled JetBrains Runtime)
- **Android SDK**: API level 37 with platform tools

### Building & Running

1. **Clone the repository**:
   ```bash
   git clone https://github.com/pallav92/DrawOnMe.git
   cd DrawOnMe
   ```

2. **Run JVM unit tests**:
   ```bash
   ./gradlew testDebugUnitTest
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

DrawOnMe includes automated unit tests covering domain models, data persistence, and UI business logic:

| Test Suite | Scope |
| :--- | :--- |
| `FileArtworkRepositoryTest` | Tests JSON serialization, disk file write/read, StateFlow emission, and deletion |
| `InMemoryStencilRepositoryTest` | Validates stencil templates, ID lookups, and verifies that all normalized vector points lie within $[0.0, 1.0]$ |
| `ScribbleViewModelTest` | Tests stroke additions, Pen/Eraser switching, color selection, stroke width adjustments, undo/redo history stacks, and canvas clearing |

Run all tests with:
```bash
./gradlew test
```

---

## 📄 License

This project is licensed under the Apache License, Version 2.0 - see the [LICENSE](LICENSE) file for details.
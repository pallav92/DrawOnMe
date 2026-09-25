package com.draw.onme.data.repository

import com.draw.onme.domain.model.ColoringOutline
import com.draw.onme.domain.model.ColoringPage
import com.draw.onme.domain.model.ColoringRegion
import com.draw.onme.domain.model.Point
import com.draw.onme.domain.repository.ColoringBookRepository
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Curated in-memory coloring book library designed specifically for children aged 3–8.
 * Provides high-recognition vector shapes with closed fillable regions and bold line-art outlines.
 * All coordinates are normalized in the [0.0f..1.0f] range.
 */
class InMemoryColoringBookRepository : ColoringBookRepository {

    private val pages: List<ColoringPage> = listOf(
        // Creative Free Drawing
        createBlankCanvasPage(),

        // Category 1: Animals & Pets
        createHappyPuppyPage(),
        createPlayfulKittenPage(),
        createCheerfulDolphinPage(),
        createWiseOwlPage(),

        // Category 2: Vehicles & Adventures
        createZoomingRaceCarPage(),
        createBraveFireTruckPage(),
        createSpaceShuttlePage(),
        createDeepSubmarinePage(),

        // Category 3: Fairy Tale & Fantasy
        createFriendlyDragonPage(),
        createMagicCastlePage(),
        createFlutteringButterflyPage(),
        createFairyWandPage(),

        // Category 4: Sweet Food & Nature
        createSweetCupcakePage(),
        createIceCreamSundaePage(),
        createBloomingFlowerPage(),
        createRainbowSunPage()
    )

    override fun getColoringPages(): List<ColoringPage> = pages

    override fun getPageById(id: String): ColoringPage? = pages.firstOrNull { it.id == id }

    override fun getCategories(): List<String> = listOf(
        CATEGORY_CREATE,
        CATEGORY_ANIMALS,
        CATEGORY_VEHICLES,
        CATEGORY_FANTASY,
        CATEGORY_NATURE
    )

    override fun getPagesByCategory(category: String): List<ColoringPage> =
        pages.filter { it.category == category }

    companion object {
        const val BLANK_CANVAS_ID = "blank_canvas"
        const val CATEGORY_CREATE = "Draw & Create"
        const val CATEGORY_ANIMALS = "Animals & Pets"
        const val CATEGORY_VEHICLES = "Vehicles & Adventures"
        const val CATEGORY_FANTASY = "Fairy Tale & Fantasy"
        const val CATEGORY_NATURE = "Sweet Food & Nature"

        fun createBlankCanvasPage(): ColoringPage = ColoringPage(
            id = BLANK_CANVAS_ID,
            title = "Blank Canvas",
            category = CATEGORY_CREATE,
            iconEmoji = "🎨",
            difficulty = "Free Draw",
            regions = emptyList(),
            outlines = emptyList()
        )

        // Geometry Helpers
        private fun circlePoints(cx: Float, cy: Float, r: Float, steps: Int = 24): List<Point> {
            return (0 until steps).map { i ->
                val angle = 2.0 * PI * i / steps
                Point(
                    x = (cx + r * cos(angle)).toFloat().coerceIn(0f, 1f),
                    y = (cy + r * sin(angle)).toFloat().coerceIn(0f, 1f)
                )
            }
        }

        private fun ovalPoints(cx: Float, cy: Float, rx: Float, ry: Float, steps: Int = 24): List<Point> {
            return (0 until steps).map { i ->
                val angle = 2.0 * PI * i / steps
                Point(
                    x = (cx + rx * cos(angle)).toFloat().coerceIn(0f, 1f),
                    y = (cy + ry * sin(angle)).toFloat().coerceIn(0f, 1f)
                )
            }
        }

        private fun arcPoints(
            cx: Float,
            cy: Float,
            rx: Float,
            ry: Float,
            startDeg: Double,
            sweepDeg: Double,
            steps: Int = 16
        ): List<Point> {
            return (0..steps).map { i ->
                val angle = Math.toRadians(startDeg + sweepDeg * i / steps)
                Point(
                    x = (cx + rx * cos(angle)).toFloat().coerceIn(0f, 1f),
                    y = (cy + ry * sin(angle)).toFloat().coerceIn(0f, 1f)
                )
            }
        }

        // ==========================================
        // 1. HAPPY PUPPY 🐶
        // ==========================================
        private fun createHappyPuppyPage(): ColoringPage {
            val leftEar = listOf(Point(0.25f, 0.28f), Point(0.18f, 0.45f), Point(0.30f, 0.50f), Point(0.34f, 0.32f))
            val rightEar = listOf(Point(0.75f, 0.28f), Point(0.66f, 0.32f), Point(0.70f, 0.50f), Point(0.82f, 0.45f))
            val head = circlePoints(0.50f, 0.38f, 0.20f)
            val snout = ovalPoints(0.50f, 0.44f, 0.10f, 0.08f)
            val nose = listOf(Point(0.46f, 0.42f), Point(0.54f, 0.42f), Point(0.50f, 0.46f))
            val body = listOf(Point(0.35f, 0.55f), Point(0.65f, 0.55f), Point(0.72f, 0.85f), Point(0.28f, 0.85f))
            val collar = listOf(Point(0.35f, 0.54f), Point(0.65f, 0.54f), Point(0.64f, 0.60f), Point(0.36f, 0.60f))
            val tail = listOf(Point(0.70f, 0.70f), Point(0.86f, 0.62f), Point(0.82f, 0.74f), Point(0.68f, 0.78f))

            val regions = listOf(
                ColoringRegion("left_ear", "Left Ear", leftEar),
                ColoringRegion("right_ear", "Right Ear", rightEar),
                ColoringRegion("head", "Puppy Face", head),
                ColoringRegion("snout", "Snout", snout),
                ColoringRegion("nose", "Nose", nose),
                ColoringRegion("body", "Puppy Body", body),
                ColoringRegion("collar", "Collar", collar),
                ColoringRegion("tail", "Wagging Tail", tail)
            )

            val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) } +
                listOf(
                    ColoringOutline(circlePoints(0.42f, 0.34f, 0.025f), strokeWidth = 4f, isClosed = true),
                    ColoringOutline(circlePoints(0.58f, 0.34f, 0.025f), strokeWidth = 4f, isClosed = true),
                    ColoringOutline(arcPoints(0.50f, 0.46f, 0.04f, 0.04f, 0.0, 180.0), strokeWidth = 4f, isClosed = false)
                )

            return ColoringPage(
                id = "happy_puppy",
                title = "Happy Puppy",
                category = CATEGORY_ANIMALS,
                iconEmoji = "🐶",
                difficulty = "Easy",
                regions = regions,
                outlines = outlines
            )
        }

        // ==========================================
        // 2. PLAYFUL KITTEN 🐱
        // ==========================================
        private fun createPlayfulKittenPage(): ColoringPage {
            val leftEar = listOf(Point(0.28f, 0.28f), Point(0.22f, 0.14f), Point(0.40f, 0.20f))
            val rightEar = listOf(Point(0.72f, 0.28f), Point(0.60f, 0.20f), Point(0.78f, 0.14f))
            val head = circlePoints(0.50f, 0.38f, 0.22f)
            val body = ovalPoints(0.50f, 0.68f, 0.20f, 0.18f)
            val tummy = ovalPoints(0.50f, 0.68f, 0.11f, 0.12f)
            val tail = arcPoints(0.70f, 0.68f, 0.16f, 0.16f, 270.0, 120.0) +
                arcPoints(0.70f, 0.68f, 0.12f, 0.12f, 390.0, -120.0)

            val regions = listOf(
                ColoringRegion("left_ear", "Left Ear", leftEar),
                ColoringRegion("right_ear", "Right Ear", rightEar),
                ColoringRegion("head", "Kitty Head", head),
                ColoringRegion("body", "Kitty Body", body),
                ColoringRegion("tummy", "Tummy", tummy),
                ColoringRegion("tail", "Curled Tail", tail)
            )

            val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) } +
                listOf(
                    ColoringOutline(ovalPoints(0.40f, 0.35f, 0.03f, 0.04f), strokeWidth = 4f, isClosed = true),
                    ColoringOutline(ovalPoints(0.60f, 0.35f, 0.03f, 0.04f), strokeWidth = 4f, isClosed = true),
                    ColoringOutline(listOf(Point(0.48f, 0.42f), Point(0.52f, 0.42f), Point(0.50f, 0.45f)), strokeWidth = 4f, isClosed = true),
                    ColoringOutline(listOf(Point(0.18f, 0.40f), Point(0.34f, 0.42f)), strokeWidth = 3f, isClosed = false),
                    ColoringOutline(listOf(Point(0.82f, 0.40f), Point(0.66f, 0.42f)), strokeWidth = 3f, isClosed = false)
                )

            return ColoringPage(
                id = "playful_kitten",
                title = "Playful Kitten",
                category = CATEGORY_ANIMALS,
                iconEmoji = "🐱",
                difficulty = "Easy",
                regions = regions,
                outlines = outlines
            )
        }

        // ==========================================
        // 3. CHEERFUL DOLPHIN 🐬
        // ==========================================
        private fun createCheerfulDolphinPage(): ColoringPage {
            val body = listOf(
                Point(0.14f, 0.48f), Point(0.24f, 0.34f), Point(0.42f, 0.28f),
                Point(0.64f, 0.32f), Point(0.80f, 0.44f), Point(0.88f, 0.40f),
                Point(0.84f, 0.54f), Point(0.64f, 0.56f), Point(0.42f, 0.58f),
                Point(0.26f, 0.56f)
            )
            val dorsalFin = listOf(Point(0.44f, 0.28f), Point(0.52f, 0.16f), Point(0.56f, 0.30f))
            val flipper = listOf(Point(0.38f, 0.56f), Point(0.44f, 0.70f), Point(0.48f, 0.57f))
            val tailFlukes = listOf(Point(0.84f, 0.54f), Point(0.94f, 0.42f), Point(0.92f, 0.56f), Point(0.94f, 0.66f))
            val wave = listOf(
                Point(0.08f, 0.76f), Point(0.28f, 0.72f), Point(0.48f, 0.80f),
                Point(0.68f, 0.72f), Point(0.92f, 0.78f), Point(0.92f, 0.90f), Point(0.08f, 0.90f)
            )

            val regions = listOf(
                ColoringRegion("dolphin_body", "Dolphin Body", body),
                ColoringRegion("dorsal_fin", "Dorsal Fin", dorsalFin),
                ColoringRegion("flipper", "Flipper", flipper),
                ColoringRegion("tail_flukes", "Tail", tailFlukes),
                ColoringRegion("ocean_wave", "Ocean Wave", wave)
            )

            val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) } +
                listOf(
                    ColoringOutline(circlePoints(0.26f, 0.40f, 0.025f), strokeWidth = 4f, isClosed = true),
                    ColoringOutline(arcPoints(0.24f, 0.46f, 0.05f, 0.03f, 0.0, 150.0), strokeWidth = 4f, isClosed = false)
                )

            return ColoringPage(
                id = "cheerful_dolphin",
                title = "Cheerful Dolphin",
                category = CATEGORY_ANIMALS,
                iconEmoji = "🐬",
                difficulty = "Medium",
                regions = regions,
                outlines = outlines
            )
        }

        // ==========================================
        // 4. WISE OWL 🦉
        // ==========================================
        private fun createWiseOwlPage(): ColoringPage {
            val body = ovalPoints(0.50f, 0.52f, 0.24f, 0.28f)
            val belly = ovalPoints(0.50f, 0.58f, 0.15f, 0.18f)
            val leftWing = listOf(Point(0.26f, 0.42f), Point(0.16f, 0.58f), Point(0.28f, 0.70f), Point(0.32f, 0.52f))
            val rightWing = listOf(Point(0.74f, 0.42f), Point(0.68f, 0.52f), Point(0.72f, 0.70f), Point(0.84f, 0.58f))
            val branch = listOf(Point(0.12f, 0.80f), Point(0.88f, 0.80f), Point(0.88f, 0.86f), Point(0.12f, 0.86f))

            val regions = listOf(
                ColoringRegion("owl_body", "Owl Body", body),
                ColoringRegion("owl_belly", "Owl Belly", belly),
                ColoringRegion("left_wing", "Left Wing", leftWing),
                ColoringRegion("right_wing", "Right Wing", rightWing),
                ColoringRegion("tree_branch", "Tree Branch", branch)
            )

            val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) } +
                listOf(
                    ColoringOutline(circlePoints(0.40f, 0.38f, 0.07f), strokeWidth = 4f, isClosed = true),
                    ColoringOutline(circlePoints(0.60f, 0.38f, 0.07f), strokeWidth = 4f, isClosed = true),
                    ColoringOutline(circlePoints(0.40f, 0.38f, 0.03f), strokeWidth = 4f, isClosed = true),
                    ColoringOutline(circlePoints(0.60f, 0.38f, 0.03f), strokeWidth = 4f, isClosed = true),
                    ColoringOutline(listOf(Point(0.47f, 0.44f), Point(0.53f, 0.44f), Point(0.50f, 0.50f)), strokeWidth = 4f, isClosed = true)
                )

            return ColoringPage(
                id = "wise_owl",
                title = "Wise Owl",
                category = CATEGORY_ANIMALS,
                iconEmoji = "🦉",
                difficulty = "Medium",
                regions = regions,
                outlines = outlines
            )
        }

        // ==========================================
        // 5. ZOOMING RACE CAR 🚗
        // ==========================================
        private fun createZoomingRaceCarPage(): ColoringPage {
            val cabin = listOf(Point(0.30f, 0.50f), Point(0.42f, 0.30f), Point(0.66f, 0.30f), Point(0.76f, 0.50f))
            val body = listOf(
                Point(0.10f, 0.50f), Point(0.10f, 0.68f), Point(0.20f, 0.68f),
                Point(0.36f, 0.68f), Point(0.64f, 0.68f), Point(0.80f, 0.68f),
                Point(0.92f, 0.68f), Point(0.92f, 0.50f), Point(0.76f, 0.50f),
                Point(0.30f, 0.50f)
            )
            val frontWheel = circlePoints(0.28f, 0.68f, 0.11f)
            val rearWheel = circlePoints(0.72f, 0.68f, 0.11f)
            val frontRim = circlePoints(0.28f, 0.68f, 0.05f)
            val rearRim = circlePoints(0.72f, 0.68f, 0.05f)
            val spoiler = listOf(Point(0.08f, 0.42f), Point(0.20f, 0.42f), Point(0.18f, 0.50f), Point(0.10f, 0.50f))

            val regions = listOf(
                ColoringRegion("cabin", "Roof & Cabin", cabin),
                ColoringRegion("car_body", "Car Body", body),
                ColoringRegion("front_wheel", "Front Tire", frontWheel),
                ColoringRegion("rear_wheel", "Rear Tire", rearWheel),
                ColoringRegion("front_rim", "Front Rim", frontRim),
                ColoringRegion("rear_rim", "Rear Rim", rearRim),
                ColoringRegion("spoiler", "Spoiler", spoiler)
            )

            val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) } +
                listOf(
                    ColoringOutline(listOf(Point(0.36f, 0.48f), Point(0.44f, 0.34f), Point(0.50f, 0.34f), Point(0.50f, 0.48f)), strokeWidth = 4f, isClosed = true),
                    ColoringOutline(listOf(Point(0.54f, 0.34f), Point(0.62f, 0.34f), Point(0.70f, 0.48f), Point(0.54f, 0.48f)), strokeWidth = 4f, isClosed = true)
                )

            return ColoringPage(
                id = "zooming_race_car",
                title = "Zooming Race Car",
                category = CATEGORY_VEHICLES,
                iconEmoji = "🏎️",
                difficulty = "Easy",
                regions = regions,
                outlines = outlines
            )
        }

        // ==========================================
        // 6. BRAVE FIRE TRUCK 🚒
        // ==========================================
        private fun createBraveFireTruckPage(): ColoringPage {
            val cab = listOf(Point(0.64f, 0.38f), Point(0.88f, 0.38f), Point(0.88f, 0.70f), Point(0.64f, 0.70f))
            val tank = listOf(Point(0.14f, 0.44f), Point(0.64f, 0.44f), Point(0.64f, 0.70f), Point(0.14f, 0.70f))
            val ladder = listOf(Point(0.16f, 0.36f), Point(0.60f, 0.36f), Point(0.60f, 0.42f), Point(0.16f, 0.42f))
            val wheel1 = circlePoints(0.28f, 0.70f, 0.10f)
            val wheel2 = circlePoints(0.76f, 0.70f, 0.10f)
            val siren = listOf(Point(0.72f, 0.32f), Point(0.80f, 0.32f), Point(0.80f, 0.38f), Point(0.72f, 0.38f))

            val regions = listOf(
                ColoringRegion("cab", "Driver Cab", cab),
                ColoringRegion("tank", "Water Tank", tank),
                ColoringRegion("ladder", "Rescue Ladder", ladder),
                ColoringRegion("wheel1", "Back Wheel", wheel1),
                ColoringRegion("wheel2", "Front Wheel", wheel2),
                ColoringRegion("siren", "Siren Light", siren)
            )

            val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) } +
                listOf(
                    ColoringOutline(listOf(Point(0.72f, 0.42f), Point(0.84f, 0.42f), Point(0.84f, 0.54f), Point(0.72f, 0.54f)), strokeWidth = 4f, isClosed = true)
                )

            return ColoringPage(
                id = "brave_fire_truck",
                title = "Brave Fire Truck",
                category = CATEGORY_VEHICLES,
                iconEmoji = "🚒",
                difficulty = "Medium",
                regions = regions,
                outlines = outlines
            )
        }

        // ==========================================
        // 7. SPACE SHUTTLE 🚀
        // ==========================================
        private fun createSpaceShuttlePage(): ColoringPage {
            val nose = listOf(Point(0.36f, 0.30f), Point(0.50f, 0.10f), Point(0.64f, 0.30f))
            val body = listOf(Point(0.36f, 0.30f), Point(0.36f, 0.68f), Point(0.64f, 0.68f), Point(0.64f, 0.30f))
            val leftFin = listOf(Point(0.36f, 0.52f), Point(0.18f, 0.72f), Point(0.36f, 0.68f))
            val rightFin = listOf(Point(0.64f, 0.52f), Point(0.82f, 0.72f), Point(0.64f, 0.68f))
            val window = circlePoints(0.50f, 0.44f, 0.08f)
            val fire = listOf(Point(0.40f, 0.68f), Point(0.44f, 0.84f), Point(0.50f, 0.94f), Point(0.56f, 0.84f), Point(0.60f, 0.68f))

            val regions = listOf(
                ColoringRegion("nose", "Nose Cone", nose),
                ColoringRegion("body", "Rocket Body", body),
                ColoringRegion("left_fin", "Left Fin", leftFin),
                ColoringRegion("right_fin", "Right Fin", rightFin),
                ColoringRegion("window", "Porthole Window", window),
                ColoringRegion("fire", "Booster Flames", fire)
            )

            val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) }

            return ColoringPage(
                id = "space_shuttle",
                title = "Space Shuttle",
                category = CATEGORY_VEHICLES,
                iconEmoji = "🚀",
                difficulty = "Easy",
                regions = regions,
                outlines = outlines
            )
        }

        // ==========================================
        // 8. DEEP SUBMARINE 🚢
        // ==========================================
        private fun createDeepSubmarinePage(): ColoringPage {
            val hull = ovalPoints(0.50f, 0.54f, 0.34f, 0.18f)
            val periscope = listOf(Point(0.46f, 0.36f), Point(0.46f, 0.22f), Point(0.56f, 0.22f), Point(0.56f, 0.28f), Point(0.52f, 0.28f), Point(0.52f, 0.36f))
            val propeller = listOf(Point(0.14f, 0.46f), Point(0.10f, 0.54f), Point(0.14f, 0.62f))
            val window1 = circlePoints(0.38f, 0.54f, 0.05f)
            val window2 = circlePoints(0.52f, 0.54f, 0.05f)
            val window3 = circlePoints(0.66f, 0.54f, 0.05f)

            val regions = listOf(
                ColoringRegion("hull", "Submarine Hull", hull),
                ColoringRegion("periscope", "Periscope", periscope),
                ColoringRegion("propeller", "Propeller", propeller),
                ColoringRegion("window1", "Window 1", window1),
                ColoringRegion("window2", "Window 2", window2),
                ColoringRegion("window3", "Window 3", window3)
            )

            val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) }

            return ColoringPage(
                id = "deep_submarine",
                title = "Deep Submarine",
                category = CATEGORY_VEHICLES,
                iconEmoji = "🫧",
                difficulty = "Easy",
                regions = regions,
                outlines = outlines
            )
        }

        // ==========================================
        // 9. FRIENDLY DRAGON 🐉
        // ==========================================
        private fun createFriendlyDragonPage(): ColoringPage {
            val head = circlePoints(0.40f, 0.34f, 0.16f)
            val snout = ovalPoints(0.30f, 0.38f, 0.10f, 0.08f)
            val body = ovalPoints(0.52f, 0.62f, 0.22f, 0.18f)
            val belly = ovalPoints(0.46f, 0.64f, 0.12f, 0.12f)
            val wing = listOf(Point(0.56f, 0.50f), Point(0.78f, 0.34f), Point(0.74f, 0.52f), Point(0.82f, 0.58f), Point(0.60f, 0.62f))
            val horn = listOf(Point(0.46f, 0.22f), Point(0.54f, 0.12f), Point(0.52f, 0.25f))

            val regions = listOf(
                ColoringRegion("dragon_head", "Dragon Head", head),
                ColoringRegion("dragon_snout", "Snout", snout),
                ColoringRegion("dragon_body", "Dragon Body", body),
                ColoringRegion("dragon_belly", "Belly Scales", belly),
                ColoringRegion("dragon_wing", "Magic Wing", wing),
                ColoringRegion("dragon_horn", "Horn", horn)
            )

            val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) } +
                listOf(
                    ColoringOutline(circlePoints(0.38f, 0.30f, 0.025f), strokeWidth = 4f, isClosed = true),
                    ColoringOutline(arcPoints(0.30f, 0.40f, 0.04f, 0.02f, 0.0, 180.0), strokeWidth = 3f, isClosed = false)
                )

            return ColoringPage(
                id = "friendly_dragon",
                title = "Friendly Dragon",
                category = CATEGORY_FANTASY,
                iconEmoji = "🐲",
                difficulty = "Medium",
                regions = regions,
                outlines = outlines
            )
        }

        // ==========================================
        // 10. MAGIC CASTLE 🏰
        // ==========================================
        private fun createMagicCastlePage(): ColoringPage {
            val mainKeep = listOf(Point(0.34f, 0.48f), Point(0.66f, 0.48f), Point(0.66f, 0.86f), Point(0.34f, 0.86f))
            val leftTower = listOf(Point(0.18f, 0.38f), Point(0.34f, 0.38f), Point(0.34f, 0.86f), Point(0.18f, 0.86f))
            val rightTower = listOf(Point(0.66f, 0.38f), Point(0.82f, 0.38f), Point(0.82f, 0.86f), Point(0.66f, 0.86f))
            val leftRoof = listOf(Point(0.16f, 0.38f), Point(0.26f, 0.18f), Point(0.36f, 0.38f))
            val rightRoof = listOf(Point(0.64f, 0.38f), Point(0.74f, 0.18f), Point(0.84f, 0.38f))
            val gate = listOf(Point(0.44f, 0.86f), Point(0.44f, 0.66f), Point(0.56f, 0.66f), Point(0.56f, 0.86f))

            val regions = listOf(
                ColoringRegion("main_keep", "Main Castle", mainKeep),
                ColoringRegion("left_tower", "Left Tower", leftTower),
                ColoringRegion("right_tower", "Right Tower", rightTower),
                ColoringRegion("left_roof", "Left Roof Cone", leftRoof),
                ColoringRegion("right_roof", "Right Roof Cone", rightRoof),
                ColoringRegion("gate", "Castle Gate", gate)
            )

            val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) } +
                listOf(
                    ColoringOutline(listOf(Point(0.46f, 0.52f), Point(0.54f, 0.52f), Point(0.54f, 0.60f), Point(0.46f, 0.60f)), strokeWidth = 4f, isClosed = true)
                )

            return ColoringPage(
                id = "magic_castle",
                title = "Magic Castle",
                category = CATEGORY_FANTASY,
                iconEmoji = "🏰",
                difficulty = "Medium",
                regions = regions,
                outlines = outlines
            )
        }

        // ==========================================
        // 11. FLUTTERING BUTTERFLY 🦋
        // ==========================================
        private fun createFlutteringButterflyPage(): ColoringPage {
            val body = ovalPoints(0.50f, 0.50f, 0.05f, 0.26f)
            val upperLeft = listOf(Point(0.48f, 0.40f), Point(0.20f, 0.18f), Point(0.12f, 0.38f), Point(0.46f, 0.50f))
            val upperRight = listOf(Point(0.52f, 0.40f), Point(0.80f, 0.18f), Point(0.88f, 0.38f), Point(0.54f, 0.50f))
            val lowerLeft = listOf(Point(0.47f, 0.54f), Point(0.18f, 0.56f), Point(0.24f, 0.78f), Point(0.48f, 0.66f))
            val lowerRight = listOf(Point(0.53f, 0.54f), Point(0.82f, 0.56f), Point(0.76f, 0.78f), Point(0.52f, 0.66f))

            val regions = listOf(
                ColoringRegion("butterfly_body", "Butterfly Body", body),
                ColoringRegion("upper_left_wing", "Top Left Wing", upperLeft),
                ColoringRegion("upper_right_wing", "Top Right Wing", upperRight),
                ColoringRegion("lower_left_wing", "Bottom Left Wing", lowerLeft),
                ColoringRegion("lower_right_wing", "Bottom Right Wing", lowerRight)
            )

            val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) } +
                listOf(
                    ColoringOutline(arcPoints(0.46f, 0.24f, 0.08f, 0.08f, 90.0, 120.0), strokeWidth = 4f, isClosed = false),
                    ColoringOutline(arcPoints(0.54f, 0.24f, 0.08f, 0.08f, 90.0, -120.0), strokeWidth = 4f, isClosed = false)
                )

            return ColoringPage(
                id = "fluttering_butterfly",
                title = "Fluttering Butterfly",
                category = CATEGORY_FANTASY,
                iconEmoji = "🦋",
                difficulty = "Easy",
                regions = regions,
                outlines = outlines
            )
        }

        // ==========================================
        // 12. FAIRY WAND ✨
        // ==========================================
        private fun createFairyWandPage(): ColoringPage {
            // 5-pointed star
            val starPoints = mutableListOf<Point>()
            for (i in 0 until 10) {
                val r = if (i % 2 == 0) 0.20f else 0.09f
                val angle = -PI / 2.0 + i * PI / 5.0
                starPoints.add(Point((0.50f + r * cos(angle)).toFloat(), (0.35f + r * sin(angle)).toFloat()))
            }
            val handle = listOf(Point(0.47f, 0.50f), Point(0.53f, 0.50f), Point(0.53f, 0.88f), Point(0.47f, 0.88f))
            val ribbon1 = listOf(Point(0.53f, 0.52f), Point(0.66f, 0.62f), Point(0.58f, 0.72f), Point(0.53f, 0.58f))

            val regions = listOf(
                ColoringRegion("wand_star", "Magic Star", starPoints),
                ColoringRegion("wand_handle", "Wand Handle", handle),
                ColoringRegion("ribbon", "Sparkle Ribbon", ribbon1)
            )

            val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) }

            return ColoringPage(
                id = "fairy_wand",
                title = "Fairy Star Wand",
                category = CATEGORY_FANTASY,
                iconEmoji = "✨",
                difficulty = "Easy",
                regions = regions,
                outlines = outlines
            )
        }

        // ==========================================
        // 13. SWEET CUPCAKE 🧁
        // ==========================================
        private fun createSweetCupcakePage(): ColoringPage {
            val cherry = circlePoints(0.50f, 0.22f, 0.07f)
            val frostingTop = listOf(Point(0.34f, 0.38f), Point(0.50f, 0.28f), Point(0.66f, 0.38f), Point(0.50f, 0.44f))
            val frostingBase = listOf(
                Point(0.20f, 0.54f), Point(0.32f, 0.44f), Point(0.50f, 0.42f),
                Point(0.68f, 0.44f), Point(0.80f, 0.54f), Point(0.50f, 0.60f)
            )
            val wrapper = listOf(Point(0.25f, 0.58f), Point(0.75f, 0.58f), Point(0.68f, 0.88f), Point(0.32f, 0.88f))

            val regions = listOf(
                ColoringRegion("cherry", "Cherry on Top", cherry),
                ColoringRegion("frosting_top", "Swirly Frosting", frostingTop),
                ColoringRegion("frosting_base", "Cream Frosting", frostingBase),
                ColoringRegion("cupcake_wrapper", "Cupcake Wrapper", wrapper)
            )

            val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) }

            return ColoringPage(
                id = "sweet_cupcake",
                title = "Sweet Cupcake",
                category = CATEGORY_NATURE,
                iconEmoji = "🧁",
                difficulty = "Easy",
                regions = regions,
                outlines = outlines
            )
        }

        // ==========================================
        // 14. ICE CREAM SUNDAE 🍨
        // ==========================================
        private fun createIceCreamSundaePage(): ColoringPage {
            val scoopTop = circlePoints(0.50f, 0.34f, 0.16f)
            val scoopBottom = circlePoints(0.50f, 0.48f, 0.18f)
            val cone = listOf(Point(0.32f, 0.52f), Point(0.68f, 0.52f), Point(0.50f, 0.90f))
            val cherry = circlePoints(0.50f, 0.16f, 0.05f)

            val regions = listOf(
                ColoringRegion("cherry", "Sundae Cherry", cherry),
                ColoringRegion("scoop_top", "Top Ice Cream Scoop", scoopTop),
                ColoringRegion("scoop_bottom", "Bottom Ice Cream Scoop", scoopBottom),
                ColoringRegion("waffle_cone", "Crispy Cone", cone)
            )

            val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) } +
                listOf(
                    ColoringOutline(listOf(Point(0.38f, 0.60f), Point(0.56f, 0.78f)), strokeWidth = 3f, isClosed = false),
                    ColoringOutline(listOf(Point(0.62f, 0.60f), Point(0.44f, 0.78f)), strokeWidth = 3f, isClosed = false)
                )

            return ColoringPage(
                id = "ice_cream_sundae",
                title = "Ice Cream Cone",
                category = CATEGORY_NATURE,
                iconEmoji = "🍦",
                difficulty = "Easy",
                regions = regions,
                outlines = outlines
            )
        }

        // ==========================================
        // 15. BLOOMING FLOWER 🌸
        // ==========================================
        private fun createBloomingFlowerPage(): ColoringPage {
            val center = circlePoints(0.50f, 0.38f, 0.10f)
            val petalTop = ovalPoints(0.50f, 0.20f, 0.08f, 0.10f)
            val petalBottom = ovalPoints(0.50f, 0.56f, 0.08f, 0.10f)
            val petalLeft = ovalPoints(0.32f, 0.38f, 0.10f, 0.08f)
            val petalRight = ovalPoints(0.68f, 0.38f, 0.10f, 0.08f)
            val stem = listOf(Point(0.48f, 0.64f), Point(0.52f, 0.64f), Point(0.52f, 0.88f), Point(0.48f, 0.88f))
            val leafLeft = listOf(Point(0.48f, 0.72f), Point(0.34f, 0.68f), Point(0.48f, 0.80f))
            val leafRight = listOf(Point(0.52f, 0.74f), Point(0.66f, 0.70f), Point(0.52f, 0.82f))

            val regions = listOf(
                ColoringRegion("flower_center", "Flower Center", center),
                ColoringRegion("petal_top", "Top Petal", petalTop),
                ColoringRegion("petal_bottom", "Bottom Petal", petalBottom),
                ColoringRegion("petal_left", "Left Petal", petalLeft),
                ColoringRegion("petal_right", "Right Petal", petalRight),
                ColoringRegion("stem", "Green Stem", stem),
                ColoringRegion("leaf_left", "Left Leaf", leafLeft),
                ColoringRegion("leaf_right", "Right Leaf", leafRight)
            )

            val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) }

            return ColoringPage(
                id = "blooming_flower",
                title = "Blooming Flower",
                category = CATEGORY_NATURE,
                iconEmoji = "🌸",
                difficulty = "Easy",
                regions = regions,
                outlines = outlines
            )
        }

        // ==========================================
        // 16. RAINBOW SUN 🌈
        // ==========================================
        private fun createRainbowSunPage(): ColoringPage {
            val sun = circlePoints(0.24f, 0.30f, 0.14f)
            val rainbowOuter = listOf(
                Point(0.15f, 0.78f), Point(0.15f, 0.70f),
                Point(0.50f, 0.40f), Point(0.85f, 0.70f), Point(0.85f, 0.78f),
                Point(0.50f, 0.48f)
            )
            val rainbowMid = listOf(
                Point(0.22f, 0.78f), Point(0.22f, 0.72f),
                Point(0.50f, 0.49f), Point(0.78f, 0.72f), Point(0.78f, 0.78f),
                Point(0.50f, 0.56f)
            )
            val rainbowInner = listOf(
                Point(0.28f, 0.78f), Point(0.28f, 0.74f),
                Point(0.50f, 0.57f), Point(0.72f, 0.74f), Point(0.72f, 0.78f),
                Point(0.50f, 0.64f)
            )
            val cloudLeft = ovalPoints(0.22f, 0.80f, 0.14f, 0.08f)
            val cloudRight = ovalPoints(0.78f, 0.80f, 0.14f, 0.08f)

            val regions = listOf(
                ColoringRegion("sun", "Smiling Sun", sun),
                ColoringRegion("rainbow_outer", "Red Rainbow Arch", rainbowOuter),
                ColoringRegion("rainbow_mid", "Yellow Rainbow Arch", rainbowMid),
                ColoringRegion("rainbow_inner", "Blue Rainbow Arch", rainbowInner),
                ColoringRegion("cloud_left", "Fluffy Cloud Left", cloudLeft),
                ColoringRegion("cloud_right", "Fluffy Cloud Right", cloudRight)
            )

            val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) }

            return ColoringPage(
                id = "rainbow_sun",
                title = "Rainbow Sun",
                category = CATEGORY_NATURE,
                iconEmoji = "🌈",
                difficulty = "Easy",
                regions = regions,
                outlines = outlines
            )
        }
    }
}

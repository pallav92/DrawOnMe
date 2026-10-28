package com.draw.onme.data.repository

import com.draw.onme.data.repository.catalog.DinoColoringPages
import com.draw.onme.data.repository.catalog.PaintByNumberPages
import com.draw.onme.data.repository.catalog.VehicleColoringPages
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
 *
 * Geometric figures like vehicles maintain rich detail (>10 parts), while curved figures like
 * animals, fantasy, and flowers are kept minimal and clean to avoid visual clutter.
 * All coordinates are normalized in the [0.0f..1.0f] range.
 */
class InMemoryColoringBookRepository : ColoringBookRepository {

    private val pages: List<ColoringPage> = listOf(
        // Creative Free Drawing
        createBlankCanvasPage(),

        // Category 1: Animals & Pets (Minimal, clean, child-friendly)
        createHappyPuppyPage(),
        createPlayfulKittenPage(),
        createCheerfulDolphinPage(),
        createWiseOwlPage()
    ) + VehicleColoringPages + listOf(
        // Category 3: Fairy Tale & Fantasy (Original minimal shapes)
        createFriendlyDragonPage(),
        createMagicCastlePage(),
        createFlutteringButterflyPage(),
        createFairyWandPage(),

        // Category 4: Flowers & Nature (Original minimal nature shapes)
        createBloomingFlowerPage(),
        createRainbowSunPage()
    ) + DinoColoringPages + PaintByNumberPages

    override fun getColoringPages(): List<ColoringPage> = pages

    override fun getPageById(id: String): ColoringPage? = pages.firstOrNull { it.id == id }

    override fun getCategories(): List<String> = listOf(
        CATEGORY_CREATE,
        CATEGORY_ANIMALS,
        CATEGORY_VEHICLES,
        CATEGORY_FANTASY,
        CATEGORY_NATURE,
        CATEGORY_DINOS,
        CATEGORY_PAINT_BY_NUMBER
    )

    override fun getPagesByCategory(category: String): List<ColoringPage> =
        pages.filter { it.category == category }

    companion object {
        const val BLANK_CANVAS_ID = "blank_canvas"
        const val CATEGORY_CREATE = "Draw & Create"
        const val CATEGORY_ANIMALS = "Animals & Pets"
        const val CATEGORY_VEHICLES = "Vehicles & Adventures"
        const val CATEGORY_FANTASY = "Fairy Tale & Fantasy"
        const val CATEGORY_NATURE = "Flowers & Nature"
        const val CATEGORY_DINOS = "Dinosaurs & Prehistoric"
        const val CATEGORY_PAINT_BY_NUMBER = "Paint by Number"

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

        /**
         * Scales a boundary toward its own centroid, producing a smaller nested copy of the same
         * shape - used to split a single region into distinct concentric rings (e.g. an outer
         * body color and an inner belly/patch color) so each ring gets its own number.
         */
        private fun scalePoints(points: List<Point>, scale: Float): List<Point> {
            if (points.isEmpty()) return points
            val cx = points.sumOf { it.x.toDouble() }.toFloat() / points.size
            val cy = points.sumOf { it.y.toDouble() }.toFloat() / points.size
            return points.map { p ->
                Point(
                    x = (cx + (p.x - cx) * scale).coerceIn(0f, 1f),
                    y = (cy + (p.y - cy) * scale).coerceIn(0f, 1f)
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
            val chestPatch = scalePoints(body, 0.55f)
            val facePatch = circlePoints(0.50f, 0.38f, 0.09f)

            val regions = listOf(
                ColoringRegion("left_ear", "Left Ear", leftEar),
                ColoringRegion("right_ear", "Right Ear", rightEar),
                ColoringRegion("head", "Puppy Face", head),
                ColoringRegion("face_patch", "Face Patch", facePatch),
                ColoringRegion("snout", "Snout", snout),
                ColoringRegion("nose", "Nose", nose),
                ColoringRegion("body", "Puppy Body", body),
                ColoringRegion("chest_patch", "Chest Patch", chestPatch),
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
            val bodyMid = ovalPoints(0.50f, 0.68f, 0.155f, 0.14f)
            val tummy = ovalPoints(0.50f, 0.68f, 0.11f, 0.12f)
            val tail = arcPoints(0.70f, 0.68f, 0.16f, 0.16f, 270.0, 120.0) +
                arcPoints(0.70f, 0.68f, 0.12f, 0.12f, 390.0, -120.0)
            val noseTip = listOf(Point(0.48f, 0.42f), Point(0.52f, 0.42f), Point(0.50f, 0.45f))
            val tailTip = circlePoints(0.82f, 0.56f, 0.035f)
            val earInnerLeft = scalePoints(leftEar, 0.55f)
            val earInnerRight = scalePoints(rightEar, 0.55f)

            val regions = listOf(
                ColoringRegion("left_ear", "Left Ear", leftEar),
                ColoringRegion("right_ear", "Right Ear", rightEar),
                ColoringRegion("ear_inner_left", "Left Inner Ear", earInnerLeft),
                ColoringRegion("ear_inner_right", "Right Inner Ear", earInnerRight),
                ColoringRegion("head", "Kitty Head", head),
                ColoringRegion("nose_tip", "Nose", noseTip),
                ColoringRegion("body", "Kitty Body", body),
                ColoringRegion("body_mid", "Kitty Body Ring", bodyMid),
                ColoringRegion("tummy", "Tummy", tummy),
                ColoringRegion("tail", "Curled Tail", tail),
                ColoringRegion("tail_tip", "Tail Tip", tailTip)
            )

            val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) } +
                listOf(
                    ColoringOutline(ovalPoints(0.40f, 0.35f, 0.03f, 0.04f), strokeWidth = 4f, isClosed = true),
                    ColoringOutline(ovalPoints(0.60f, 0.35f, 0.03f, 0.04f), strokeWidth = 4f, isClosed = true),
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
            val belly = scalePoints(body, 0.5f)
            val eye = circlePoints(0.26f, 0.40f, 0.025f)
            val splashLeft = ovalPoints(0.14f, 0.70f, 0.05f, 0.03f)
            val splashRight = ovalPoints(0.86f, 0.70f, 0.05f, 0.03f)

            val regions = listOf(
                ColoringRegion("dolphin_body", "Dolphin Body", body),
                ColoringRegion("dolphin_belly", "Dolphin Belly", belly),
                ColoringRegion("dorsal_fin", "Dorsal Fin", dorsalFin),
                ColoringRegion("flipper", "Flipper", flipper),
                ColoringRegion("tail_flukes", "Tail", tailFlukes),
                ColoringRegion("dolphin_eye", "Eye", eye),
                ColoringRegion("ocean_wave", "Ocean Wave", wave),
                ColoringRegion("splash_a", "Splash", splashLeft),
                ColoringRegion("splash_b", "Splash", splashRight)
            )

            val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) } +
                listOf(
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
            val bellyInner = scalePoints(belly, 0.6f)
            val eyeLeft = circlePoints(0.40f, 0.38f, 0.07f)
            val eyeRight = circlePoints(0.60f, 0.38f, 0.07f)
            val beak = listOf(Point(0.47f, 0.44f), Point(0.53f, 0.44f), Point(0.50f, 0.50f))

            val regions = listOf(
                ColoringRegion("owl_body", "Owl Body", body),
                ColoringRegion("owl_belly", "Owl Belly", belly),
                ColoringRegion("owl_belly_inner", "Owl Belly Pattern", bellyInner),
                ColoringRegion("left_wing", "Left Wing", leftWing),
                ColoringRegion("right_wing", "Right Wing", rightWing),
                ColoringRegion("tree_branch", "Tree Branch", branch),
                ColoringRegion("eye_left", "Left Eye", eyeLeft),
                ColoringRegion("eye_right", "Right Eye", eyeRight),
                ColoringRegion("owl_beak", "Beak", beak)
            )

            val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) } +
                listOf(
                    ColoringOutline(circlePoints(0.40f, 0.38f, 0.03f), strokeWidth = 4f, isClosed = true),
                    ColoringOutline(circlePoints(0.60f, 0.38f, 0.03f), strokeWidth = 4f, isClosed = true)
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
        // 5. FRIENDLY DRAGON 🐲
        // ==========================================
        private fun createFriendlyDragonPage(): ColoringPage {
            val head = circlePoints(0.40f, 0.34f, 0.16f)
            val snout = ovalPoints(0.30f, 0.38f, 0.10f, 0.08f)
            val body = ovalPoints(0.52f, 0.62f, 0.22f, 0.18f)
            val belly = ovalPoints(0.46f, 0.64f, 0.12f, 0.12f)
            val wing = listOf(Point(0.56f, 0.50f), Point(0.78f, 0.34f), Point(0.74f, 0.52f), Point(0.82f, 0.58f), Point(0.60f, 0.62f))
            val horn = listOf(Point(0.46f, 0.22f), Point(0.54f, 0.12f), Point(0.52f, 0.25f))
            val wingInner = scalePoints(wing, 0.55f)
            val eye = circlePoints(0.38f, 0.30f, 0.025f)
            val cheek = circlePoints(0.34f, 0.36f, 0.03f)

            val regions = listOf(
                ColoringRegion("dragon_head", "Dragon Head", head),
                ColoringRegion("dragon_snout", "Snout", snout),
                ColoringRegion("dragon_body", "Dragon Body", body),
                ColoringRegion("dragon_belly", "Belly Scales", belly),
                ColoringRegion("dragon_wing", "Magic Wing", wing),
                ColoringRegion("dragon_wing_inner", "Wing Pattern", wingInner),
                ColoringRegion("dragon_horn", "Horn", horn),
                ColoringRegion("dragon_eye", "Eye", eye),
                ColoringRegion("dragon_cheek", "Cheek Blush", cheek)
            )

            val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) } +
                listOf(
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
        // 6. MAGIC CASTLE 🏰
        // ==========================================
        private fun createMagicCastlePage(): ColoringPage {
            val mainKeep = listOf(Point(0.34f, 0.48f), Point(0.66f, 0.48f), Point(0.66f, 0.86f), Point(0.34f, 0.86f))
            val leftTower = listOf(Point(0.18f, 0.38f), Point(0.34f, 0.38f), Point(0.34f, 0.86f), Point(0.18f, 0.86f))
            val rightTower = listOf(Point(0.66f, 0.38f), Point(0.82f, 0.38f), Point(0.82f, 0.86f), Point(0.66f, 0.86f))
            val leftRoof = listOf(Point(0.16f, 0.38f), Point(0.26f, 0.18f), Point(0.36f, 0.38f))
            val rightRoof = listOf(Point(0.64f, 0.38f), Point(0.74f, 0.18f), Point(0.84f, 0.38f))
            val gate = listOf(Point(0.44f, 0.86f), Point(0.44f, 0.66f), Point(0.56f, 0.66f), Point(0.56f, 0.86f))
            val window = listOf(Point(0.46f, 0.52f), Point(0.54f, 0.52f), Point(0.54f, 0.60f), Point(0.46f, 0.60f))
            val flagLeft = listOf(Point(0.26f, 0.18f), Point(0.26f, 0.10f), Point(0.32f, 0.14f))
            val flagRight = listOf(Point(0.74f, 0.18f), Point(0.74f, 0.10f), Point(0.80f, 0.14f))
            val keepBanner = scalePoints(mainKeep, 0.45f)

            val regions = listOf(
                ColoringRegion("main_keep", "Main Castle", mainKeep),
                ColoringRegion("keep_banner", "Castle Banner", keepBanner),
                ColoringRegion("left_tower", "Left Tower", leftTower),
                ColoringRegion("right_tower", "Right Tower", rightTower),
                ColoringRegion("left_roof", "Left Roof Cone", leftRoof),
                ColoringRegion("right_roof", "Right Roof Cone", rightRoof),
                ColoringRegion("flag_left", "Left Flag", flagLeft),
                ColoringRegion("flag_right", "Right Flag", flagRight),
                ColoringRegion("gate", "Castle Gate", gate),
                ColoringRegion("window", "Castle Window", window)
            )

            val outlines = regions.map { ColoringOutline(it.boundaryPoints, strokeWidth = 5f, isClosed = true) }

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
        // 7. FLUTTERING BUTTERFLY 🦋
        // ==========================================
        private fun createFlutteringButterflyPage(): ColoringPage {
            val body = ovalPoints(0.50f, 0.50f, 0.05f, 0.26f)
            val upperLeft = listOf(Point(0.48f, 0.40f), Point(0.20f, 0.18f), Point(0.12f, 0.38f), Point(0.46f, 0.50f))
            val upperRight = listOf(Point(0.52f, 0.40f), Point(0.80f, 0.18f), Point(0.88f, 0.38f), Point(0.54f, 0.50f))
            val lowerLeft = listOf(Point(0.47f, 0.54f), Point(0.18f, 0.56f), Point(0.24f, 0.78f), Point(0.48f, 0.66f))
            val lowerRight = listOf(Point(0.53f, 0.54f), Point(0.82f, 0.56f), Point(0.76f, 0.78f), Point(0.52f, 0.66f))
            val upperLeftInner = scalePoints(upperLeft, 0.55f)
            val upperRightInner = scalePoints(upperRight, 0.55f)
            val lowerLeftInner = scalePoints(lowerLeft, 0.55f)
            val lowerRightInner = scalePoints(lowerRight, 0.55f)

            val regions = listOf(
                ColoringRegion("butterfly_body", "Butterfly Body", body),
                ColoringRegion("upper_left_wing", "Top Left Wing", upperLeft),
                ColoringRegion("upper_left_wing_inner", "Top Left Wing Pattern", upperLeftInner),
                ColoringRegion("upper_right_wing", "Top Right Wing", upperRight),
                ColoringRegion("upper_right_wing_inner", "Top Right Wing Pattern", upperRightInner),
                ColoringRegion("lower_left_wing", "Bottom Left Wing", lowerLeft),
                ColoringRegion("lower_left_wing_inner", "Bottom Left Wing Pattern", lowerLeftInner),
                ColoringRegion("lower_right_wing", "Bottom Right Wing", lowerRight),
                ColoringRegion("lower_right_wing_inner", "Bottom Right Wing Pattern", lowerRightInner)
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
        // 8. FAIRY STAR WAND ✨
        // ==========================================
        private fun createFairyWandPage(): ColoringPage {
            val starPoints = mutableListOf<Point>()
            for (i in 0 until 10) {
                val r = if (i % 2 == 0) 0.20f else 0.09f
                val angle = -PI / 2.0 + i * PI / 5.0
                starPoints.add(Point((0.50f + r * cos(angle)).toFloat(), (0.35f + r * sin(angle)).toFloat()))
            }
            val handle = listOf(Point(0.47f, 0.50f), Point(0.53f, 0.50f), Point(0.53f, 0.88f), Point(0.47f, 0.88f))
            val ribbon1 = listOf(Point(0.53f, 0.52f), Point(0.66f, 0.62f), Point(0.58f, 0.72f), Point(0.53f, 0.58f))
            val ribbon2 = listOf(Point(0.47f, 0.52f), Point(0.34f, 0.62f), Point(0.42f, 0.72f), Point(0.47f, 0.58f))
            val starInner = scalePoints(starPoints, 0.5f)
            val sparkle1 = circlePoints(0.20f, 0.20f, 0.025f)
            val sparkle2 = circlePoints(0.80f, 0.20f, 0.025f)
            val sparkle3 = circlePoints(0.25f, 0.50f, 0.025f)
            val handleTip = circlePoints(0.50f, 0.89f, 0.035f)

            val regions = listOf(
                ColoringRegion("wand_star", "Magic Star", starPoints),
                ColoringRegion("wand_star_inner", "Star Glow", starInner),
                ColoringRegion("wand_handle", "Wand Handle", handle),
                ColoringRegion("handle_tip", "Handle Tip", handleTip),
                ColoringRegion("ribbon", "Sparkle Ribbon", ribbon1),
                ColoringRegion("ribbon_2", "Second Sparkle Ribbon", ribbon2),
                ColoringRegion("sparkle_1", "Sparkle", sparkle1),
                ColoringRegion("sparkle_2", "Sparkle", sparkle2),
                ColoringRegion("sparkle_3", "Sparkle", sparkle3)
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
        // 9. BLOOMING FLOWER 🌸
        // ==========================================
        private fun createBloomingFlowerPage(): ColoringPage {
            val center = circlePoints(0.50f, 0.38f, 0.10f)
            val petalTop = ovalPoints(0.50f, 0.20f, 0.08f, 0.10f)
            val petalBottom = ovalPoints(0.50f, 0.56f, 0.08f, 0.10f)
            val petalLeft = ovalPoints(0.32f, 0.38f, 0.10f, 0.08f)
            val petalRight = ovalPoints(0.68f, 0.38f, 0.10f, 0.08f)
            val petalNortheast = ovalPoints(0.635f, 0.255f, 0.085f, 0.085f)
            val petalSouthwest = ovalPoints(0.365f, 0.505f, 0.085f, 0.085f)
            val stem = listOf(Point(0.48f, 0.64f), Point(0.52f, 0.64f), Point(0.52f, 0.88f), Point(0.48f, 0.88f))
            val leafLeft = listOf(Point(0.48f, 0.72f), Point(0.34f, 0.68f), Point(0.48f, 0.80f))
            val leafRight = listOf(Point(0.52f, 0.74f), Point(0.66f, 0.70f), Point(0.52f, 0.82f))
            val centerInner = scalePoints(center, 0.5f)

            val regions = listOf(
                ColoringRegion("flower_center", "Flower Center", center),
                ColoringRegion("flower_center_inner", "Flower Center Pattern", centerInner),
                ColoringRegion("petal_top", "Top Petal", petalTop),
                ColoringRegion("petal_bottom", "Bottom Petal", petalBottom),
                ColoringRegion("petal_left", "Left Petal", petalLeft),
                ColoringRegion("petal_right", "Right Petal", petalRight),
                ColoringRegion("petal_northeast", "Northeast Petal", petalNortheast),
                ColoringRegion("petal_southwest", "Southwest Petal", petalSouthwest),
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
        // 10. RAINBOW SUN 🌈
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
            val sunInner = scalePoints(sun, 0.55f)
            val cloudLeftPuff = ovalPoints(0.12f, 0.76f, 0.07f, 0.05f)
            val cloudRightPuff = ovalPoints(0.88f, 0.76f, 0.07f, 0.05f)

            val regions = listOf(
                ColoringRegion("sun", "Smiling Sun", sun),
                ColoringRegion("sun_inner", "Sun Glow", sunInner),
                ColoringRegion("rainbow_outer", "Red Rainbow Arch", rainbowOuter),
                ColoringRegion("rainbow_mid", "Yellow Rainbow Arch", rainbowMid),
                ColoringRegion("rainbow_inner", "Blue Rainbow Arch", rainbowInner),
                ColoringRegion("cloud_left", "Fluffy Cloud Left", cloudLeft),
                ColoringRegion("cloud_left_puff", "Cloud Left Puff", cloudLeftPuff),
                ColoringRegion("cloud_right", "Fluffy Cloud Right", cloudRight),
                ColoringRegion("cloud_right_puff", "Cloud Right Puff", cloudRightPuff)
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

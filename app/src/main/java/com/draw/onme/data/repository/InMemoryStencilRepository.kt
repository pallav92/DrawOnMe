package com.draw.onme.data.repository

import com.draw.onme.domain.model.Point
import com.draw.onme.domain.model.Stencil
import com.draw.onme.domain.model.StencilPath
import com.draw.onme.domain.repository.StencilRepository
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * In-memory repository providing curated, high-recognition geometric stencil templates
 * designed specifically for young children to easily trace and color.
 * All coordinates are normalized in the [0.0f..1.0f] range.
 */
class InMemoryStencilRepository : StencilRepository {

    private val stencils: List<Stencil> = listOf(
        createCozyHouseStencil(),
        createZoomingCarStencil(),
        createSpaceRocketStencil(),
        createHappySailboatStencil(),
        createSmilingSunStencil(),
        createCuteTeddyStencil(),
        createPlayfulKittyStencil(),
        createCartoonMouseStencil()
    )

    override fun getStencils(): List<Stencil> {
        return stencils
    }

    override fun getStencilById(id: String): Stencil? {
        return stencils.firstOrNull { it.id == id }
    }

    companion object {
        private fun createCircle(
            cx: Float,
            cy: Float,
            radius: Float,
            steps: Int = 36,
            label: String? = null
        ): StencilPath {
            val points = (0..steps).map { i ->
                val angle = 2.0 * PI * i / steps
                Point(
                    x = (cx + radius * cos(angle)).toFloat().coerceIn(0f, 1f),
                    y = (cy + radius * sin(angle)).toFloat().coerceIn(0f, 1f)
                )
            }
            return StencilPath(points = points, isClosed = true, label = label)
        }

        private fun createOval(
            cx: Float,
            cy: Float,
            rx: Float,
            ry: Float,
            steps: Int = 32,
            label: String? = null
        ): StencilPath {
            val points = (0..steps).map { i ->
                val angle = 2.0 * PI * i / steps
                Point(
                    x = (cx + rx * cos(angle)).toFloat().coerceIn(0f, 1f),
                    y = (cy + ry * sin(angle)).toFloat().coerceIn(0f, 1f)
                )
            }
            return StencilPath(points = points, isClosed = true, label = label)
        }

        private fun createArc(
            cx: Float,
            cy: Float,
            rx: Float,
            ry: Float,
            startDeg: Double,
            sweepDeg: Double,
            steps: Int = 20,
            label: String? = null
        ): StencilPath {
            val points = (0..steps).map { i ->
                val angle = Math.toRadians(startDeg + sweepDeg * i / steps)
                Point(
                    x = (cx + rx * cos(angle)).toFloat().coerceIn(0f, 1f),
                    y = (cy + ry * sin(angle)).toFloat().coerceIn(0f, 1f)
                )
            }
            return StencilPath(points = points, isClosed = false, label = label)
        }

        private fun createPolygon(
            points: List<Point>,
            isClosed: Boolean = true,
            label: String? = null
        ): StencilPath {
            return StencilPath(
                points = points.map { Point(it.x.coerceIn(0f, 1f), it.y.coerceIn(0f, 1f)) },
                isClosed = isClosed,
                label = label
            )
        }

        // 1. Cozy House
        private fun createCozyHouseStencil(): Stencil {
            val paths = listOf(
                // Triangle Roof
                createPolygon(
                    points = listOf(Point(0.10f, 0.40f), Point(0.50f, 0.12f), Point(0.90f, 0.40f)),
                    isClosed = true,
                    label = "Roof"
                ),
                // Chimney
                createPolygon(
                    points = listOf(
                        Point(0.70f, 0.28f),
                        Point(0.70f, 0.14f),
                        Point(0.80f, 0.14f),
                        Point(0.80f, 0.35f)
                    ),
                    isClosed = true,
                    label = "Chimney"
                ),
                // Chimney Smoke Loop
                createArc(cx = 0.75f, cy = 0.08f, rx = 0.04f, ry = 0.04f, startDeg = -90.0, sweepDeg = 240.0, label = "Smoke"),
                // House Body Walls
                createPolygon(
                    points = listOf(
                        Point(0.18f, 0.40f),
                        Point(0.18f, 0.88f),
                        Point(0.82f, 0.88f),
                        Point(0.82f, 0.40f)
                    ),
                    isClosed = true,
                    label = "Walls"
                ),
                // Front Door
                createPolygon(
                    points = listOf(
                        Point(0.42f, 0.88f),
                        Point(0.42f, 0.58f),
                        Point(0.58f, 0.58f),
                        Point(0.58f, 0.88f)
                    ),
                    isClosed = true,
                    label = "Door"
                ),
                // Door Knob
                createCircle(cx = 0.54f, cy = 0.74f, radius = 0.015f, label = "Door Knob"),
                // Left Window
                createPolygon(
                    points = listOf(
                        Point(0.24f, 0.48f),
                        Point(0.36f, 0.48f),
                        Point(0.36f, 0.60f),
                        Point(0.24f, 0.60f)
                    ),
                    isClosed = true,
                    label = "Left Window"
                ),
                createPolygon(listOf(Point(0.24f, 0.54f), Point(0.36f, 0.54f)), isClosed = false, label = "Window Pane H"),
                createPolygon(listOf(Point(0.30f, 0.48f), Point(0.30f, 0.60f)), isClosed = false, label = "Window Pane V"),
                // Right Window
                createPolygon(
                    points = listOf(
                        Point(0.64f, 0.48f),
                        Point(0.76f, 0.48f),
                        Point(0.76f, 0.60f),
                        Point(0.64f, 0.60f)
                    ),
                    isClosed = true,
                    label = "Right Window"
                ),
                createPolygon(listOf(Point(0.64f, 0.54f), Point(0.76f, 0.54f)), isClosed = false, label = "Window Pane H"),
                createPolygon(listOf(Point(0.70f, 0.48f), Point(0.70f, 0.60f)), isClosed = false, label = "Window Pane V"),
                // Ground Line
                createPolygon(listOf(Point(0.08f, 0.88f), Point(0.92f, 0.88f)), isClosed = false, label = "Ground")
            )

            return Stencil(
                id = "cozy_house",
                title = "Cozy House",
                category = "Vehicles & Things",
                description = "Triangle roof, chimney with smoke, windows, and door",
                iconEmoji = "🏠",
                difficulty = "Easy",
                paths = paths
            )
        }

        // 2. Zooming Car
        private fun createZoomingCarStencil(): Stencil {
            val paths = listOf(
                // Car Cabin / Roof
                createPolygon(
                    points = listOf(
                        Point(0.24f, 0.50f),
                        Point(0.36f, 0.30f),
                        Point(0.68f, 0.30f),
                        Point(0.80f, 0.50f)
                    ),
                    isClosed = true,
                    label = "Roof"
                ),
                // Front Window
                createPolygon(
                    points = listOf(
                        Point(0.28f, 0.48f),
                        Point(0.38f, 0.34f),
                        Point(0.50f, 0.34f),
                        Point(0.50f, 0.48f)
                    ),
                    isClosed = true,
                    label = "Front Window"
                ),
                // Rear Window
                createPolygon(
                    points = listOf(
                        Point(0.53f, 0.34f),
                        Point(0.66f, 0.34f),
                        Point(0.76f, 0.48f),
                        Point(0.53f, 0.48f)
                    ),
                    isClosed = true,
                    label = "Rear Window"
                ),
                // Car Lower Body
                createPolygon(
                    points = listOf(
                        Point(0.08f, 0.50f),
                        Point(0.08f, 0.66f),
                        Point(0.16f, 0.66f),
                        Point(0.38f, 0.66f),
                        Point(0.64f, 0.66f),
                        Point(0.86f, 0.66f),
                        Point(0.94f, 0.66f),
                        Point(0.94f, 0.50f),
                        Point(0.80f, 0.50f),
                        Point(0.24f, 0.50f)
                    ),
                    isClosed = true,
                    label = "Body"
                ),
                // Front Wheel & Hubcap
                createCircle(cx = 0.27f, cy = 0.66f, radius = 0.11f, label = "Front Wheel"),
                createCircle(cx = 0.27f, cy = 0.66f, radius = 0.05f, label = "Front Rim"),
                // Rear Wheel & Hubcap
                createCircle(cx = 0.75f, cy = 0.66f, radius = 0.11f, label = "Rear Wheel"),
                createCircle(cx = 0.75f, cy = 0.66f, radius = 0.05f, label = "Rear Rim"),
                // Headlight & Taillight
                createPolygon(listOf(Point(0.94f, 0.54f), Point(0.94f, 0.60f)), isClosed = false, label = "Headlight"),
                createPolygon(listOf(Point(0.08f, 0.54f), Point(0.08f, 0.60f)), isClosed = false, label = "Taillight")
            )

            return Stencil(
                id = "zooming_car",
                title = "Zooming Car",
                category = "Vehicles & Things",
                description = "Sleek car body with windows and round wheels",
                iconEmoji = "🚗",
                difficulty = "Easy",
                paths = paths
            )
        }

        // 3. Space Rocket
        private fun createSpaceRocketStencil(): Stencil {
            val paths = listOf(
                // Pointed Nose Cone
                createPolygon(
                    points = listOf(Point(0.36f, 0.30f), Point(0.50f, 0.10f), Point(0.64f, 0.30f)),
                    isClosed = true,
                    label = "Nose Cone"
                ),
                // Rocket Cylinder Body
                createPolygon(
                    points = listOf(
                        Point(0.36f, 0.30f),
                        Point(0.36f, 0.70f),
                        Point(0.64f, 0.70f),
                        Point(0.64f, 0.30f)
                    ),
                    isClosed = true,
                    label = "Fuselage"
                ),
                // Left Stabilizer Fin
                createPolygon(
                    points = listOf(Point(0.36f, 0.56f), Point(0.18f, 0.76f), Point(0.36f, 0.70f)),
                    isClosed = true,
                    label = "Left Fin"
                ),
                // Right Stabilizer Fin
                createPolygon(
                    points = listOf(Point(0.64f, 0.56f), Point(0.82f, 0.76f), Point(0.64f, 0.70f)),
                    isClosed = true,
                    label = "Right Fin"
                ),
                // Astronaut Porthole Window
                createCircle(cx = 0.50f, cy = 0.44f, radius = 0.08f, label = "Outer Window"),
                createCircle(cx = 0.50f, cy = 0.44f, radius = 0.05f, label = "Inner Window"),
                // Rocket Engine Nozzle
                createPolygon(
                    points = listOf(
                        Point(0.42f, 0.70f),
                        Point(0.38f, 0.76f),
                        Point(0.62f, 0.76f),
                        Point(0.58f, 0.70f)
                    ),
                    isClosed = true,
                    label = "Nozzle"
                ),
                // Fire Booster Flame
                createPolygon(
                    points = listOf(
                        Point(0.40f, 0.76f),
                        Point(0.44f, 0.86f),
                        Point(0.50f, 0.94f),
                        Point(0.56f, 0.86f),
                        Point(0.60f, 0.76f),
                        Point(0.50f, 0.80f)
                    ),
                    isClosed = true,
                    label = "Fire"
                )
            )

            return Stencil(
                id = "space_rocket",
                title = "Space Rocket",
                category = "Vehicles & Things",
                description = "Blasting off into space with fins and booster fire",
                iconEmoji = "🚀",
                difficulty = "Medium",
                paths = paths
            )
        }

        // 4. Happy Sailboat
        private fun createHappySailboatStencil(): Stencil {
            val paths = listOf(
                // Boat Hull
                createPolygon(
                    points = listOf(
                        Point(0.12f, 0.62f),
                        Point(0.88f, 0.62f),
                        Point(0.76f, 0.78f),
                        Point(0.24f, 0.78f)
                    ),
                    isClosed = true,
                    label = "Hull"
                ),
                // Mast
                createPolygon(
                    points = listOf(Point(0.48f, 0.62f), Point(0.48f, 0.16f)),
                    isClosed = false,
                    label = "Mast"
                ),
                // Flag
                createPolygon(
                    points = listOf(Point(0.48f, 0.16f), Point(0.58f, 0.20f), Point(0.48f, 0.24f)),
                    isClosed = true,
                    label = "Flag"
                ),
                // Main Right Sail
                createPolygon(
                    points = listOf(Point(0.50f, 0.20f), Point(0.50f, 0.58f), Point(0.82f, 0.58f)),
                    isClosed = true,
                    label = "Main Sail"
                ),
                // Front Left Sail
                createPolygon(
                    points = listOf(Point(0.46f, 0.25f), Point(0.46f, 0.58f), Point(0.20f, 0.58f)),
                    isClosed = true,
                    label = "Front Sail"
                ),
                // Ocean Waves
                createArc(cx = 0.22f, cy = 0.84f, rx = 0.12f, ry = 0.04f, startDeg = 0.0, sweepDeg = 180.0, label = "Wave 1"),
                createArc(cx = 0.50f, cy = 0.84f, rx = 0.12f, ry = 0.04f, startDeg = 0.0, sweepDeg = 180.0, label = "Wave 2"),
                createArc(cx = 0.78f, cy = 0.84f, rx = 0.12f, ry = 0.04f, startDeg = 0.0, sweepDeg = 180.0, label = "Wave 3")
            )

            return Stencil(
                id = "happy_sailboat",
                title = "Happy Sailboat",
                category = "Vehicles & Things",
                description = "Floating sailboat with big sails and ocean waves",
                iconEmoji = "⛵",
                difficulty = "Easy",
                paths = paths
            )
        }

        // 5. Smiling Sun
        private fun createSmilingSunStencil(): Stencil {
            val paths = listOf(
                // Big Center Face
                createCircle(cx = 0.50f, cy = 0.50f, radius = 0.24f, label = "Sun Face"),
                // 8 Radiant Triangle Rays
                createPolygon(listOf(Point(0.46f, 0.25f), Point(0.50f, 0.08f), Point(0.54f, 0.25f)), label = "Ray Top"),
                createPolygon(listOf(Point(0.46f, 0.75f), Point(0.50f, 0.92f), Point(0.54f, 0.75f)), label = "Ray Bottom"),
                createPolygon(listOf(Point(0.75f, 0.46f), Point(0.92f, 0.50f), Point(0.75f, 0.54f)), label = "Ray Right"),
                createPolygon(listOf(Point(0.25f, 0.46f), Point(0.08f, 0.50f), Point(0.25f, 0.54f)), label = "Ray Left"),
                createPolygon(listOf(Point(0.64f, 0.33f), Point(0.78f, 0.22f), Point(0.69f, 0.38f)), label = "Ray TR"),
                createPolygon(listOf(Point(0.36f, 0.33f), Point(0.22f, 0.22f), Point(0.31f, 0.38f)), label = "Ray TL"),
                createPolygon(listOf(Point(0.69f, 0.62f), Point(0.78f, 0.78f), Point(0.64f, 0.67f)), label = "Ray BR"),
                createPolygon(listOf(Point(0.31f, 0.62f), Point(0.22f, 0.78f), Point(0.36f, 0.67f)), label = "Ray BL"),
                // Eyes
                createCircle(cx = 0.42f, cy = 0.46f, radius = 0.03f, label = "Left Eye"),
                createCircle(cx = 0.58f, cy = 0.46f, radius = 0.03f, label = "Right Eye"),
                // Happy Smile
                createArc(cx = 0.50f, cy = 0.48f, rx = 0.12f, ry = 0.12f, startDeg = 20.0, sweepDeg = 140.0, label = "Smile"),
                // Cheeks
                createCircle(cx = 0.36f, cy = 0.54f, radius = 0.025f, label = "Left Cheek"),
                createCircle(cx = 0.64f, cy = 0.54f, radius = 0.025f, label = "Right Cheek")
            )

            return Stencil(
                id = "smiling_sun",
                title = "Smiling Sun",
                category = "Animals & Friends",
                description = "Bright geometric sun with triangular rays and smile",
                iconEmoji = "☀️",
                difficulty = "Easy",
                paths = paths
            )
        }

        // 6. Cute Teddy Bear
        private fun createCuteTeddyStencil(): Stencil {
            val paths = listOf(
                // Body Tummy
                createCircle(cx = 0.50f, cy = 0.66f, radius = 0.22f, label = "Body"),
                createOval(cx = 0.50f, cy = 0.66f, rx = 0.12f, ry = 0.14f, label = "Tummy Patch"),
                // Head
                createCircle(cx = 0.50f, cy = 0.36f, radius = 0.18f, label = "Head"),
                // Left Ear & Inner Ear
                createCircle(cx = 0.34f, cy = 0.22f, radius = 0.07f, label = "Left Ear"),
                createCircle(cx = 0.34f, cy = 0.22f, radius = 0.04f, label = "Left Inner Ear"),
                // Right Ear & Inner Ear
                createCircle(cx = 0.66f, cy = 0.22f, radius = 0.07f, label = "Right Ear"),
                createCircle(cx = 0.66f, cy = 0.22f, radius = 0.04f, label = "Right Inner Ear"),
                // Snout
                createOval(cx = 0.50f, cy = 0.40f, rx = 0.07f, ry = 0.05f, label = "Snout"),
                createOval(cx = 0.50f, cy = 0.38f, rx = 0.03f, ry = 0.02f, label = "Nose"),
                createArc(cx = 0.50f, cy = 0.40f, rx = 0.03f, ry = 0.03f, startDeg = 20.0, sweepDeg = 140.0, label = "Mouth"),
                // Eyes
                createCircle(cx = 0.43f, cy = 0.32f, radius = 0.02f, label = "Left Eye"),
                createCircle(cx = 0.57f, cy = 0.32f, radius = 0.02f, label = "Right Eye"),
                // Arms
                createOval(cx = 0.26f, cy = 0.58f, rx = 0.06f, ry = 0.11f, label = "Left Arm"),
                createOval(cx = 0.74f, cy = 0.58f, rx = 0.06f, ry = 0.11f, label = "Right Arm"),
                // Feet
                createCircle(cx = 0.36f, cy = 0.86f, radius = 0.08f, label = "Left Foot"),
                createCircle(cx = 0.64f, cy = 0.86f, radius = 0.08f, label = "Right Foot")
            )

            return Stencil(
                id = "cute_teddy",
                title = "Cute Teddy Bear",
                category = "Animals & Friends",
                description = "Lovable geometric teddy bear with round ears and paws",
                iconEmoji = "🧸",
                difficulty = "Easy",
                paths = paths
            )
        }

        // 7. Playful Kitty
        private fun createPlayfulKittyStencil(): Stencil {
            val paths = listOf(
                // Round Head
                createCircle(cx = 0.50f, cy = 0.40f, radius = 0.22f, label = "Head"),
                // Left Triangular Ear & Inner
                createPolygon(listOf(Point(0.30f, 0.28f), Point(0.24f, 0.12f), Point(0.42f, 0.20f)), label = "Left Ear"),
                createPolygon(listOf(Point(0.31f, 0.25f), Point(0.27f, 0.16f), Point(0.38f, 0.21f)), label = "Left Inner Ear"),
                // Right Triangular Ear & Inner
                createPolygon(listOf(Point(0.70f, 0.28f), Point(0.76f, 0.12f), Point(0.58f, 0.20f)), label = "Right Ear"),
                createPolygon(listOf(Point(0.69f, 0.25f), Point(0.73f, 0.16f), Point(0.62f, 0.21f)), label = "Right Inner Ear"),
                // Eyes
                createOval(cx = 0.40f, cy = 0.37f, rx = 0.035f, ry = 0.045f, label = "Left Eye"),
                createOval(cx = 0.60f, cy = 0.37f, rx = 0.035f, ry = 0.045f, label = "Right Eye"),
                // Button Nose
                createPolygon(listOf(Point(0.47f, 0.44f), Point(0.53f, 0.44f), Point(0.50f, 0.48f)), label = "Nose"),
                // Mouth
                createArc(cx = 0.46f, cy = 0.48f, rx = 0.04f, ry = 0.04f, startDeg = 0.0, sweepDeg = 180.0, label = "Left Lip"),
                createArc(cx = 0.54f, cy = 0.48f, rx = 0.04f, ry = 0.04f, startDeg = 0.0, sweepDeg = 180.0, label = "Right Lip"),
                // Whiskers
                createPolygon(listOf(Point(0.16f, 0.43f), Point(0.33f, 0.45f)), isClosed = false, label = "Whisker L1"),
                createPolygon(listOf(Point(0.14f, 0.48f), Point(0.32f, 0.48f)), isClosed = false, label = "Whisker L2"),
                createPolygon(listOf(Point(0.84f, 0.43f), Point(0.67f, 0.45f)), isClosed = false, label = "Whisker R1"),
                createPolygon(listOf(Point(0.86f, 0.48f), Point(0.68f, 0.48f)), isClosed = false, label = "Whisker R2"),
                // Body & Paws
                createOval(cx = 0.50f, cy = 0.72f, rx = 0.16f, ry = 0.18f, label = "Body"),
                createCircle(cx = 0.43f, cy = 0.88f, radius = 0.04f, label = "Left Paw"),
                createCircle(cx = 0.57f, cy = 0.88f, radius = 0.04f, label = "Right Paw"),
                // Arched Tail
                createArc(cx = 0.72f, cy = 0.72f, rx = 0.14f, ry = 0.14f, startDeg = 270.0, sweepDeg = 120.0, label = "Tail")
            )

            return Stencil(
                id = "playful_kitty",
                title = "Playful Kitty",
                category = "Animals & Friends",
                description = "Cute cat with pointy ears, whiskers, and curled tail",
                iconEmoji = "🐱",
                difficulty = "Easy",
                paths = paths
            )
        }

        // 8. Classic Cartoon Mouse
        private fun createCartoonMouseStencil(): Stencil {
            val paths = listOf(
                // Big Circular Head
                createCircle(cx = 0.50f, cy = 0.55f, radius = 0.25f, label = "Head"),
                // Left Big Round Ear
                createCircle(cx = 0.26f, cy = 0.26f, radius = 0.18f, label = "Left Ear"),
                // Right Big Round Ear
                createCircle(cx = 0.74f, cy = 0.26f, radius = 0.18f, label = "Right Ear"),
                // Left Eye
                createOval(cx = 0.43f, cy = 0.48f, rx = 0.04f, ry = 0.08f, label = "Left Eye"),
                // Right Eye
                createOval(cx = 0.57f, cy = 0.48f, rx = 0.04f, ry = 0.08f, label = "Right Eye"),
                // Cute Button Nose
                createOval(cx = 0.50f, cy = 0.59f, rx = 0.06f, ry = 0.04f, label = "Nose"),
                // Big Cartoon Smile
                createArc(cx = 0.50f, cy = 0.56f, rx = 0.16f, ry = 0.16f, startDeg = 20.0, sweepDeg = 140.0, label = "Smile"),
                // Dimples
                createPolygon(listOf(Point(0.32f, 0.65f), Point(0.35f, 0.70f)), isClosed = false, label = "Left Dimple"),
                createPolygon(listOf(Point(0.68f, 0.65f), Point(0.65f, 0.70f)), isClosed = false, label = "Right Dimple")
            )

            return Stencil(
                id = "cartoon_mouse",
                title = "Playful Mouse",
                category = "Animals & Friends",
                description = "Iconic round ears, cute button nose, and joyful smile",
                iconEmoji = "🐭",
                difficulty = "Easy",
                paths = paths
            )
        }
    }
}

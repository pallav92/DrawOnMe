package com.pallav.drawonme.data.repository

import com.pallav.drawonme.domain.model.Point
import com.pallav.drawonme.domain.model.Stencil
import com.pallav.drawonme.domain.model.StencilPath
import com.pallav.drawonme.domain.repository.StencilRepository
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * In-memory implementation of StencilRepository providing curated cartoon tracing templates.
 * All coordinates are normalized in the [0f..1f] range.
 */
class InMemoryStencilRepository : StencilRepository {

    private val stencils: List<Stencil> = listOf(
        createPlayfulMouseStencil(),
        createCheerfulDuckStencil(),
        createJungleElephantStencil(),
        createHappyLionStencil()
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
                    x = (cx + radius * cos(angle)).toFloat(),
                    y = (cy + radius * sin(angle)).toFloat()
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
                    x = (cx + rx * cos(angle)).toFloat(),
                    y = (cy + ry * sin(angle)).toFloat()
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
                    x = (cx + rx * cos(angle)).toFloat(),
                    y = (cy + ry * sin(angle)).toFloat()
                )
            }
            return StencilPath(points = points, isClosed = false, label = label)
        }

        private fun createPlayfulMouseStencil(): Stencil {
            val paths = listOf(
                // Head
                createCircle(cx = 0.50f, cy = 0.54f, radius = 0.22f, label = "Head"),
                // Left Ear
                createCircle(cx = 0.30f, cy = 0.30f, radius = 0.15f, label = "Left Ear"),
                // Right Ear
                createCircle(cx = 0.70f, cy = 0.30f, radius = 0.15f, label = "Right Ear"),
                // Left Eye
                createOval(cx = 0.44f, cy = 0.48f, rx = 0.035f, ry = 0.06f, label = "Left Eye"),
                // Right Eye
                createOval(cx = 0.56f, cy = 0.48f, rx = 0.035f, ry = 0.06f, label = "Right Eye"),
                // Cute Button Nose
                createOval(cx = 0.50f, cy = 0.58f, rx = 0.05f, ry = 0.03f, label = "Nose"),
                // Big Happy Smile
                createArc(cx = 0.50f, cy = 0.56f, rx = 0.14f, ry = 0.14f, startDeg = 25.0, sweepDeg = 130.0, label = "Smile"),
                // Smile Dimples
                StencilPath(points = listOf(Point(0.35f, 0.64f), Point(0.38f, 0.68f)), label = "Left Dimple"),
                StencilPath(points = listOf(Point(0.65f, 0.64f), Point(0.62f, 0.68f)), label = "Right Dimple")
            )

            return Stencil(
                id = "playful_mouse",
                title = "Playful Mouse",
                category = "Cartoons",
                description = "Iconic round ears and a happy cartoon smile",
                iconEmoji = "🐭",
                difficulty = "Easy",
                paths = paths
            )
        }

        private fun createCheerfulDuckStencil(): Stencil {
            val paths = listOf(
                // Head
                createCircle(cx = 0.50f, cy = 0.38f, radius = 0.18f, label = "Head"),
                // Sailor Cap
                StencilPath(
                    points = listOf(
                        Point(0.36f, 0.23f),
                        Point(0.48f, 0.16f),
                        Point(0.64f, 0.23f),
                        Point(0.50f, 0.25f),
                        Point(0.36f, 0.23f)
                    ),
                    isClosed = true,
                    label = "Sailor Cap"
                ),
                // Left Eye
                createOval(cx = 0.45f, cy = 0.34f, rx = 0.03f, ry = 0.05f, label = "Left Eye"),
                // Right Eye
                createOval(cx = 0.55f, cy = 0.34f, rx = 0.03f, ry = 0.05f, label = "Right Eye"),
                // Upper Duck Bill
                StencilPath(
                    points = listOf(
                        Point(0.30f, 0.44f),
                        Point(0.24f, 0.48f),
                        Point(0.34f, 0.51f),
                        Point(0.50f, 0.52f),
                        Point(0.66f, 0.51f),
                        Point(0.76f, 0.48f),
                        Point(0.70f, 0.44f)
                    ),
                    isClosed = false,
                    label = "Upper Bill"
                ),
                // Lower Bill Smile
                StencilPath(
                    points = listOf(
                        Point(0.28f, 0.49f),
                        Point(0.38f, 0.62f),
                        Point(0.50f, 0.65f),
                        Point(0.62f, 0.62f),
                        Point(0.72f, 0.49f)
                    ),
                    isClosed = false,
                    label = "Lower Bill"
                ),
                // Bow tie hint
                StencilPath(
                    points = listOf(
                        Point(0.42f, 0.70f),
                        Point(0.50f, 0.67f),
                        Point(0.58f, 0.70f),
                        Point(0.50f, 0.73f),
                        Point(0.42f, 0.70f)
                    ),
                    isClosed = true,
                    label = "Bow Tie"
                )
            )

            return Stencil(
                id = "cheerful_duck",
                title = "Cheerful Duck",
                category = "Cartoons",
                description = "Classic duck bill with a playful sailor cap",
                iconEmoji = "🦆",
                difficulty = "Medium",
                paths = paths
            )
        }

        private fun createJungleElephantStencil(): Stencil {
            val paths = listOf(
                // Head & Body
                createCircle(cx = 0.52f, cy = 0.45f, radius = 0.22f, label = "Head"),
                // Left Big Ear
                StencilPath(
                    points = listOf(
                        Point(0.40f, 0.30f),
                        Point(0.22f, 0.32f),
                        Point(0.18f, 0.48f),
                        Point(0.24f, 0.62f),
                        Point(0.38f, 0.55f)
                    ),
                    isClosed = false,
                    label = "Big Ear"
                ),
                // Long Curved Trunk
                StencilPath(
                    points = listOf(
                        Point(0.48f, 0.48f),
                        Point(0.44f, 0.62f),
                        Point(0.42f, 0.74f),
                        Point(0.48f, 0.82f),
                        Point(0.56f, 0.82f),
                        Point(0.60f, 0.74f),
                        Point(0.54f, 0.75f),
                        Point(0.48f, 0.72f),
                        Point(0.50f, 0.58f),
                        Point(0.54f, 0.48f)
                    ),
                    isClosed = false,
                    label = "Curved Trunk"
                ),
                // Friendly Eye
                createCircle(cx = 0.46f, cy = 0.38f, radius = 0.025f, label = "Eye"),
                // Small Tusk
                StencilPath(
                    points = listOf(
                        Point(0.52f, 0.52f),
                        Point(0.60f, 0.56f),
                        Point(0.54f, 0.56f)
                    ),
                    isClosed = true,
                    label = "Tusk"
                ),
                // Elephant Feet hints
                StencilPath(
                    points = listOf(
                        Point(0.42f, 0.66f),
                        Point(0.42f, 0.86f),
                        Point(0.50f, 0.86f),
                        Point(0.50f, 0.68f)
                    ),
                    isClosed = false,
                    label = "Front Leg"
                ),
                StencilPath(
                    points = listOf(
                        Point(0.62f, 0.64f),
                        Point(0.62f, 0.86f),
                        Point(0.70f, 0.86f),
                        Point(0.70f, 0.62f)
                    ),
                    isClosed = false,
                    label = "Back Leg"
                )
            )

            return Stencil(
                id = "jungle_elephant",
                title = "Jungle Elephant",
                category = "Animals",
                description = "Gentle elephant with big ears and a raised trunk",
                iconEmoji = "🐘",
                difficulty = "Medium",
                paths = paths
            )
        }

        private fun createHappyLionStencil(): Stencil {
            // Mane with 12 radiant wavy scallops
            val manePoints = (0..72).map { i ->
                val angle = 2.0 * PI * i / 72
                val r = 0.28f + 0.08f * (0.5f + 0.5f * sin(angle * 12).toFloat())
                Point(
                    x = (0.50f + r * cos(angle)).toFloat(),
                    y = (0.46f + r * sin(angle)).toFloat()
                )
            }

            val paths = listOf(
                // Fluffy Mane
                StencilPath(points = manePoints, isClosed = true, label = "Mane"),
                // Face Circle
                createCircle(cx = 0.50f, cy = 0.46f, radius = 0.20f, label = "Face"),
                // Left Ear
                createCircle(cx = 0.35f, cy = 0.28f, radius = 0.06f, label = "Left Ear"),
                // Right Ear
                createCircle(cx = 0.65f, cy = 0.28f, radius = 0.06f, label = "Right Ear"),
                // Left Eye
                createCircle(cx = 0.42f, cy = 0.42f, radius = 0.025f, label = "Left Eye"),
                // Right Eye
                createCircle(cx = 0.58f, cy = 0.42f, radius = 0.025f, label = "Right Eye"),
                // Nose Triangle
                StencilPath(
                    points = listOf(
                        Point(0.46f, 0.49f),
                        Point(0.54f, 0.49f),
                        Point(0.50f, 0.54f),
                        Point(0.46f, 0.49f)
                    ),
                    isClosed = true,
                    label = "Nose"
                ),
                // Mouth
                StencilPath(
                    points = listOf(Point(0.50f, 0.54f), Point(0.50f, 0.58f)),
                    label = "Philtrum"
                ),
                createArc(cx = 0.45f, cy = 0.57f, rx = 0.05f, ry = 0.04f, startDeg = 0.0, sweepDeg = 180.0, label = "Left Lip"),
                createArc(cx = 0.55f, cy = 0.57f, rx = 0.05f, ry = 0.04f, startDeg = 0.0, sweepDeg = 180.0, label = "Right Lip"),
                // Whiskers
                StencilPath(points = listOf(Point(0.30f, 0.52f), Point(0.40f, 0.53f)), label = "Left Whisker 1"),
                StencilPath(points = listOf(Point(0.30f, 0.56f), Point(0.40f, 0.56f)), label = "Left Whisker 2"),
                StencilPath(points = listOf(Point(0.60f, 0.53f), Point(0.70f, 0.52f)), label = "Right Whisker 1"),
                StencilPath(points = listOf(Point(0.60f, 0.56f), Point(0.70f, 0.56f)), label = "Right Whisker 2")
            )

            return Stencil(
                id = "happy_lion",
                title = "Happy Lion",
                category = "Animals",
                description = "King of the jungle with a big fluffy mane and cute smile",
                iconEmoji = "🦁",
                difficulty = "Easy",
                paths = paths
            )
        }
    }
}

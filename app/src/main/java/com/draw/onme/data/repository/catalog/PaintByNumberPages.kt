package com.draw.onme.data.repository.catalog

import com.draw.onme.data.repository.InMemoryColoringBookRepository.Companion.CATEGORY_PAINT_BY_NUMBER
import com.draw.onme.data.repository.catalog.ColoringPageGenerators.circlePoints
import com.draw.onme.data.repository.catalog.ColoringPageGenerators.createPage
import com.draw.onme.data.repository.catalog.ColoringPageGenerators.ovalPoints
import com.draw.onme.data.repository.catalog.ColoringPageGenerators.rectPoints
import com.draw.onme.data.repository.catalog.ColoringPageGenerators.trianglePoints
import com.draw.onme.domain.model.ColoringPage
import com.draw.onme.domain.model.ColoringRegion
import com.draw.onme.domain.model.StrokeColor

/**
 * Paint by Number pages: every region declares a fixed [ColoringRegion.targetColor], so the
 * Coloring Studio numbers the distinct colors and only accepts a fill matching a region's number.
 */
internal val PaintByNumberPages: List<ColoringPage> = listOf(
    createPage(
        id = "pbn_rainbow_flower",
        title = "Rainbow Number Flower",
        category = CATEGORY_PAINT_BY_NUMBER,
        iconEmoji = "🌼",
        difficulty = "Medium",
        regions = listOf(
            ColoringRegion("flower_center", "Flower Center", circlePoints(0.50f, 0.38f, 0.10f), targetColor = StrokeColor(0xFF8D6E63)),
            ColoringRegion("petal_n", "North Petal", ovalPoints(0.50f, 0.20f, 0.08f, 0.10f), targetColor = StrokeColor(0xFFE53935)),
            ColoringRegion("petal_ne", "Northeast Petal", ovalPoints(0.627f, 0.253f, 0.085f, 0.085f), targetColor = StrokeColor(0xFFFB8C00)),
            ColoringRegion("petal_e", "East Petal", ovalPoints(0.68f, 0.38f, 0.10f, 0.08f), targetColor = StrokeColor(0xFFFDD835)),
            ColoringRegion("petal_se", "Southeast Petal", ovalPoints(0.627f, 0.507f, 0.085f, 0.085f), targetColor = StrokeColor(0xFFF06292)),
            ColoringRegion("petal_s", "South Petal", ovalPoints(0.50f, 0.56f, 0.08f, 0.10f), targetColor = StrokeColor(0xFF8E24AA)),
            ColoringRegion("petal_sw", "Southwest Petal", ovalPoints(0.373f, 0.507f, 0.085f, 0.085f), targetColor = StrokeColor(0xFF1E88E5)),
            ColoringRegion("petal_w", "West Petal", ovalPoints(0.32f, 0.38f, 0.10f, 0.08f), targetColor = StrokeColor(0xFF26A69A)),
            ColoringRegion("petal_nw", "Northwest Petal", ovalPoints(0.373f, 0.253f, 0.085f, 0.085f), targetColor = StrokeColor(0xFF8BC34A)),
            ColoringRegion("stem", "Green Stem", rectPoints(0.48f, 0.64f, 0.52f, 0.88f), targetColor = StrokeColor(0xFF43A047)),
            ColoringRegion("leaf_left", "Left Leaf", trianglePoints(0.48f, 0.72f, 0.34f, 0.68f, 0.48f, 0.80f), targetColor = StrokeColor(0xFF43A047)),
            ColoringRegion("leaf_right", "Right Leaf", trianglePoints(0.52f, 0.74f, 0.66f, 0.70f, 0.52f, 0.82f), targetColor = StrokeColor(0xFF43A047))
        )
    )
)

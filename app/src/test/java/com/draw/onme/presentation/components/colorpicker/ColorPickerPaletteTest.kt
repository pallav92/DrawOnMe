package com.draw.onme.presentation.components.colorpicker

import com.draw.onme.domain.model.StrokeColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class ColorPickerPaletteTest {

    @Test
    fun testAllCategories_containCuratedColors() {
        val categories = ColorPickerPalette.AllCategories
        assertTrue(categories.isNotEmpty())

        val rainbow = categories.find { it.id == "rainbow" }
        val pastels = categories.find { it.id == "pastel" }
        val nature = categories.find { it.id == "nature" }
        val neon = categories.find { it.id == "neon" }

        assertTrue(rainbow != null && rainbow.colors.size >= 12)
        assertTrue(pastels != null && pastels.colors.size >= 10)
        assertTrue(nature != null && nature.colors.size >= 10)
        assertTrue(neon != null && neon.colors.size >= 10)
    }

    @Test
    fun testHsvToStrokeColor_primaryColors() {
        // Red: H=0, S=1, V=1 -> #FFFF0000
        val red = ColorPickerPalette.hsvToStrokeColor(0f, 1f, 1f)
        assertEquals(0xFFFF0000.toInt(), red.argb.toInt())

        // Green: H=120, S=1, V=1 -> #FF00FF00
        val green = ColorPickerPalette.hsvToStrokeColor(120f, 1f, 1f)
        assertEquals(0xFF00FF00.toInt(), green.argb.toInt())

        // Blue: H=240, S=1, V=1 -> #FF0000FF
        val blue = ColorPickerPalette.hsvToStrokeColor(240f, 1f, 1f)
        assertEquals(0xFF0000FF.toInt(), blue.argb.toInt())

        // White: H=0, S=0, V=1 -> #FFFFFFFF
        val white = ColorPickerPalette.hsvToStrokeColor(0f, 0f, 1f)
        assertEquals(0xFFFFFFFF.toInt(), white.argb.toInt())

        // Black: H=0, S=0, V=0 -> #FF000000
        val black = ColorPickerPalette.hsvToStrokeColor(0f, 0f, 0f)
        assertEquals(0xFF000000.toInt(), black.argb.toInt())
    }

    @Test
    fun testStrokeColorToHsv_roundTrip() {
        val testColors = listOf(
            StrokeColor(0xFFFF0000), // Pure Red
            StrokeColor(0xFF00FF00), // Pure Green
            StrokeColor(0xFF0000FF), // Pure Blue
            StrokeColor(0xFFFFFFFF), // White
            StrokeColor(0xFF000000), // Black
            StrokeColor(0xFFE53935), // Cherry Red
            StrokeColor(0xFF1E88E5), // Ocean Blue
            StrokeColor(0xFF43A047)  // Grass Green
        )

        for (color in testColors) {
            val (h, s, v) = ColorPickerPalette.strokeColorToHsv(color)
            val reconstructed = ColorPickerPalette.hsvToStrokeColor(h, s, v)

            val rOrig = (color.argb shr 16) and 0xFF
            val gOrig = (color.argb shr 8) and 0xFF
            val bOrig = color.argb and 0xFF

            val rRecon = (reconstructed.argb shr 16) and 0xFF
            val gRecon = (reconstructed.argb shr 8) and 0xFF
            val bRecon = reconstructed.argb and 0xFF

            // Allow +/- 2 tolerance due to integer rounding
            assertTrue(
                "Mismatch for color ${color.argb}: orig=($rOrig,$gOrig,$bOrig), recon=($rRecon,$gRecon,$bRecon)",
                abs(rOrig - rRecon) <= 2 && abs(gOrig - gRecon) <= 2 && abs(bOrig - bRecon) <= 2
            )
        }
    }
}

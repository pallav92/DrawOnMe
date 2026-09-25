package com.draw.onme.presentation.components.colorpicker

import com.draw.onme.domain.model.StrokeColor
import kotlin.math.abs

/**
 * Categorized, child-friendly color palette definition.
 */
data class ColorCategory(
    val id: String,
    val title: String,
    val icon: String,
    val colors: List<StrokeColor>
)

/**
 * Universal color palettes and color conversion math.
 */
object ColorPickerPalette {

    /**
     * Classic 16-color rainbow favorites: bright, vibrant, primary & secondary colors.
     */
    val RainbowColors: List<StrokeColor> = listOf(
        StrokeColor(0xFFE53935), // Cherry Red
        StrokeColor(0xFFD81B60), // Berry Pink
        StrokeColor(0xFFFF5722), // Coral Flame
        StrokeColor(0xFFFB8C00), // Tangerine Orange
        StrokeColor(0xFFFFA000), // Amber Gold
        StrokeColor(0xFFFDD835), // Sunshine Yellow
        StrokeColor(0xFF7CB342), // Lime Green
        StrokeColor(0xFF43A047), // Grass Green
        StrokeColor(0xFF00897B), // Teal Green
        StrokeColor(0xFF00ACC1), // Sky Cyan
        StrokeColor(0xFF039BE5), // Azure Sky
        StrokeColor(0xFF1E88E5), // Ocean Blue
        StrokeColor(0xFF3949AB), // Royal Indigo
        StrokeColor(0xFF8E24AA), // Magic Purple
        StrokeColor(0xFFEC407A), // Bubblegum Pink
        StrokeColor(0xFF8D6E63)  // Warm Brown
    )

    /**
     * 12 soft, delightful pastel shades.
     */
    val PastelColors: List<StrokeColor> = listOf(
        StrokeColor(0xFFFFB2D1), // Pastel Pink
        StrokeColor(0xFFFFC1E3), // Cotton Candy
        StrokeColor(0xFFFFCCBC), // Soft Peach
        StrokeColor(0xFFFFF59D), // Buttercup Yellow
        StrokeColor(0xFFC8E6C9), // Mint Frost
        StrokeColor(0xFFB2DFDB), // Seafoam
        StrokeColor(0xFFBBDEFB), // Baby Blue
        StrokeColor(0xFFB3E5FC), // Sky Mist
        StrokeColor(0xFFE1BEE7), // Soft Lavender
        StrokeColor(0xFFCE93D8), // Lilac
        StrokeColor(0xFFFFF8E1), // Cream Puff
        StrokeColor(0xFFFFFFFF)  // Pure White
    )

    /**
     * 12 organic earth, nature, animal and neutral colors.
     */
    val NatureColors: List<StrokeColor> = listOf(
        StrokeColor(0xFFFFE082), // Sunny Straw
        StrokeColor(0xFFD7CCC8), // Sand Dune
        StrokeColor(0xFFD84315), // Terracotta
        StrokeColor(0xFFA1887F), // Cinnamon
        StrokeColor(0xFF8D6E63), // Teddy Brown
        StrokeColor(0xFF5D4037), // Dark Chocolate
        StrokeColor(0xFF558B2F), // Moss Green
        StrokeColor(0xFF2E7D32), // Forest Green
        StrokeColor(0xFF006064), // Deep Sea
        StrokeColor(0xFF455A64), // Slate Grey
        StrokeColor(0xFF616161), // Classic Charcoal
        StrokeColor(0xFF212121)  // Classic Black
    )

    /**
     * 12 energetic vivid & neon shades.
     */
    val NeonColors: List<StrokeColor> = listOf(
        StrokeColor(0xFFFF3D00), // Neon Coral
        StrokeColor(0xFFFF1744), // Hot Red
        StrokeColor(0xFFF50057), // Neon Rose
        StrokeColor(0xFFD500F9), // Electric Purple
        StrokeColor(0xFF651FFF), // Electric Indigo
        StrokeColor(0xFF2979FF), // Bright Blue
        StrokeColor(0xFF00E5FF), // Electric Cyan
        StrokeColor(0xFF1DE9B6), // Neon Mint
        StrokeColor(0xFF76FF03), // Electric Lime
        StrokeColor(0xFFEEFF41), // Neon Lemon
        StrokeColor(0xFFFF9100), // Sunset Orange
        StrokeColor(0xFFFFD600)  // Gold Star
    )

    val AllCategories: List<ColorCategory> = listOf(
        ColorCategory(id = "rainbow", title = "Rainbow", icon = "🌈", colors = RainbowColors),
        ColorCategory(id = "pastel", title = "Pastels", icon = "🍬", colors = PastelColors),
        ColorCategory(id = "nature", title = "Nature", icon = "🌿", colors = NatureColors),
        ColorCategory(id = "neon", title = "Neon", icon = "⚡", colors = NeonColors)
    )

    /**
     * Converts HSV components into a pure [StrokeColor].
     *
     * @param hue 0.0f..360.0f
     * @param saturation 0.0f..1.0f
     * @param value 0.0f..1.0f (brightness)
     */
    fun hsvToStrokeColor(hue: Float, saturation: Float, value: Float): StrokeColor {
        val h = (hue % 360f + 360f) % 360f
        val s = saturation.coerceIn(0f, 1f)
        val v = value.coerceIn(0f, 1f)

        val c = v * s
        val x = c * (1f - abs((h / 60f) % 2f - 1f))
        val m = v - c

        val (r1, g1, b1) = when {
            h < 60f -> Triple(c, x, 0f)
            h < 120f -> Triple(x, c, 0f)
            h < 180f -> Triple(0f, c, x)
            h < 240f -> Triple(0f, x, c)
            h < 300f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }

        val r = ((r1 + m) * 255f).toInt().coerceIn(0, 255)
        val g = ((g1 + m) * 255f).toInt().coerceIn(0, 255)
        val b = ((b1 + m) * 255f).toInt().coerceIn(0, 255)
        val argb = (0xFFL shl 24) or (r.toLong() shl 16) or (g.toLong() shl 8) or b.toLong()
        return StrokeColor(argb)
    }

    /**
     * Converts a [StrokeColor] into its HSV components (Hue: 0..360, Saturation: 0..1, Value: 0..1).
     */
    fun strokeColorToHsv(strokeColor: StrokeColor): Triple<Float, Float, Float> {
        val r = ((strokeColor.argb shr 16) and 0xFF).toFloat() / 255f
        val g = ((strokeColor.argb shr 8) and 0xFF).toFloat() / 255f
        val b = (strokeColor.argb and 0xFF).toFloat() / 255f

        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val delta = max - min

        val v = max
        val s = if (max == 0f) 0f else delta / max
        val h = when {
            delta == 0f -> 0f
            max == r -> ((g - b) / delta % 6f) * 60f
            max == g -> (((b - r) / delta) + 2f) * 60f
            else -> (((r - g) / delta) + 4f) * 60f
        }.let { if (it < 0f) it + 360f else it }

        return Triple(h, s, v)
    }
}

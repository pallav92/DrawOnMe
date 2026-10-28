package com.draw.onme.domain.model

/**
 * Guesses a sensible Paint by Number target color for every region on a page that has no
 * hand-authored [ColoringRegion.targetColor]. Used as a fallback so every template page supports
 * Number Mode today, even pages whose colors haven't been curated yet.
 *
 * Keyword matches cover common recognizable parts (eyes, wheels, windows, foliage, ...), and are
 * intentionally shared across a page (e.g. every "eye" region is black). Everything else is
 * assigned by cycling through a curated palette in region order, so neighboring or nested
 * regions on the same page (like concentric rings on a body) reliably land on different numbers
 * instead of colliding by coincidence - the cycle only repeats once every region needing a
 * fallback has had a turn.
 */
internal object RegionColorHeuristics {

    // Deliberately small: only parts where two same-colored instances are always expected
    // (both eyes, all tires, all windows, ...). Thematic categories (leaf, cloud, water, ...)
    // are intentionally left to the round-robin fallback below, so they still read as "that
    // kind of thing" by their assigned color while not swallowing up multiple regions into one
    // number - every added part should earn its own number unless it's a true pair/set like this.
    private val KEYWORD_COLORS: List<Pair<List<String>, StrokeColor>> = listOf(
        listOf("eye", "pupil") to StrokeColor.Black,
        listOf("wheel", "tire", "tyre", "hoof") to StrokeColor(0xFF424242),
        listOf("rim", "hubcap") to StrokeColor(0xFFBDBDBD),
        listOf("window", "windshield", "glass", "visor", "goggle") to StrokeColor(0xFF81D4FA),
        listOf("tooth", "teeth", "fang", "claw", "bone") to StrokeColor(0xFFF5F5F5),
        listOf("tongue") to StrokeColor(0xFFE57373)
    )

    private val FALLBACK_PALETTE: List<StrokeColor> = listOf(
        StrokeColor(0xFFE53935), // Cherry Red
        StrokeColor(0xFFFB8C00), // Tangerine Orange
        StrokeColor(0xFFFDD835), // Sunny Yellow
        StrokeColor(0xFF43A047), // Grass Green
        StrokeColor(0xFF1E88E5), // Ocean Blue
        StrokeColor(0xFF8E24AA), // Magic Purple
        StrokeColor(0xFF00ACC1), // Sky Cyan
        StrokeColor(0xFFF06292), // Bubblegum Pink
        StrokeColor(0xFF8D6E63), // Teddy Brown
        StrokeColor(0xFF6D4C41)  // Chocolate Brown
    )

    // Only true mirror/pair markers - "top"/"bottom" is deliberately excluded since compass-style
    // parts (e.g. a flower's top/bottom/left/right petals) should usually still get distinct
    // numbers, not collapse into one just because they share a directional word.
    private val SIDE_MARKER_REGEX = Regex("(^|_)(left|right|l|r|front|rear)(_|\$)")

    private fun keywordColorFor(region: ColoringRegion): StrokeColor? {
        val haystack = "${region.id} ${region.label}".lowercase()
        return KEYWORD_COLORS.firstOrNull { (keywords, _) -> keywords.any { haystack.contains(it) } }?.second
    }

    /** Strips left/right/front/rear/etc. markers so symmetric parts share one fallback slot. */
    private fun symmetryKey(region: ColoringRegion): String =
        region.id.replace(SIDE_MARKER_REGEX, "_").trim('_').ifEmpty { region.id }

    /**
     * Every region's fallback color for [page], keyed by region id. Regions sharing a
     * [symmetryKey] (e.g. "ear_left"/"ear_right") get the same color; every other region needing
     * a fallback is assigned the next unused palette color in region order, so a page's first
     * ten distinct fallback-needing parts are guaranteed to be ten different numbers.
     */
    fun colorMapFor(page: ColoringPage): Map<String, StrokeColor> {
        val startOffset = ((page.id.hashCode() % FALLBACK_PALETTE.size) + FALLBACK_PALETTE.size) % FALLBACK_PALETTE.size
        val colorBySymmetryKey = mutableMapOf<String, StrokeColor>()
        var cursor = 0
        val result = LinkedHashMap<String, StrokeColor>(page.regions.size)

        for (region in page.regions) {
            val keywordColor = keywordColorFor(region)
            val color = when {
                keywordColor != null -> keywordColor
                else -> colorBySymmetryKey.getOrPut(symmetryKey(region)) {
                    val next = FALLBACK_PALETTE[(startOffset + cursor) % FALLBACK_PALETTE.size]
                    cursor++
                    next
                }
            }
            result[region.id] = color
        }
        return result
    }
}

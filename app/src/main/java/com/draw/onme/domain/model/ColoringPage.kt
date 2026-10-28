package com.draw.onme.domain.model

/**
 * Represents a single fillable vector region within a coloring page.
 * Coordinates in [boundaryPoints] are normalized to the [0.0f, 1.0f] range.
 *
 * @property id Unique identifier for this region within the page (e.g. "ear_left", "roof").
 * @property label Friendly descriptive label (e.g. "Left Ear", "Roof").
 * @property boundaryPoints Ordered normalized coordinates forming the closed perimeter.
 * @property defaultColor Optional initial placeholder color.
 * @property targetColor The single correct color for this region in a Paint by Number page.
 * When every region on a page defines one, the page becomes numbered (see [ColoringPage.isPaintByNumber]).
 */
data class ColoringRegion(
    val id: String,
    val label: String,
    val boundaryPoints: List<Point>,
    val defaultColor: StrokeColor? = null,
    val targetColor: StrokeColor? = null
)

/**
 * Represents a line-art outline path drawn over the coloring page.
 * Outlines provide bold black borders that stay crisp and visible above filled regions and crayon strokes.
 * Coordinates in [points] are normalized to the [0.0f, 1.0f] range.
 *
 * @property points Ordered normalized coordinates forming the outline segment.
 * @property strokeWidth Stroke width in points (normalized or scaled).
 * @property isClosed True if the outline loops back to its start point.
 */
data class ColoringOutline(
    val points: List<Point>,
    val strokeWidth: Float = 4f,
    val isClosed: Boolean = true
)

/**
 * Represents a complete coloring book page template.
 *
 * @property id Unique page identifier (e.g. "puppy", "fire_truck").
 * @property title Child-friendly title (e.g. "Happy Puppy").
 * @property category Themed album (e.g. "Animals & Pets", "Vehicles & Adventures").
 * @property iconEmoji Playful representative emoji.
 * @property difficulty Level indication ("Easy", "Medium").
 * @property regions Enclosed vector areas that can be filled or painted into.
 * @property outlines Bold line-art contours separating the regions.
 */
data class ColoringPage(
    val id: String,
    val title: String,
    val category: String,
    val iconEmoji: String,
    val difficulty: String,
    val regions: List<ColoringRegion>,
    val outlines: List<ColoringOutline>
) {
    /**
     * True when every region declares a hand-authored [ColoringRegion.targetColor].
     * Most pages rely on [effectiveTargetColor]'s heuristic fallback instead; this only tracks
     * pages whose numbers were deliberately chosen region by region.
     */
    val isPaintByNumber: Boolean
        get() = regions.isNotEmpty() && regions.all { it.targetColor != null }

    /**
     * Distinct hand-authored target colors in first-appearance order. Prefer [effectiveNumberedPalette]
     * for anything shown to the user, since most pages don't set [ColoringRegion.targetColor].
     */
    val numberedPalette: List<StrokeColor>
        get() = regions.mapNotNull { it.targetColor }.distinctBy { it.argb }

    /**
     * The color [region] should be filled with in Number Mode: its hand-authored [ColoringRegion.targetColor]
     * when set, otherwise a deterministic heuristic guess (see [RegionColorHeuristics]) so every
     * template page supports Number Mode, not just hand-authored ones.
     */
    fun effectiveTargetColor(region: ColoringRegion): StrokeColor =
        region.targetColor ?: RegionColorHeuristics.colorMapFor(this)[region.id] ?: StrokeColor.Black

    /**
     * The full numbered palette used in Number Mode: every region's [effectiveTargetColor],
     * deduplicated in first-appearance order and numbered 1-based by index here.
     */
    val effectiveNumberedPalette: List<StrokeColor>
        get() = regions.map { effectiveTargetColor(it) }.distinctBy { it.argb }
}

/**
 * Coloring mode for a template page: numbered (strict Paint by Number matching) or free
 * (pick any color, draw with crayon). Every template page supports both; the studio opens
 * in [NUMBER] by default and lets the user switch to [FREE].
 */
enum class ColoringMode {
    NUMBER,
    FREE
}

/**
 * Tools available in the Coloring Studio.
 */
enum class ColoringTool {
    /**
     * Tapping inside any closed area (enclosed by outlines or crayon strokes) instantly floods it with the selected color.
     */
    BUCKET,

    /**
     * Freehand crayon drawing anywhere across the canvas.
     */
    MAGIC_CRAYON,

    /**
     * Erases freehand crayon strokes within the touched area.
     */
    ERASER
}

/**
 * In-progress coloring draft snapshot for persistence and restoration.
 *
 * @property pageId The identifier of the page being colored.
 * @property fills Map of region ID to filled [StrokeColor].
 * @property strokes Freehand crayon or detail strokes drawn over the page.
 * @property customFills Flood-filled closed areas created by the bucket tool.
 */
data class ColoringDraft(
    val pageId: String,
    val fills: Map<String, StrokeColor> = emptyMap(),
    val strokes: List<Stroke> = emptyList(),
    val customFills: List<ClosedAreaFill> = emptyList()
)

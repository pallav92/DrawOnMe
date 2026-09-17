package com.pallav.drawonme.domain.model

/**
 * Represents a single vector path segment of a stencil template.
 * Coordinates in [points] are normalized to the [0.0f, 1.0f] range so they can
 * dynamically scale to any canvas size or screen aspect ratio.
 *
 * @property points Normalized (x, y) coordinates forming the path segment.
 * @property isClosed True if the path connects back to its start point (e.g. circles, loops).
 * @property label Optional friendly label for this part of the character (e.g. "Head", "Ears").
 */
data class StencilPath(
    val points: List<Point>,
    val isClosed: Boolean = false,
    val label: String? = null
)

/**
 * Represents a cartoon character stencil template for guided tracing.
 *
 * @property id Unique identifier.
 * @property title Name of the stencil (e.g. "Playful Mouse").
 * @property category Category (e.g. "Cartoons", "Animals").
 * @property description Short child-friendly description.
 * @property iconEmoji Playful emoji representation.
 * @property difficulty Level indication ("Easy", "Medium").
 * @property paths Vector outlines forming the character.
 */
data class Stencil(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val iconEmoji: String,
    val difficulty: String,
    val paths: List<StencilPath>
)

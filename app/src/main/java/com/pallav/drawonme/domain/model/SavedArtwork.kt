package com.pallav.drawonme.domain.model

import java.util.UUID

/**
 * Represents an artwork saved by the child or parent to the virtual refrigerator.
 *
 * @property id Unique identifier.
 * @property title Friendly title (e.g. "My Cozy House" or "Magic Doodle").
 * @property createdAt Epoch millisecond timestamp when the artwork was pinned.
 * @property strokes Immutable list of drawing strokes forming the artwork.
 * @property stencilId Optional ID of the stencil traced, if created in Stencil Studio.
 * @property magnetEmoji Fun magnet holding the artwork on the fridge (e.g. "⭐️", "🍎", "🚀", "🌸").
 */
data class SavedArtwork(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val strokes: List<Stroke>,
    val stencilId: String? = null,
    val magnetEmoji: String = DefaultMagnets.random()
) {
    companion object {
        val DefaultMagnets: List<String> = listOf("⭐️", "🍎", "🚀", "🌸", "🍌", "🌈", "🐱", "🎈")
    }
}

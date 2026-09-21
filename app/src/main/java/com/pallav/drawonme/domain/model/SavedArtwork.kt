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

        /**
         * Creates a [SavedArtwork] with strokes cropped tightly to their visible bounding box.
         * Normalizes stroke coordinates to origin (0, 0) plus padding, eliminating dead canvas space.
         */
        fun createCropped(
            title: String,
            strokes: List<Stroke>,
            stencilId: String? = null,
            id: String = UUID.randomUUID().toString(),
            createdAt: Long = System.currentTimeMillis(),
            magnetEmoji: String = DefaultMagnets.random(),
            padding: Float = ArtworkCropper.DEFAULT_PADDING
        ): SavedArtwork {
            val cropped = ArtworkCropper.cropStrokes(strokes, padding)
            return SavedArtwork(
                id = id,
                title = title,
                createdAt = createdAt,
                strokes = if (cropped.isNotEmpty()) cropped else strokes,
                stencilId = stencilId,
                magnetEmoji = magnetEmoji
            )
        }
    }
}

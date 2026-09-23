package com.draw.onme.domain.repository

import com.draw.onme.domain.model.SavedArtwork
import kotlinx.coroutines.flow.StateFlow

/**
 * Domain repository interface for preserving and managing children's saved artworks.
 */
interface ArtworkRepository {

    /**
     * Observes all saved artworks in reverse chronological order (newest first).
     */
    fun observeArtworks(): StateFlow<List<SavedArtwork>>

    /**
     * Preserves an artwork to the child's collection.
     */
    suspend fun saveArtwork(artwork: SavedArtwork)

    /**
     * Deletes an artwork by its unique ID.
     */
    suspend fun deleteArtwork(id: String)

    /**
     * Retrieves an artwork by its ID, or null if not found.
     */
    suspend fun getArtworkById(id: String): SavedArtwork?
}

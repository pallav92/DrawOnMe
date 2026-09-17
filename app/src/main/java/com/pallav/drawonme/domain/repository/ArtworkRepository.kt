package com.pallav.drawonme.domain.repository

import com.pallav.drawonme.domain.model.SavedArtwork
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository interface for preserving and managing children's saved artworks.
 */
interface ArtworkRepository {

    /**
     * Observes all saved artworks in reverse chronological order (newest first).
     */
    fun observeArtworks(): Flow<List<SavedArtwork>>

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

package com.pallav.drawonme.domain.repository

import com.pallav.drawonme.domain.model.Stroke

/**
 * Domain repository interface for preserving and managing active drawing board drafts.
 * Ensures that freehand scribble and individual stencil boards maintain isolated strokes
 * and can be safely restored across navigation and app restarts until cleared.
 */
interface BoardDraftRepository {

    /**
     * Retrieves the saved strokes for a specific board ID, or empty list if no draft exists.
     *
     * @param boardId Unique identifier for the drawing board (e.g. "magic_doodle", "stencil_cozy-house").
     */
    suspend fun getBoardStrokes(boardId: String): List<Stroke>

    /**
     * Saves the current strokes for a specific board ID.
     *
     * @param boardId Unique identifier for the drawing board.
     * @param strokes The immutable list of strokes to persist.
     */
    suspend fun saveBoardStrokes(boardId: String, strokes: List<Stroke>)

    /**
     * Clears any saved draft for a specific board ID.
     *
     * @param boardId Unique identifier for the drawing board.
     */
    suspend fun clearBoard(boardId: String)
}

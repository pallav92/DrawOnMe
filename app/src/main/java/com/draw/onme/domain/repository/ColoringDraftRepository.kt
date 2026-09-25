package com.draw.onme.domain.repository

import com.draw.onme.domain.model.ColoringDraft

/**
 * Repository interface for managing auto-saved coloring page drafts.
 * Ensures in-progress coloring survives app rotation, backgrounding, and process death.
 */
interface ColoringDraftRepository {

    /**
     * Retrieves the saved draft for [pageId], or null if none exists.
     */
    suspend fun getDraft(pageId: String): ColoringDraft?

    /**
     * Persists [draft] to storage.
     */
    suspend fun saveDraft(draft: ColoringDraft)

    /**
     * Removes the draft for [pageId].
     */
    suspend fun clearDraft(pageId: String)
}

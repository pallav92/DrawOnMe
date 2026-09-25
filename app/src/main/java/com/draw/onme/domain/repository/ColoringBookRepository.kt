package com.draw.onme.domain.repository

import com.draw.onme.domain.model.ColoringPage

/**
 * Repository interface providing available coloring book pages and categories.
 * Pure Kotlin contract with zero Android framework dependencies.
 */
interface ColoringBookRepository {

    /**
     * Returns all available coloring book pages across all categories.
     */
    fun getColoringPages(): List<ColoringPage>

    /**
     * Looks up a coloring page by its unique [id], or returns null if not found.
     */
    fun getPageById(id: String): ColoringPage?

    /**
     * Returns the ordered list of unique album category names.
     */
    fun getCategories(): List<String>

    /**
     * Returns all coloring pages belonging to [category].
     */
    fun getPagesByCategory(category: String): List<ColoringPage>
}

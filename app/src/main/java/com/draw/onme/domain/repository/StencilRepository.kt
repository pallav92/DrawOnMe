package com.draw.onme.domain.repository

import com.draw.onme.domain.model.Stencil

/**
 * Domain repository interface providing cartoon character stencil templates.
 */
interface StencilRepository {

    /**
     * Returns all available stencil templates.
     */
    fun getStencils(): List<Stencil>

    /**
     * Retrieves a stencil by its unique identifier, or null if not found.
     */
    fun getStencilById(id: String): Stencil?
}

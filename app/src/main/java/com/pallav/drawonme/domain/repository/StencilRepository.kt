package com.pallav.drawonme.domain.repository

import com.pallav.drawonme.domain.model.Stencil

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

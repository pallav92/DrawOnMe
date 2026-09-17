package com.pallav.drawonme.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InMemoryStencilRepositoryTest {

    private lateinit var repository: InMemoryStencilRepository

    @Before
    fun setUp() {
        repository = InMemoryStencilRepository()
    }

    @Test
    fun getStencils_returnsPredefinedCartoonCharacters() {
        val stencils = repository.getStencils()

        assertEquals(4, stencils.size)
        val ids = stencils.map { it.id }.toSet()
        assertTrue(ids.contains("playful_mouse"))
        assertTrue(ids.contains("cheerful_duck"))
        assertTrue(ids.contains("jungle_elephant"))
        assertTrue(ids.contains("happy_lion"))
    }

    @Test
    fun getStencils_allPointsNormalizedBetweenZeroAndOne() {
        val stencils = repository.getStencils()

        for (stencil in stencils) {
            assertTrue("Stencil ${stencil.id} must have paths", stencil.paths.isNotEmpty())

            for (path in stencil.paths) {
                assertTrue("Path in ${stencil.id} must have points", path.points.isNotEmpty())

                for (point in path.points) {
                    assertTrue(
                        "Point x (${point.x}) in ${stencil.id} must be in [0.0, 1.0]",
                        point.x in 0.0f..1.0f
                    )
                    assertTrue(
                        "Point y (${point.y}) in ${stencil.id} must be in [0.0, 1.0]",
                        point.y in 0.0f..1.0f
                    )
                }
            }
        }
    }

    @Test
    fun getStencilById_withKnownId_returnsStencil() {
        val mouse = repository.getStencilById("playful_mouse")
        assertNotNull(mouse)
        assertEquals("Playful Mouse", mouse?.title)
        assertEquals("🐭", mouse?.iconEmoji)

        val duck = repository.getStencilById("cheerful_duck")
        assertNotNull(duck)
        assertEquals("Cheerful Duck", duck?.title)
        assertEquals("🦆", duck?.iconEmoji)

        val elephant = repository.getStencilById("jungle_elephant")
        assertNotNull(elephant)
        assertEquals("Jungle Elephant", elephant?.title)
        assertEquals("🐘", elephant?.iconEmoji)

        val lion = repository.getStencilById("happy_lion")
        assertNotNull(lion)
        assertEquals("Happy Lion", lion?.title)
        assertEquals("🦁", lion?.iconEmoji)
    }

    @Test
    fun getStencilById_withUnknownId_returnsNull() {
        val unknown = repository.getStencilById("non_existent_character")
        assertNull(unknown)
    }
}

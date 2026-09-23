package com.draw.onme.data.repository

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
    fun getStencils_returnsPredefinedGeometricTemplates() {
        val stencils = repository.getStencils()

        assertEquals(8, stencils.size)
        val ids = stencils.map { it.id }.toSet()
        assertTrue(ids.contains("cozy_house"))
        assertTrue(ids.contains("zooming_car"))
        assertTrue(ids.contains("space_rocket"))
        assertTrue(ids.contains("happy_sailboat"))
        assertTrue(ids.contains("smiling_sun"))
        assertTrue(ids.contains("cute_teddy"))
        assertTrue(ids.contains("playful_kitty"))
        assertTrue(ids.contains("cartoon_mouse"))
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
        val house = repository.getStencilById("cozy_house")
        assertNotNull(house)
        assertEquals("Cozy House", house?.title)
        assertEquals("🏠", house?.iconEmoji)

        val car = repository.getStencilById("zooming_car")
        assertNotNull(car)
        assertEquals("Zooming Car", car?.title)
        assertEquals("🚗", car?.iconEmoji)

        val rocket = repository.getStencilById("space_rocket")
        assertNotNull(rocket)
        assertEquals("Space Rocket", rocket?.title)
        assertEquals("🚀", rocket?.iconEmoji)

        val sailboat = repository.getStencilById("happy_sailboat")
        assertNotNull(sailboat)
        assertEquals("Happy Sailboat", sailboat?.title)
        assertEquals("⛵", sailboat?.iconEmoji)

        val sun = repository.getStencilById("smiling_sun")
        assertNotNull(sun)
        assertEquals("Smiling Sun", sun?.title)
        assertEquals("☀️", sun?.iconEmoji)

        val teddy = repository.getStencilById("cute_teddy")
        assertNotNull(teddy)
        assertEquals("Cute Teddy Bear", teddy?.title)
        assertEquals("🧸", teddy?.iconEmoji)

        val kitty = repository.getStencilById("playful_kitty")
        assertNotNull(kitty)
        assertEquals("Playful Kitty", kitty?.title)
        assertEquals("🐱", kitty?.iconEmoji)

        val mouse = repository.getStencilById("cartoon_mouse")
        assertNotNull(mouse)
        assertEquals("Playful Mouse", mouse?.title)
        assertEquals("🐭", mouse?.iconEmoji)
    }

    @Test
    fun getStencilById_withUnknownId_returnsNull() {
        val unknown = repository.getStencilById("non_existent_template")
        assertNull(unknown)
    }
}

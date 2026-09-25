package com.draw.onme.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InMemoryColoringBookRepositoryTest {

    private lateinit var repository: InMemoryColoringBookRepository

    @Before
    fun setUp() {
        repository = InMemoryColoringBookRepository()
    }

    @Test
    fun testAllPages_loadedAndHaveUniqueIds() {
        val pages = repository.getColoringPages()
        // 17 pages: 1 blank canvas + 16 template pages
        assertEquals(17, pages.size)

        val uniqueIds = pages.map { it.id }.toSet()
        assertEquals(pages.size, uniqueIds.size)
    }

    @Test
    fun testCategories_containExpectedFiveAlbums() {
        val categories = repository.getCategories()
        assertEquals(5, categories.size)
        assertTrue(categories.contains(InMemoryColoringBookRepository.CATEGORY_CREATE))
        assertTrue(categories.contains("Animals & Pets"))
        assertTrue(categories.contains("Vehicles & Adventures"))
        assertTrue(categories.contains("Fairy Tale & Fantasy"))
        assertTrue(categories.contains("Sweet Food & Nature"))

        val blankPages = repository.getPagesByCategory(InMemoryColoringBookRepository.CATEGORY_CREATE)
        assertEquals(1, blankPages.size)

        for (category in categories.filter { it != InMemoryColoringBookRepository.CATEGORY_CREATE }) {
            val pagesInCategory = repository.getPagesByCategory(category)
            assertEquals(4, pagesInCategory.size)
        }
    }

    @Test
    fun testAllPageCoordinates_normalizedInRangeZeroToOne() {
        val pages = repository.getColoringPages().filter { it.id != InMemoryColoringBookRepository.BLANK_CANVAS_ID }
        for (page in pages) {
            assertTrue("Page ${page.id} must have regions", page.regions.isNotEmpty())
            assertTrue("Page ${page.id} must have outlines", page.outlines.isNotEmpty())

            for (region in page.regions) {
                assertTrue("Region ${region.id} in ${page.id} must have at least 3 vertices", region.boundaryPoints.size >= 3)
                for (p in region.boundaryPoints) {
                    assertTrue("Point x ${p.x} in ${region.id} out of bounds", p.x in 0f..1f)
                    assertTrue("Point y ${p.y} in ${region.id} out of bounds", p.y in 0f..1f)
                }
            }

            for (outline in page.outlines) {
                assertTrue("Outline in ${page.id} must have at least 2 points", outline.points.size >= 2)
                for (p in outline.points) {
                    assertTrue("Outline x ${p.x} in ${page.id} out of bounds", p.x in 0f..1f)
                    assertTrue("Outline y ${p.y} in ${page.id} out of bounds", p.y in 0f..1f)
                }
            }
        }
    }

    @Test
    fun testGetPageById_returnsCorrectPage() {
        val puppy = repository.getPageById("happy_puppy")
        assertNotNull(puppy)
        assertEquals("Happy Puppy", puppy?.title)
        assertEquals("Animals & Pets", puppy?.category)
    }

    @Test
    fun testBlankCanvas_existsAndRetrievable() {
        val blank = repository.getPageById(InMemoryColoringBookRepository.BLANK_CANVAS_ID)
        assertNotNull(blank)
        assertEquals("Blank Canvas", blank?.title)
        assertEquals(InMemoryColoringBookRepository.CATEGORY_CREATE, blank?.category)
        assertTrue(blank!!.regions.isEmpty())
        assertTrue(blank.outlines.isEmpty())
    }
}

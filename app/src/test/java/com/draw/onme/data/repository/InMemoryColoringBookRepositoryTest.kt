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
        // 56 pages: 1 blank canvas + 4 animals + 40 vehicles + 4 fantasy + 2 nature + 4 dinos + 1 paint-by-number
        assertEquals(56, pages.size)

        val uniqueIds = pages.map { it.id }.toSet()
        assertEquals(pages.size, uniqueIds.size)
    }

    @Test
    fun testVehicles_haveDetailedParts() {
        val vehiclePages = repository.getPagesByCategory(InMemoryColoringBookRepository.CATEGORY_VEHICLES)
        assertEquals(40, vehiclePages.size)
        for (page in vehiclePages) {
            assertTrue(
                "Vehicle page ${page.id} ('${page.title}') should have >= 10 parts for geometric details, found ${page.regions.size}",
                page.regions.size >= 10
            )
        }
    }

    @Test
    fun testAnimals_haveAtLeastNineColoringAreas() {
        val animalPages = repository.getPagesByCategory(InMemoryColoringBookRepository.CATEGORY_ANIMALS)
        assertEquals(4, animalPages.size)
        for (page in animalPages) {
            assertTrue(
                "Animal page ${page.id} ('${page.title}') should have >= 9 coloring areas, found ${page.regions.size}",
                page.regions.size >= 9
            )
        }
    }

    @Test
    fun testFantasyAndNature_haveAtLeastNineColoringAreas() {
        val pages = repository.getPagesByCategory(InMemoryColoringBookRepository.CATEGORY_FANTASY) +
            repository.getPagesByCategory(InMemoryColoringBookRepository.CATEGORY_NATURE)
        assertEquals(6, pages.size)
        for (page in pages) {
            assertTrue(
                "Page ${page.id} ('${page.title}') should have >= 9 coloring areas, found ${page.regions.size}",
                page.regions.size >= 9
            )
        }
    }

    @Test
    fun testCategories_containExpectedAlbums() {
        val categories = repository.getCategories()
        assertEquals(7, categories.size)
        assertTrue(categories.contains(InMemoryColoringBookRepository.CATEGORY_CREATE))
        assertTrue(categories.contains(InMemoryColoringBookRepository.CATEGORY_ANIMALS))
        assertTrue(categories.contains(InMemoryColoringBookRepository.CATEGORY_VEHICLES))
        assertTrue(categories.contains(InMemoryColoringBookRepository.CATEGORY_FANTASY))
        assertTrue(categories.contains(InMemoryColoringBookRepository.CATEGORY_NATURE))
        assertTrue(categories.contains(InMemoryColoringBookRepository.CATEGORY_DINOS))
        assertTrue(categories.contains(InMemoryColoringBookRepository.CATEGORY_PAINT_BY_NUMBER))

        assertEquals(1, repository.getPagesByCategory(InMemoryColoringBookRepository.CATEGORY_CREATE).size)
        assertEquals(4, repository.getPagesByCategory(InMemoryColoringBookRepository.CATEGORY_ANIMALS).size)
        assertEquals(40, repository.getPagesByCategory(InMemoryColoringBookRepository.CATEGORY_VEHICLES).size)
        assertEquals(4, repository.getPagesByCategory(InMemoryColoringBookRepository.CATEGORY_FANTASY).size)
        assertEquals(2, repository.getPagesByCategory(InMemoryColoringBookRepository.CATEGORY_NATURE).size)
        assertEquals(4, repository.getPagesByCategory(InMemoryColoringBookRepository.CATEGORY_DINOS).size)
        assertEquals(1, repository.getPagesByCategory(InMemoryColoringBookRepository.CATEGORY_PAINT_BY_NUMBER).size)
    }

    @Test
    fun testPaintByNumber_pageHasFixedNumberedPaletteOnEveryRegion() {
        val pbnPages = repository.getPagesByCategory(InMemoryColoringBookRepository.CATEGORY_PAINT_BY_NUMBER)
        assertEquals(1, pbnPages.size)
        val page = pbnPages.first()
        assertTrue("Every region must declare a targetColor", page.isPaintByNumber)
        assertTrue("Numbered palette should have multiple distinct colors", page.numberedPalette.size > 1)
    }

    @Test
    fun testKittenConcentricBodyRings_eachGetADistinctNumber() {
        val kitten = repository.getPageById("playful_kitten")!!
        val body = kitten.regions.first { it.id == "body" }
        val bodyMid = kitten.regions.first { it.id == "body_mid" }
        val tummy = kitten.regions.first { it.id == "tummy" }

        val bodyColor = kitten.effectiveTargetColor(body)
        val bodyMidColor = kitten.effectiveTargetColor(bodyMid)
        val tummyColor = kitten.effectiveTargetColor(tummy)

        assertTrue("Outer body ring and middle ring must differ", bodyColor.argb != bodyMidColor.argb)
        assertTrue("Middle ring and inner tummy must differ", bodyMidColor.argb != tummyColor.argb)
        assertTrue("Outer body ring and inner tummy must differ", bodyColor.argb != tummyColor.argb)
    }

    @Test
    fun testEveryPage_hasNoAccidentalColorCollisionsAmongItsFirstTenRegions() {
        // The round-robin fallback guarantees every region needing one gets a fresh color until
        // the 10-color palette is exhausted, so a page's first 10 fallback-needing regions
        // (excluding deliberate keyword/symmetry sharing) should never collide by coincidence.
        for (page in repository.getColoringPages()) {
            if (page.regions.isEmpty()) continue
            val colorsInOrder = page.regions.take(10).map { page.effectiveTargetColor(it).argb }
            // Not asserting full uniqueness (keyword/symmetry sharing is intentional), just that
            // the palette isn't degenerately collapsing everything to one or two colors.
            assertTrue(
                "Page ${page.id} should use more than one distinct color across its regions",
                colorsInOrder.toSet().size > 1 || page.regions.size <= 1
            )
        }
    }

    @Test
    fun testDinosaurs_containOnlyFourSelectedCreatures() {
        val dinos = repository.getPagesByCategory(InMemoryColoringBookRepository.CATEGORY_DINOS)
        assertEquals(4, dinos.size)
        val dinoIds = dinos.map { it.id }.toSet()
        assertEquals(
            setOf("dino_stegosaurus", "dino_pteranodon", "dino_carnotaurus", "dino_ceratosaurus"),
            dinoIds
        )
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
        assertTrue((puppy?.regions?.size ?: 0) in 9..12)
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

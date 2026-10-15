package com.draw.onme.data.repository.catalog

import com.draw.onme.data.repository.InMemoryColoringBookRepository.Companion.CATEGORY_DINOS
import com.draw.onme.data.repository.catalog.ColoringPageGenerators.circlePoints
import com.draw.onme.data.repository.catalog.ColoringPageGenerators.createPage
import com.draw.onme.data.repository.catalog.ColoringPageGenerators.diamondPoints
import com.draw.onme.data.repository.catalog.ColoringPageGenerators.ovalPoints
import com.draw.onme.data.repository.catalog.ColoringPageGenerators.rectPoints
import com.draw.onme.data.repository.catalog.ColoringPageGenerators.trapezoidPoints
import com.draw.onme.data.repository.catalog.ColoringPageGenerators.trianglePoints
import com.draw.onme.domain.model.ColoringPage
import com.draw.onme.domain.model.ColoringRegion

/**
 * Curated dinosaurs and prehistoric creatures retaining distinct, recognizable shapes.
 */
internal val DinoColoringPages: List<ColoringPage> = listOf(
    // 1. PLATED STEGOSAURUS
    createPage("dino_stegosaurus", "Plated Stegosaurus", CATEGORY_DINOS, "🦕", "Medium", listOf(
        ColoringRegion("ground_moss", "Jurassic Moss Bed", rectPoints(0.04f, 0.86f, 0.96f, 0.94f)),
        ColoringRegion("stego_body", "Arched Herbivore Body", ovalPoints(0.48f, 0.58f, 0.24f, 0.15f)),
        ColoringRegion("stego_head", "Slender Beaked Head", ovalPoints(0.18f, 0.64f, 0.07f, 0.05f)),
        ColoringRegion("stego_eye", "Stego Kind Eye", circlePoints(0.18f, 0.63f, 0.015f)),
        ColoringRegion("stego_neck", "Flexible Neck", trapezoidPoints(0.22f, 0.30f, 0.60f, 0.24f, 0.32f, 0.68f)),
        ColoringRegion("plate_1", "Dorsal Plate Front", diamondPoints(0.30f, 0.42f, 0.045f, 0.08f)),
        ColoringRegion("plate_2", "Dorsal Plate Crest 1", diamondPoints(0.40f, 0.36f, 0.055f, 0.10f)),
        ColoringRegion("plate_3", "Dorsal Plate Crest 2", diamondPoints(0.52f, 0.34f, 0.060f, 0.11f)),
        ColoringRegion("plate_4", "Dorsal Plate Rear 1", diamondPoints(0.64f, 0.38f, 0.055f, 0.09f)),
        ColoringRegion("plate_5", "Dorsal Plate Rear 2", diamondPoints(0.74f, 0.44f, 0.045f, 0.07f)),
        ColoringRegion("stego_tail", "Muscular Tail", trianglePoints(0.68f, 0.54f, 0.90f, 0.62f, 0.70f, 0.68f)),
        ColoringRegion("thagomizer_1", "Thagomizer Spike 1", trianglePoints(0.86f, 0.56f, 0.92f, 0.48f, 0.88f, 0.60f)),
        ColoringRegion("thagomizer_2", "Thagomizer Spike 2", trianglePoints(0.90f, 0.58f, 0.96f, 0.50f, 0.92f, 0.62f)),
        ColoringRegion("stego_foreleg_l", "Pillar Foreleg Front", rectPoints(0.32f, 0.68f, 0.38f, 0.86f)),
        ColoringRegion("stego_foreleg_r", "Pillar Foreleg Back", rectPoints(0.40f, 0.70f, 0.46f, 0.86f)),
        ColoringRegion("stego_hindleg_l", "Pillar Hindleg Front", rectPoints(0.56f, 0.68f, 0.64f, 0.86f)),
        ColoringRegion("stego_hindleg_r", "Pillar Hindleg Back", rectPoints(0.66f, 0.70f, 0.72f, 0.86f))
    )),

    // 2. SOARING PTERANODON
    createPage("dino_pteranodon", "Soaring Pteranodon", CATEGORY_DINOS, "🦅", "Medium", listOf(
        ColoringRegion("sky_cloud_1", "Stratosphere Cloud L", ovalPoints(0.16f, 0.16f, 0.12f, 0.05f)),
        ColoringRegion("sky_cloud_2", "Stratosphere Cloud R", ovalPoints(0.84f, 0.18f, 0.12f, 0.05f)),
        ColoringRegion("ptera_body", "Furry Pterosaur Torso", ovalPoints(0.50f, 0.54f, 0.08f, 0.12f)),
        ColoringRegion("ptera_head", "Pterosaur Skull Base", circlePoints(0.50f, 0.38f, 0.045f)),
        ColoringRegion("ptera_crest", "Swept Aerodynamic Crest", trianglePoints(0.50f, 0.35f, 0.54f, 0.39f, 0.68f, 0.22f)),
        ColoringRegion("ptera_beak_upper", "Spear Jaws Upper", trianglePoints(0.48f, 0.36f, 0.50f, 0.40f, 0.32f, 0.38f)),
        ColoringRegion("ptera_beak_lower", "Spear Jaws Lower", trianglePoints(0.48f, 0.40f, 0.50f, 0.42f, 0.36f, 0.42f)),
        ColoringRegion("ptera_eye", "Sharp Sky Eye", circlePoints(0.48f, 0.38f, 0.012f)),
        ColoringRegion("wing_spar_l", "Long Finger Spar L", trianglePoints(0.48f, 0.48f, 0.14f, 0.44f, 0.12f, 0.48f)),
        ColoringRegion("wing_membrane_l", "Vast Leathery Patagium L", trianglePoints(0.12f, 0.48f, 0.48f, 0.56f, 0.30f, 0.68f)),
        ColoringRegion("wing_spar_r", "Long Finger Spar R", trianglePoints(0.52f, 0.48f, 0.86f, 0.44f, 0.88f, 0.48f)),
        ColoringRegion("wing_membrane_r", "Vast Leathery Patagium R", trianglePoints(0.88f, 0.48f, 0.52f, 0.56f, 0.70f, 0.68f)),
        ColoringRegion("claw_hand_l", "Wing Claw Hook L", circlePoints(0.14f, 0.46f, 0.02f)),
        ColoringRegion("claw_hand_r", "Wing Claw Hook R", circlePoints(0.86f, 0.46f, 0.02f)),
        ColoringRegion("ptera_leg_l", "Perching Foot L", rectPoints(0.44f, 0.64f, 0.48f, 0.74f)),
        ColoringRegion("ptera_leg_r", "Perching Foot R", rectPoints(0.52f, 0.64f, 0.56f, 0.74f))
    )),

    // 3. BULL HORN CARNOTAURUS
    createPage("dino_carnotaurus", "Bull Horn Carnotaurus", CATEGORY_DINOS, "🐂", "Medium", listOf(
        ColoringRegion("arid_dirt", "Patagonian Dust Plain", rectPoints(0.04f, 0.86f, 0.96f, 0.94f)),
        ColoringRegion("carno_body", "Deep-Chested Hunter Body", ovalPoints(0.50f, 0.54f, 0.20f, 0.15f)),
        ColoringRegion("carno_neck", "Thick Muscular Neck", trapezoidPoints(0.32f, 0.38f, 0.42f, 0.42f, 0.44f, 0.52f)),
        ColoringRegion("carno_head", "Deep Bulldog Skull", rectPoints(0.18f, 0.28f, 0.34f, 0.42f)),
        ColoringRegion("bull_horn_l", "Sharp Bull Horn L", trianglePoints(0.24f, 0.28f, 0.28f, 0.28f, 0.22f, 0.16f)),
        ColoringRegion("bull_horn_r", "Sharp Bull Horn R", trianglePoints(0.28f, 0.28f, 0.32f, 0.28f, 0.28f, 0.16f)),
        ColoringRegion("carno_snout", "Blunt Snapping Snout", rectPoints(0.12f, 0.32f, 0.18f, 0.40f)),
        ColoringRegion("carno_eye", "Binocular Red Eye", circlePoints(0.26f, 0.33f, 0.015f)),
        ColoringRegion("tiny_arm", "Vestigial Four-Fingered Arm", circlePoints(0.38f, 0.54f, 0.03f)),
        ColoringRegion("speed_thigh", "High-Speed Running Thigh", ovalPoints(0.54f, 0.64f, 0.10f, 0.13f)),
        ColoringRegion("speed_shin", "Slender Sprint Metatarsus", rectPoints(0.52f, 0.74f, 0.58f, 0.86f)),
        ColoringRegion("carno_tail", "Caudofemoralis Power Tail", trianglePoints(0.68f, 0.48f, 0.96f, 0.54f, 0.68f, 0.62f)),
        ColoringRegion("pebble_scales_1", "Osteoderm Row 1", circlePoints(0.44f, 0.48f, 0.02f)),
        ColoringRegion("pebble_scales_2", "Osteoderm Row 2", circlePoints(0.54f, 0.46f, 0.02f)),
        ColoringRegion("pebble_scales_3", "Osteoderm Row 3", circlePoints(0.64f, 0.46f, 0.02f)),
        ColoringRegion("arid_shrub", "Thorny Scrub Bush", ovalPoints(0.84f, 0.82f, 0.07f, 0.04f))
    )),

    // 4. NOSE-HORN CERATOSAURUS
    createPage("dino_ceratosaurus", "Nose-Horn Ceratosaurus", CATEGORY_DINOS, "🦏", "Medium", listOf(
        ColoringRegion("mud_trail", "Jurassic River Mud Trail", rectPoints(0.04f, 0.86f, 0.96f, 0.94f)),
        ColoringRegion("cerato_body", "Deep-Chested Hunter Torso", ovalPoints(0.50f, 0.54f, 0.20f, 0.15f)),
        ColoringRegion("cerato_neck", "Stout Muscular Neck", trapezoidPoints(0.32f, 0.36f, 0.42f, 0.40f, 0.44f, 0.50f)),
        ColoringRegion("nasal_horn", "Prominent Blade Nasal Horn", trianglePoints(0.16f, 0.28f, 0.22f, 0.28f, 0.18f, 0.16f)),
        ColoringRegion("brow_horn_l", "Left Lacrimal Eye Horn", trianglePoints(0.26f, 0.28f, 0.30f, 0.28f, 0.28f, 0.20f)),
        ColoringRegion("cerato_head", "Blade-Toothed Skull", rectPoints(0.16f, 0.28f, 0.34f, 0.40f)),
        ColoringRegion("long_blade_teeth", "Elongated Dagger Teeth", trianglePoints(0.18f, 0.38f, 0.22f, 0.38f, 0.20f, 0.45f)),
        ColoringRegion("cerato_eye", "Piercing Hunter Eye", circlePoints(0.28f, 0.32f, 0.015f)),
        ColoringRegion("midline_scute_1", "Dorsal Bony Osteoderm 1", trianglePoints(0.42f, 0.44f, 0.46f, 0.44f, 0.44f, 0.40f)),
        ColoringRegion("midline_scute_2", "Dorsal Bony Osteoderm 2", trianglePoints(0.52f, 0.44f, 0.56f, 0.44f, 0.54f, 0.40f)),
        ColoringRegion("midline_scute_3", "Dorsal Bony Osteoderm 3", trianglePoints(0.62f, 0.44f, 0.66f, 0.44f, 0.64f, 0.40f)),
        ColoringRegion("cerato_arm", "Four-Fingered Forelimb", rectPoints(0.36f, 0.52f, 0.42f, 0.64f)),
        ColoringRegion("cerato_thigh", "Powerful Bipedal Thigh", ovalPoints(0.54f, 0.64f, 0.10f, 0.13f)),
        ColoringRegion("cerato_foot", "Heavy Three-Toed Foot", trapezoidPoints(0.50f, 0.58f, 0.78f, 0.46f, 0.60f, 0.86f)),
        ColoringRegion("swimming_tail", "Deep Flexible Swimming Tail", trianglePoints(0.68f, 0.48f, 0.96f, 0.54f, 0.68f, 0.62f)),
        ColoringRegion("river_reeds", "Horsetail River Reeds", rectPoints(0.86f, 0.74f, 0.88f, 0.86f))
    ))
)

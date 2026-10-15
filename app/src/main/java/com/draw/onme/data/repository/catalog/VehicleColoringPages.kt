package com.draw.onme.data.repository.catalog

import com.draw.onme.data.repository.InMemoryColoringBookRepository.Companion.CATEGORY_VEHICLES
import com.draw.onme.data.repository.catalog.ColoringPageGenerators.circlePoints
import com.draw.onme.data.repository.catalog.ColoringPageGenerators.createPage
import com.draw.onme.data.repository.catalog.ColoringPageGenerators.diamondPoints
import com.draw.onme.data.repository.catalog.ColoringPageGenerators.fanPoints
import com.draw.onme.data.repository.catalog.ColoringPageGenerators.ovalPoints
import com.draw.onme.data.repository.catalog.ColoringPageGenerators.rectPoints
import com.draw.onme.data.repository.catalog.ColoringPageGenerators.starPoints
import com.draw.onme.data.repository.catalog.ColoringPageGenerators.trapezoidPoints
import com.draw.onme.data.repository.catalog.ColoringPageGenerators.trianglePoints
import com.draw.onme.domain.model.ColoringPage
import com.draw.onme.domain.model.ColoringRegion
import com.draw.onme.domain.model.Point

/**
 * 40 uniquely shaped, mechanically distinct vehicles, aircraft, watercraft, and space vessels.
 * Every vehicle has its own distinct silhouette and over 15 colorable regions.
 */
internal val VehicleColoringPages: List<ColoringPage> = listOf(
    // 1. FORMULA RACE CAR
    createPage("vehicle_race_car", "Formula Racer", CATEGORY_VEHICLES, "🏎️", "Easy", listOf(
        ColoringRegion("track", "Racetrack Asphalt", rectPoints(0.04f, 0.78f, 0.96f, 0.88f)),
        ColoringRegion("curb", "Checkered Rumble Strip", rectPoints(0.04f, 0.88f, 0.96f, 0.92f)),
        ColoringRegion("chassis", "Aerodynamic Monocoque", listOf(Point(0.16f, 0.58f), Point(0.86f, 0.58f), Point(0.88f, 0.68f), Point(0.12f, 0.68f))),
        ColoringRegion("cockpit", "Driver Cockpit", ovalPoints(0.50f, 0.52f, 0.12f, 0.08f)),
        ColoringRegion("driver_helmet", "Racer Helmet", circlePoints(0.50f, 0.48f, 0.045f)),
        ColoringRegion("helmet_visor", "Helmet Visor", ovalPoints(0.52f, 0.48f, 0.025f, 0.015f)),
        ColoringRegion("front_wing", "Front Downforce Wing", rectPoints(0.84f, 0.64f, 0.94f, 0.72f)),
        ColoringRegion("rear_wing", "High Rear Wing", rectPoints(0.08f, 0.42f, 0.22f, 0.48f)),
        ColoringRegion("wing_pylon", "Wing Support Pylon", rectPoints(0.14f, 0.48f, 0.18f, 0.58f)),
        ColoringRegion("side_stripe", "Racing Speed Stripe", rectPoints(0.24f, 0.60f, 0.78f, 0.64f)),
        ColoringRegion("number_circle", "Race Number Disk", circlePoints(0.60f, 0.62f, 0.035f)),
        ColoringRegion("wheel_front", "Front Racing Tire", circlePoints(0.76f, 0.68f, 0.10f)),
        ColoringRegion("wheel_rear", "Rear Wide Tire", circlePoints(0.26f, 0.68f, 0.11f)),
        ColoringRegion("rim_front", "Front Alloy Rim", circlePoints(0.76f, 0.68f, 0.05f)),
        ColoringRegion("rim_rear", "Rear Alloy Rim", circlePoints(0.26f, 0.68f, 0.055f)),
        ColoringRegion("exhaust_flame", "Turbo Exhaust Flame", trianglePoints(0.08f, 0.62f, 0.02f, 0.60f, 0.08f, 0.66f))
    )),

    // 2. LONDON DOUBLE DECKER BUS
    createPage("vehicle_double_decker", "Double Decker Bus", CATEGORY_VEHICLES, "🚌", "Medium", listOf(
        ColoringRegion("street", "Cobblestone Street", rectPoints(0.05f, 0.82f, 0.95f, 0.92f)),
        ColoringRegion("bus_body", "Iconic Red Bus Body", rectPoints(0.14f, 0.28f, 0.86f, 0.76f)),
        ColoringRegion("roof_cap", "Curved Bus Roof", ovalPoints(0.50f, 0.28f, 0.36f, 0.05f)),
        ColoringRegion("upper_window_1", "Upper Window Front", rectPoints(0.20f, 0.34f, 0.32f, 0.46f)),
        ColoringRegion("upper_window_2", "Upper Window Mid L", rectPoints(0.36f, 0.34f, 0.48f, 0.46f)),
        ColoringRegion("upper_window_3", "Upper Window Mid R", rectPoints(0.52f, 0.34f, 0.64f, 0.46f)),
        ColoringRegion("upper_window_4", "Upper Window Rear", rectPoints(0.68f, 0.34f, 0.80f, 0.46f)),
        ColoringRegion("route_banner", "Destination Sign", rectPoints(0.26f, 0.48f, 0.74f, 0.53f)),
        ColoringRegion("lower_window_1", "Lower Window 1", rectPoints(0.20f, 0.55f, 0.34f, 0.66f)),
        ColoringRegion("lower_window_2", "Lower Window 2", rectPoints(0.38f, 0.55f, 0.52f, 0.66f)),
        ColoringRegion("lower_window_3", "Lower Window 3", rectPoints(0.56f, 0.55f, 0.70f, 0.66f)),
        ColoringRegion("front_door", "Passenger Door", rectPoints(0.74f, 0.55f, 0.84f, 0.74f)),
        ColoringRegion("wheel_front", "Front Heavy Tire", circlePoints(0.72f, 0.76f, 0.09f)),
        ColoringRegion("wheel_rear", "Rear Heavy Tire", circlePoints(0.28f, 0.76f, 0.09f)),
        ColoringRegion("rim_front", "Front Chrome Hub", circlePoints(0.72f, 0.76f, 0.045f)),
        ColoringRegion("rim_rear", "Rear Chrome Hub", circlePoints(0.28f, 0.76f, 0.045f)),
        ColoringRegion("headlight", "Headlamp", circlePoints(0.86f, 0.70f, 0.025f))
    )),

    // 3. BRAVE FIRE TRUCK
    createPage("vehicle_fire_truck", "Hero Fire Engine", CATEGORY_VEHICLES, "🚒", "Medium", listOf(
        ColoringRegion("road", "City Roadway", rectPoints(0.04f, 0.80f, 0.96f, 0.90f)),
        ColoringRegion("turntable_ladder", "Rescue Ladder", rectPoints(0.18f, 0.28f, 0.68f, 0.36f)),
        ColoringRegion("ladder_rungs", "Ladder Rungs Inner", rectPoints(0.20f, 0.30f, 0.66f, 0.34f)),
        ColoringRegion("ladder_base", "Hydraulic Turntable", circlePoints(0.28f, 0.40f, 0.05f)),
        ColoringRegion("cab", "Forward Driver Cab", rectPoints(0.62f, 0.42f, 0.88f, 0.74f)),
        ColoringRegion("windshield", "Cab Windshield", rectPoints(0.72f, 0.46f, 0.86f, 0.58f)),
        ColoringRegion("side_window", "Cab Side Window", rectPoints(0.64f, 0.46f, 0.70f, 0.58f)),
        ColoringRegion("siren_l", "Emergency Siren Blue", circlePoints(0.74f, 0.38f, 0.025f)),
        ColoringRegion("siren_r", "Emergency Siren Red", circlePoints(0.82f, 0.38f, 0.025f)),
        ColoringRegion("equipment_body", "Pump & Equipment Bay", rectPoints(0.12f, 0.44f, 0.62f, 0.74f)),
        ColoringRegion("hose_reel", "Water Hose Reel", circlePoints(0.48f, 0.54f, 0.06f)),
        ColoringRegion("side_stripe", "Reflective Safety Stripe", rectPoints(0.12f, 0.62f, 0.88f, 0.67f)),
        ColoringRegion("wheel_front", "Front Tire", circlePoints(0.76f, 0.74f, 0.09f)),
        ColoringRegion("wheel_rear_1", "Rear Dual Tire 1", circlePoints(0.24f, 0.74f, 0.09f)),
        ColoringRegion("wheel_rear_2", "Rear Dual Tire 2", circlePoints(0.42f, 0.74f, 0.09f)),
        ColoringRegion("bumper", "Chrome Push Bumper", rectPoints(0.88f, 0.66f, 0.94f, 0.74f))
    )),

    // 4. STEAM TRAIN LOCOMOTIVE
    createPage("vehicle_steam_train", "Classic Steam Train", CATEGORY_VEHICLES, "🚂", "Hard", listOf(
        ColoringRegion("rails", "Iron Railroad Tracks", rectPoints(0.04f, 0.84f, 0.96f, 0.92f)),
        ColoringRegion("cowcatcher", "Front Cowcatcher Pilot", trianglePoints(0.82f, 0.80f, 0.94f, 0.80f, 0.88f, 0.72f)),
        ColoringRegion("boiler", "Cylindrical Steam Boiler", rectPoints(0.38f, 0.50f, 0.82f, 0.70f)),
        ColoringRegion("boiler_band_1", "Brass Boiler Band 1", rectPoints(0.50f, 0.50f, 0.53f, 0.70f)),
        ColoringRegion("boiler_band_2", "Brass Boiler Band 2", rectPoints(0.68f, 0.50f, 0.71f, 0.70f)),
        ColoringRegion("smokestack", "Fluted Chimney Stack", trapezoidPoints(0.72f, 0.80f, 0.36f, 0.74f, 0.78f, 0.50f)),
        ColoringRegion("steam_dome", "Steam Dome Chamber", circlePoints(0.58f, 0.46f, 0.05f)),
        ColoringRegion("smoke_puff_1", "Smoke Cloud Billow 1", circlePoints(0.68f, 0.28f, 0.06f)),
        ColoringRegion("smoke_puff_2", "Smoke Cloud Billow 2", circlePoints(0.56f, 0.20f, 0.08f)),
        ColoringRegion("smoke_puff_3", "Smoke Cloud Billow 3", circlePoints(0.40f, 0.14f, 0.10f)),
        ColoringRegion("cab", "Engineer Cabin", rectPoints(0.16f, 0.40f, 0.38f, 0.70f)),
        ColoringRegion("cab_roof", "Curved Cab Roof", rectPoints(0.14f, 0.38f, 0.40f, 0.42f)),
        ColoringRegion("cab_window", "Engineer Window", rectPoints(0.22f, 0.46f, 0.32f, 0.56f)),
        ColoringRegion("headlamp", "Big Golden Headlamp", circlePoints(0.86f, 0.54f, 0.04f)),
        ColoringRegion("drive_wheel_1", "Large Drive Wheel 1", circlePoints(0.32f, 0.74f, 0.11f)),
        ColoringRegion("drive_wheel_2", "Large Drive Wheel 2", circlePoints(0.56f, 0.74f, 0.11f)),
        ColoringRegion("front_truck_wheel", "Front Guide Wheel", circlePoints(0.78f, 0.77f, 0.065f)),
        ColoringRegion("connecting_rod", "Piston Connecting Rod", rectPoints(0.32f, 0.73f, 0.68f, 0.76f))
    )),

    // 5. YELLOW SCHOOL BUS
    createPage("vehicle_school_bus", "Yellow School Bus", CATEGORY_VEHICLES, "🚌", "Easy", listOf(
        ColoringRegion("road", "School Crossing", rectPoints(0.04f, 0.80f, 0.96f, 0.90f)),
        ColoringRegion("bus_body", "Yellow Body Shell", rectPoints(0.10f, 0.36f, 0.88f, 0.72f)),
        ColoringRegion("hood", "Forward Engine Hood", rectPoints(0.74f, 0.52f, 0.90f, 0.72f)),
        ColoringRegion("windshield", "Driver Windshield", rectPoints(0.68f, 0.40f, 0.76f, 0.52f)),
        ColoringRegion("window_1", "Pupil Window 1", rectPoints(0.16f, 0.42f, 0.26f, 0.52f)),
        ColoringRegion("window_2", "Pupil Window 2", rectPoints(0.29f, 0.42f, 0.39f, 0.52f)),
        ColoringRegion("window_3", "Pupil Window 3", rectPoints(0.42f, 0.42f, 0.52f, 0.52f)),
        ColoringRegion("window_4", "Pupil Window 4", rectPoints(0.55f, 0.42f, 0.65f, 0.52f)),
        ColoringRegion("rub_rail_top", "Black Rub Rail Top", rectPoints(0.10f, 0.56f, 0.74f, 0.59f)),
        ColoringRegion("rub_rail_bot", "Black Rub Rail Bot", rectPoints(0.10f, 0.64f, 0.74f, 0.67f)),
        ColoringRegion("stop_sign", "Stop Sign Arm", circlePoints(0.34f, 0.61f, 0.035f)),
        ColoringRegion("front_grille", "Black Radiator Grille", rectPoints(0.86f, 0.56f, 0.90f, 0.68f)),
        ColoringRegion("headlight", "School Bus Headlight", circlePoints(0.88f, 0.56f, 0.025f)),
        ColoringRegion("wheel_rear", "Rear Wheel", circlePoints(0.26f, 0.74f, 0.09f)),
        ColoringRegion("wheel_front", "Front Wheel", circlePoints(0.74f, 0.74f, 0.09f)),
        ColoringRegion("rim_rear", "Rear Rim", circlePoints(0.26f, 0.74f, 0.045f)),
        ColoringRegion("rim_front", "Front Rim", circlePoints(0.74f, 0.74f, 0.045f))
    )),

    // 6. PATROL POLICE CAR
    createPage("vehicle_police_car", "Patrol Police Cruiser", CATEGORY_VEHICLES, "🚓", "Easy", listOf(
        ColoringRegion("highway", "Highway Asphalt", rectPoints(0.04f, 0.78f, 0.96f, 0.88f)),
        ColoringRegion("body_lower", "Lower Black Chassis", rectPoints(0.12f, 0.58f, 0.88f, 0.72f)),
        ColoringRegion("door_white", "White Door Section", rectPoints(0.38f, 0.52f, 0.64f, 0.68f)),
        ColoringRegion("police_star", "Gold Police Badge", starPoints(0.51f, 0.60f, 0.035f, 0.018f)),
        ColoringRegion("cabin_roof", "Cabin Roof", listOf(Point(0.30f, 0.52f), Point(0.40f, 0.38f), Point(0.70f, 0.38f), Point(0.78f, 0.52f))),
        ColoringRegion("windshield", "Front Windshield", listOf(Point(0.66f, 0.40f), Point(0.74f, 0.50f), Point(0.62f, 0.50f), Point(0.62f, 0.40f))),
        ColoringRegion("side_window", "Side Window Glass", rectPoints(0.42f, 0.40f, 0.60f, 0.50f)),
        ColoringRegion("lightbar_blue", "Blue Lightbar Flasher", rectPoints(0.48f, 0.34f, 0.54f, 0.38f)),
        ColoringRegion("lightbar_red", "Red Lightbar Flasher", rectPoints(0.54f, 0.34f, 0.60f, 0.38f)),
        ColoringRegion("push_bar", "Front Push Bumper", rectPoints(0.88f, 0.58f, 0.94f, 0.70f)),
        ColoringRegion("headlight", "Twin Headlight", circlePoints(0.87f, 0.62f, 0.025f)),
        ColoringRegion("wheel_front", "Front Police Tire", circlePoints(0.72f, 0.72f, 0.09f)),
        ColoringRegion("wheel_rear", "Rear Police Tire", circlePoints(0.28f, 0.72f, 0.09f)),
        ColoringRegion("rim_front", "Chrome Center Hub F", circlePoints(0.72f, 0.72f, 0.045f)),
        ColoringRegion("rim_rear", "Chrome Center Hub R", circlePoints(0.28f, 0.72f, 0.045f)),
        ColoringRegion("antenna", "Radio Antenna", rectPoints(0.32f, 0.30f, 0.33f, 0.48f))
    )),

    // 7. MEGA MONSTER TRUCK
    createPage("vehicle_monster_truck", "Mega Monster Truck", CATEGORY_VEHICLES, "🛻", "Medium", listOf(
        ColoringRegion("dirt_mound", "Crushed Dirt Mound", ovalPoints(0.50f, 0.90f, 0.46f, 0.08f)),
        ColoringRegion("cab", "Lifted Truck Cab", listOf(Point(0.40f, 0.34f), Point(0.66f, 0.34f), Point(0.74f, 0.48f), Point(0.34f, 0.48f))),
        ColoringRegion("windshield", "Windshield", rectPoints(0.56f, 0.36f, 0.68f, 0.46f)),
        ColoringRegion("side_window", "Side Window", rectPoints(0.44f, 0.36f, 0.54f, 0.46f)),
        ColoringRegion("truck_bed", "Open Cargo Bed", rectPoints(0.20f, 0.42f, 0.40f, 0.50f)),
        ColoringRegion("hood", "Forward Hood", rectPoints(0.66f, 0.42f, 0.82f, 0.50f)),
        ColoringRegion("flame_decal", "Hot Rod Flame Decal", trianglePoints(0.48f, 0.46f, 0.68f, 0.44f, 0.58f, 0.49f)),
        ColoringRegion("roll_cage", "Roof Light Rack", rectPoints(0.42f, 0.30f, 0.64f, 0.34f)),
        ColoringRegion("spotlight_1", "Roof Spotlight 1", circlePoints(0.46f, 0.31f, 0.02f)),
        ColoringRegion("spotlight_2", "Roof Spotlight 2", circlePoints(0.54f, 0.31f, 0.02f)),
        ColoringRegion("spotlight_3", "Roof Spotlight 3", circlePoints(0.62f, 0.31f, 0.02f)),
        ColoringRegion("chassis_lift", "Lifted Frame Beams", rectPoints(0.36f, 0.50f, 0.66f, 0.58f)),
        ColoringRegion("shock_spring_f", "Heavy Coil Spring Front", rectPoints(0.68f, 0.56f, 0.74f, 0.66f)),
        ColoringRegion("shock_spring_r", "Heavy Coil Spring Rear", rectPoints(0.28f, 0.56f, 0.34f, 0.66f)),
        ColoringRegion("giant_tire_front", "Giant Front 66-Inch Tire", circlePoints(0.72f, 0.68f, 0.15f)),
        ColoringRegion("giant_tire_rear", "Giant Rear 66-Inch Tire", circlePoints(0.28f, 0.68f, 0.15f)),
        ColoringRegion("deep_rim_front", "Deep Dish Rim Front", circlePoints(0.72f, 0.68f, 0.08f)),
        ColoringRegion("deep_rim_rear", "Deep Dish Rim Rear", circlePoints(0.28f, 0.68f, 0.08f))
    )),

    // 8. SUPERSONIC JET
    createPage("vehicle_jet", "Supersonic Jet", CATEGORY_VEHICLES, "✈️", "Easy", listOf(
        ColoringRegion("sky_clouds", "Stratosphere Clouds", ovalPoints(0.50f, 0.88f, 0.44f, 0.06f)),
        ColoringRegion("fuselage", "Needle Jet Fuselage", listOf(Point(0.12f, 0.48f), Point(0.86f, 0.48f), Point(0.94f, 0.52f), Point(0.86f, 0.56f), Point(0.12f, 0.56f))),
        ColoringRegion("needle_nose", "Sharp Nose Cone", trianglePoints(0.86f, 0.48f, 0.98f, 0.52f, 0.86f, 0.56f)),
        ColoringRegion("cockpit_canopy", "Glass Cockpit Canopy", ovalPoints(0.76f, 0.46f, 0.09f, 0.035f)),
        ColoringRegion("delta_wing_top", "Upper Swept Wing", trianglePoints(0.40f, 0.48f, 0.22f, 0.20f, 0.62f, 0.48f)),
        ColoringRegion("delta_wing_bot", "Lower Swept Wing", trianglePoints(0.40f, 0.56f, 0.22f, 0.84f, 0.62f, 0.56f)),
        ColoringRegion("wing_missile_top", "Wingtip Pod Top", rectPoints(0.18f, 0.18f, 0.28f, 0.22f)),
        ColoringRegion("wing_missile_bot", "Wingtip Pod Bottom", rectPoints(0.18f, 0.82f, 0.28f, 0.86f)),
        ColoringRegion("vertical_tail", "Vertical Stabilizer Fin", trianglePoints(0.14f, 0.48f, 0.06f, 0.26f, 0.26f, 0.48f)),
        ColoringRegion("engine_turbine_top", "Jet Turbine Top", rectPoints(0.18f, 0.44f, 0.34f, 0.48f)),
        ColoringRegion("engine_turbine_bot", "Jet Turbine Bottom", rectPoints(0.18f, 0.56f, 0.34f, 0.60f)),
        ColoringRegion("afterburner_flame_1", "Afterburner Flame Upper", trianglePoints(0.12f, 0.45f, 0.01f, 0.47f, 0.12f, 0.49f)),
        ColoringRegion("afterburner_flame_2", "Afterburner Flame Lower", trianglePoints(0.12f, 0.55f, 0.01f, 0.57f, 0.12f, 0.59f)),
        ColoringRegion("speed_stripe", "Mission Decal Stripe", rectPoints(0.46f, 0.51f, 0.70f, 0.53f)),
        ColoringRegion("sun", "High Altitude Sun", circlePoints(0.88f, 0.16f, 0.08f)),
        ColoringRegion("contrail", "Supersonic Contrail", rectPoints(0.01f, 0.51f, 0.10f, 0.53f))
    )),

    // 9. SKY RESCUE HELICOPTER
    createPage("vehicle_helicopter", "Sky Rescue Copter", CATEGORY_VEHICLES, "🚁", "Medium", listOf(
        ColoringRegion("helipad", "Mountain Helipad", rectPoints(0.10f, 0.84f, 0.90f, 0.92f)),
        ColoringRegion("cabin", "Helicopter Egg Cabin", ovalPoints(0.50f, 0.52f, 0.22f, 0.16f)),
        ColoringRegion("windshield", "Cockpit Bubble Window", listOf(Point(0.56f, 0.40f), Point(0.70f, 0.46f), Point(0.68f, 0.58f), Point(0.56f, 0.56f))),
        ColoringRegion("side_window", "Cabin Window", circlePoints(0.44f, 0.50f, 0.045f)),
        ColoringRegion("medical_cross_h", "Red Cross Stripe H", rectPoints(0.40f, 0.56f, 0.48f, 0.60f)),
        ColoringRegion("medical_cross_v", "Red Cross Stripe V", rectPoints(0.42f, 0.54f, 0.46f, 0.62f)),
        ColoringRegion("rotor_mast", "Rotor Hub Mast", rectPoints(0.48f, 0.30f, 0.52f, 0.38f)),
        ColoringRegion("rotor_blade_l", "Rotor Blade Left", rectPoints(0.12f, 0.28f, 0.48f, 0.32f)),
        ColoringRegion("rotor_blade_r", "Rotor Blade Right", rectPoints(0.52f, 0.28f, 0.88f, 0.32f)),
        ColoringRegion("tail_boom", "Long Tail Boom", listOf(Point(0.28f, 0.50f), Point(0.10f, 0.44f), Point(0.10f, 0.50f), Point(0.28f, 0.56f))),
        ColoringRegion("tail_fin", "Vertical Tail Fin", listOf(Point(0.10f, 0.38f), Point(0.14f, 0.38f), Point(0.12f, 0.54f), Point(0.08f, 0.54f))),
        ColoringRegion("tail_rotor", "Spinning Tail Rotor", circlePoints(0.11f, 0.38f, 0.04f)),
        ColoringRegion("skid_strut_f", "Front Skid Leg", rectPoints(0.56f, 0.66f, 0.60f, 0.76f)),
        ColoringRegion("skid_strut_r", "Rear Skid Leg", rectPoints(0.38f, 0.66f, 0.42f, 0.76f)),
        ColoringRegion("skid_tube", "Tubular Landing Skid", rectPoints(0.30f, 0.76f, 0.70f, 0.80f)),
        ColoringRegion("searchlight", "Rescue Searchlight", circlePoints(0.68f, 0.62f, 0.035f))
    )),

    // 10. VINTAGE BIPLANE
    createPage("vehicle_biplane", "Vintage Biplane", CATEGORY_VEHICLES, "🛩️", "Medium", listOf(
        ColoringRegion("cloud", "Summer Cloud", ovalPoints(0.50f, 0.88f, 0.42f, 0.06f)),
        ColoringRegion("fuselage", "Classic Aeroplane Body", ovalPoints(0.50f, 0.54f, 0.32f, 0.10f)),
        ColoringRegion("cockpit", "Open Air Cockpit", ovalPoints(0.48f, 0.47f, 0.07f, 0.04f)),
        ColoringRegion("pilot_goggles", "Aviator Cap & Goggles", circlePoints(0.48f, 0.43f, 0.035f)),
        ColoringRegion("upper_wing", "Top Wing", rectPoints(0.30f, 0.30f, 0.72f, 0.36f)),
        ColoringRegion("lower_wing", "Bottom Wing", rectPoints(0.34f, 0.58f, 0.68f, 0.64f)),
        ColoringRegion("wing_strut_l", "Wing Strut Left", rectPoints(0.38f, 0.36f, 0.40f, 0.58f)),
        ColoringRegion("wing_strut_r", "Wing Strut Right", rectPoints(0.62f, 0.36f, 0.64f, 0.58f)),
        ColoringRegion("engine_cowl", "Radial Engine Cowl", rectPoints(0.78f, 0.48f, 0.86f, 0.60f)),
        ColoringRegion("propeller_hub", "Wooden Propeller Hub", circlePoints(0.88f, 0.54f, 0.03f)),
        ColoringRegion("propeller_blade_top", "Propeller Blade Up", ovalPoints(0.88f, 0.42f, 0.02f, 0.10f)),
        ColoringRegion("propeller_blade_bot", "Propeller Blade Down", ovalPoints(0.88f, 0.66f, 0.02f, 0.10f)),
        ColoringRegion("tail_rudder", "Vertical Tail Rudder", trianglePoints(0.18f, 0.54f, 0.10f, 0.36f, 0.24f, 0.54f)),
        ColoringRegion("tail_elevator", "Horizontal Elevator", rectPoints(0.12f, 0.52f, 0.24f, 0.56f)),
        ColoringRegion("wheel_landing", "Landing Gear Wheel", circlePoints(0.64f, 0.74f, 0.06f)),
        ColoringRegion("wheel_strut", "Gear V-Strut", trianglePoints(0.60f, 0.62f, 0.68f, 0.62f, 0.64f, 0.74f))
    )),

    // 11. COSMIC SPACE SHUTTLE ROCKET
    createPage("vehicle_space_shuttle", "Cosmic Rocket", CATEGORY_VEHICLES, "🚀", "Easy", listOf(
        ColoringRegion("space_stars", "Star Nebula", starPoints(0.18f, 0.18f, 0.06f, 0.025f)),
        ColoringRegion("rocket_body", "Cylindrical Fuselage", rectPoints(0.40f, 0.28f, 0.60f, 0.68f)),
        ColoringRegion("nose_cone", "Aerodynamic Nose Cone", trianglePoints(0.40f, 0.28f, 0.60f, 0.28f, 0.50f, 0.08f)),
        ColoringRegion("mission_stripe_1", "Space Agency Stripe 1", rectPoints(0.40f, 0.36f, 0.60f, 0.40f)),
        ColoringRegion("mission_stripe_2", "Space Agency Stripe 2", rectPoints(0.40f, 0.46f, 0.60f, 0.50f)),
        ColoringRegion("porthole_ring", "Observation Porthole Ring", circlePoints(0.50f, 0.32f, 0.06f)),
        ColoringRegion("porthole_glass", "Porthole Window Glass", circlePoints(0.50f, 0.32f, 0.04f)),
        ColoringRegion("booster_left", "Solid Booster Left", rectPoints(0.28f, 0.42f, 0.38f, 0.70f)),
        ColoringRegion("booster_right", "Solid Booster Right", rectPoints(0.62f, 0.42f, 0.72f, 0.70f)),
        ColoringRegion("booster_cone_l", "Booster Nose L", trianglePoints(0.28f, 0.42f, 0.38f, 0.42f, 0.33f, 0.34f)),
        ColoringRegion("booster_cone_r", "Booster Nose R", trianglePoints(0.62f, 0.42f, 0.72f, 0.42f, 0.67f, 0.34f)),
        ColoringRegion("wing_fin_l", "Stabilizer Fin Left", trianglePoints(0.40f, 0.56f, 0.22f, 0.72f, 0.40f, 0.68f)),
        ColoringRegion("wing_fin_r", "Stabilizer Fin Right", trianglePoints(0.60f, 0.56f, 0.78f, 0.72f, 0.60f, 0.68f)),
        ColoringRegion("nozzle_main", "Main Engine Nozzle", trapezoidPoints(0.44f, 0.56f, 0.68f, 0.42f, 0.58f, 0.74f)),
        ColoringRegion("flame_outer", "Exhaust Thrust Plume", trianglePoints(0.40f, 0.74f, 0.60f, 0.74f, 0.50f, 0.96f)),
        ColoringRegion("flame_inner", "Hot White Plasma Core", trianglePoints(0.44f, 0.74f, 0.56f, 0.74f, 0.50f, 0.88f))
    )),

    // 12. DEEP SEA SUBMARINE
    createPage("vehicle_submarine", "Deep Explorer Sub", CATEGORY_VEHICLES, "🫧", "Easy", listOf(
        ColoringRegion("ocean_floor", "Deep Trench Bed", ovalPoints(0.50f, 0.90f, 0.44f, 0.06f)),
        ColoringRegion("hull", "Yellow Submarine Hull", ovalPoints(0.50f, 0.56f, 0.34f, 0.18f)),
        ColoringRegion("conning_tower", "Conning Tower Sail", rectPoints(0.44f, 0.36f, 0.56f, 0.44f)),
        ColoringRegion("periscope_stem", "Periscope Tube", rectPoints(0.48f, 0.22f, 0.52f, 0.36f)),
        ColoringRegion("periscope_lens", "Prism Lens", rectPoints(0.52f, 0.22f, 0.58f, 0.26f)),
        ColoringRegion("porthole_1_ring", "Porthole 1 Ring", circlePoints(0.36f, 0.56f, 0.055f)),
        ColoringRegion("porthole_1_glass", "Porthole 1 Glass", circlePoints(0.36f, 0.56f, 0.035f)),
        ColoringRegion("porthole_2_ring", "Porthole 2 Ring", circlePoints(0.50f, 0.56f, 0.055f)),
        ColoringRegion("porthole_2_glass", "Porthole 2 Glass", circlePoints(0.50f, 0.56f, 0.035f)),
        ColoringRegion("porthole_3_ring", "Porthole 3 Ring", circlePoints(0.64f, 0.56f, 0.055f)),
        ColoringRegion("porthole_3_glass", "Porthole 3 Glass", circlePoints(0.64f, 0.56f, 0.035f)),
        ColoringRegion("propeller_shaft", "Shaft Extension", rectPoints(0.12f, 0.54f, 0.18f, 0.58f)),
        ColoringRegion("propeller_blade_top", "Brass Screw Propeller Top", trianglePoints(0.12f, 0.54f, 0.06f, 0.44f, 0.12f, 0.50f)),
        ColoringRegion("propeller_blade_bot", "Brass Screw Propeller Bot", trianglePoints(0.12f, 0.58f, 0.06f, 0.68f, 0.12f, 0.62f)),
        ColoringRegion("diving_plane", "Forward Hydroplane", rectPoints(0.74f, 0.54f, 0.84f, 0.58f)),
        ColoringRegion("bubble_cluster", "Air Bubble", circlePoints(0.24f, 0.34f, 0.035f))
    )),

    // 13. PIRATE GALLEON SHIP
    createPage("vehicle_pirate_ship", "Pirate Galleon", CATEGORY_VEHICLES, "🏴‍☠️", "Hard", listOf(
        ColoringRegion("ocean_waves", "Rolling Sea Waves", rectPoints(0.04f, 0.78f, 0.96f, 0.92f)),
        ColoringRegion("hull", "Curved Wooden Hull", listOf(Point(0.14f, 0.56f), Point(0.86f, 0.52f), Point(0.76f, 0.78f), Point(0.24f, 0.78f))),
        ColoringRegion("poop_deck", "Sterncastle Quarterdeck", rectPoints(0.14f, 0.48f, 0.32f, 0.58f)),
        ColoringRegion("bowsprit", "Forward Bowsprit Pole", listOf(Point(0.86f, 0.52f), Point(0.96f, 0.40f), Point(0.88f, 0.54f))),
        ColoringRegion("main_mast", "Center Mainmast", rectPoints(0.48f, 0.18f, 0.52f, 0.58f)),
        ColoringRegion("fore_mast", "Forward Foremast", rectPoints(0.70f, 0.26f, 0.74f, 0.54f)),
        ColoringRegion("mainsail_lower", "Billowing Lower Mainsail", rectPoints(0.38f, 0.38f, 0.62f, 0.54f)),
        ColoringRegion("mainsail_upper", "Topsail with Skull", rectPoints(0.42f, 0.22f, 0.58f, 0.36f)),
        ColoringRegion("skull_emblem", "Jolly Roger Skull", circlePoints(0.50f, 0.29f, 0.035f)),
        ColoringRegion("crows_nest", "Lookout Crow's Nest", rectPoints(0.46f, 0.16f, 0.54f, 0.22f)),
        ColoringRegion("pirate_flag", "Black Jolly Roger Flag", trianglePoints(0.52f, 0.16f, 0.62f, 0.12f, 0.52f, 0.20f)),
        ColoringRegion("foresail", "Triangular Jib Sail", trianglePoints(0.74f, 0.28f, 0.88f, 0.46f, 0.74f, 0.48f)),
        ColoringRegion("cannon_port_1", "Cannon Gunport 1", circlePoints(0.36f, 0.66f, 0.025f)),
        ColoringRegion("cannon_port_2", "Cannon Gunport 2", circlePoints(0.50f, 0.66f, 0.025f)),
        ColoringRegion("cannon_port_3", "Cannon Gunport 3", circlePoints(0.64f, 0.66f, 0.025f)),
        ColoringRegion("anchor", "Iron Anchor", circlePoints(0.80f, 0.70f, 0.03f))
    )),

    // 14. HOT AIR BALLOON
    createPage("vehicle_hot_air_balloon", "Sky Airship Balloon", CATEGORY_VEHICLES, "🎈", "Easy", listOf(
        ColoringRegion("cloud_base", "Fluffy Sky Cloud", ovalPoints(0.50f, 0.92f, 0.44f, 0.06f)),
        ColoringRegion("gore_center", "Center Balloon Stripe", ovalPoints(0.50f, 0.36f, 0.08f, 0.26f)),
        ColoringRegion("gore_left_1", "Inner Left Stripe", ovalPoints(0.42f, 0.36f, 0.07f, 0.25f)),
        ColoringRegion("gore_right_1", "Inner Right Stripe", ovalPoints(0.58f, 0.36f, 0.07f, 0.25f)),
        ColoringRegion("gore_left_2", "Outer Left Stripe", ovalPoints(0.32f, 0.36f, 0.09f, 0.23f)),
        ColoringRegion("gore_right_2", "Outer Right Stripe", ovalPoints(0.68f, 0.36f, 0.09f, 0.23f)),
        ColoringRegion("equator_band", "Decorative Middle Belt", rectPoints(0.24f, 0.34f, 0.76f, 0.38f)),
        ColoringRegion("balloon_skirt", "Lower Burner Skirt", trapezoidPoints(0.42f, 0.58f, 0.58f, 0.45f, 0.55f, 0.66f)),
        ColoringRegion("burner_flame", "Propane Burner Flame", trianglePoints(0.48f, 0.68f, 0.52f, 0.68f, 0.50f, 0.62f)),
        ColoringRegion("rigging_rope_l", "Suspension Rope Left", rectPoints(0.44f, 0.66f, 0.46f, 0.74f)),
        ColoringRegion("rigging_rope_r", "Suspension Rope Right", rectPoints(0.54f, 0.66f, 0.56f, 0.74f)),
        ColoringRegion("basket", "Woven Wicker Basket", rectPoints(0.42f, 0.74f, 0.58f, 0.84f)),
        ColoringRegion("basket_weave", "Basket Weave Trim", rectPoints(0.42f, 0.78f, 0.58f, 0.80f)),
        ColoringRegion("sky_bird_1", "Flying Bird 1", trianglePoints(0.18f, 0.22f, 0.24f, 0.18f, 0.22f, 0.24f)),
        ColoringRegion("sky_bird_2", "Flying Bird 2", trianglePoints(0.78f, 0.20f, 0.84f, 0.16f, 0.82f, 0.22f)),
        ColoringRegion("sun", "Warm Sun", circlePoints(0.14f, 0.12f, 0.08f))
    )),

    // 15. HEAVY BULLDOZER
    createPage("vehicle_bulldozer", "Heavy Bulldozer", CATEGORY_VEHICLES, "🚧", "Medium", listOf(
        ColoringRegion("gravel", "Gravel Worksite", rectPoints(0.04f, 0.82f, 0.96f, 0.92f)),
        ColoringRegion("push_blade", "Steel Dozer Blade", trapezoidPoints(0.78f, 0.86f, 0.58f, 0.76f, 0.92f, 0.82f)),
        ColoringRegion("blade_edge", "Hardened Cutting Edge", rectPoints(0.76f, 0.80f, 0.92f, 0.84f)),
        ColoringRegion("hydraulic_arm", "Hydraulic Lift Cylinder", rectPoints(0.58f, 0.64f, 0.78f, 0.70f)),
        ColoringRegion("engine_hood", "Heavy Engine Block", rectPoints(0.48f, 0.48f, 0.76f, 0.64f)),
        ColoringRegion("exhaust_pipe", "Vertical Exhaust Stack", rectPoints(0.52f, 0.34f, 0.56f, 0.48f)),
        ColoringRegion("exhaust_cap", "Exhaust Rain Cap", ovalPoints(0.54f, 0.33f, 0.03f, 0.015f)),
        ColoringRegion("rops_cab", "Protective Driver ROPS", rectPoints(0.24f, 0.36f, 0.48f, 0.64f)),
        ColoringRegion("cab_window_front", "Cab Front Glass", rectPoints(0.40f, 0.40f, 0.46f, 0.54f)),
        ColoringRegion("cab_window_side", "Cab Side Door Glass", rectPoints(0.28f, 0.40f, 0.38f, 0.54f)),
        ColoringRegion("crawler_track", "Continuous Track Belt", ovalPoints(0.44f, 0.74f, 0.28f, 0.10f)),
        ColoringRegion("track_core", "Inner Track Frame", ovalPoints(0.44f, 0.74f, 0.22f, 0.06f)),
        ColoringRegion("drive_wheel_f", "Front Idler Wheel", circlePoints(0.62f, 0.74f, 0.05f)),
        ColoringRegion("drive_wheel_r", "Rear Drive Sprocket", circlePoints(0.26f, 0.74f, 0.05f)),
        ColoringRegion("roller_wheel_1", "Track Roller 1", circlePoints(0.38f, 0.75f, 0.035f)),
        ColoringRegion("roller_wheel_2", "Track Roller 2", circlePoints(0.50f, 0.75f, 0.035f))
    ))
) + buildAdditionalDistinctVehicles()

private fun buildAdditionalDistinctVehicles(): List<ColoringPage> = listOf(
    // 16. GIANT EXCAVATOR
    createPage("vehicle_excavator", "Giant Excavator", CATEGORY_VEHICLES, "🏗️", "Hard", listOf(
        ColoringRegion("quarry", "Quarry Ground", rectPoints(0.04f, 0.82f, 0.96f, 0.92f)),
        ColoringRegion("track_belt", "Continuous Tracks", ovalPoints(0.32f, 0.76f, 0.20f, 0.08f)),
        ColoringRegion("roller_1", "Track Roller L", circlePoints(0.22f, 0.76f, 0.04f)),
        ColoringRegion("roller_2", "Track Roller R", circlePoints(0.42f, 0.76f, 0.04f)),
        ColoringRegion("turntable_house", "Revolving Cab House", rectPoints(0.18f, 0.52f, 0.46f, 0.68f)),
        ColoringRegion("counterweight", "Heavy Counterweight", rectPoints(0.14f, 0.54f, 0.20f, 0.68f)),
        ColoringRegion("operator_cab", "Operator Glass Cab", rectPoints(0.34f, 0.46f, 0.46f, 0.64f)),
        ColoringRegion("cab_window", "Cab Glass Panel", rectPoints(0.36f, 0.48f, 0.44f, 0.58f)),
        ColoringRegion("boom_main", "Articulated Boom Arm", listOf(Point(0.42f, 0.60f), Point(0.62f, 0.30f), Point(0.68f, 0.32f), Point(0.46f, 0.64f))),
        ColoringRegion("hydraulic_ram_1", "Boom Hydraulic Ram", rectPoints(0.46f, 0.50f, 0.56f, 0.40f)),
        ColoringRegion("stick_arm", "Dipper Stick Arm", listOf(Point(0.64f, 0.30f), Point(0.82f, 0.52f), Point(0.78f, 0.56f), Point(0.60f, 0.34f))),
        ColoringRegion("bucket", "Toothed Digging Bucket", listOf(Point(0.78f, 0.52f), Point(0.92f, 0.58f), Point(0.86f, 0.72f), Point(0.74f, 0.62f))),
        ColoringRegion("bucket_teeth", "Steel Excavator Teeth", trianglePoints(0.88f, 0.72f, 0.94f, 0.66f, 0.86f, 0.62f)),
        ColoringRegion("dirt_pile", "Excavated Earth Pile", ovalPoints(0.86f, 0.82f, 0.10f, 0.06f)),
        ColoringRegion("exhaust", "Cab Exhaust", rectPoints(0.20f, 0.46f, 0.22f, 0.52f)),
        ColoringRegion("spotlight", "Boom Worklight", circlePoints(0.62f, 0.36f, 0.025f))
    )),

    // 17. MOBILE CRANE
    createPage("vehicle_crane", "Tower Mobile Crane", CATEGORY_VEHICLES, "🏢", "Medium", listOf(
        ColoringRegion("jobsite", "Construction Slab", rectPoints(0.04f, 0.82f, 0.96f, 0.92f)),
        ColoringRegion("carrier_chassis", "Heavy Carrier Truck", rectPoints(0.10f, 0.66f, 0.72f, 0.78f)),
        ColoringRegion("wheel_1", "Heavy Tire 1", circlePoints(0.18f, 0.78f, 0.07f)),
        ColoringRegion("wheel_2", "Heavy Tire 2", circlePoints(0.32f, 0.78f, 0.07f)),
        ColoringRegion("wheel_3", "Heavy Tire 3", circlePoints(0.50f, 0.78f, 0.07f)),
        ColoringRegion("wheel_4", "Heavy Tire 4", circlePoints(0.64f, 0.78f, 0.07f)),
        ColoringRegion("outrigger_pad_l", "Outrigger Left", rectPoints(0.06f, 0.76f, 0.12f, 0.84f)),
        ColoringRegion("outrigger_pad_r", "Outrigger Right", rectPoints(0.70f, 0.76f, 0.76f, 0.84f)),
        ColoringRegion("cab_driver", "Truck Driver Cab", rectPoints(0.56f, 0.58f, 0.72f, 0.68f)),
        ColoringRegion("crane_turret", "Rotating Crane Turret", rectPoints(0.24f, 0.54f, 0.44f, 0.66f)),
        ColoringRegion("counterweight", "Turret Counterweight", rectPoints(0.18f, 0.56f, 0.24f, 0.66f)),
        ColoringRegion("telescopic_boom_1", "Telescopic Boom Section 1", listOf(Point(0.34f, 0.56f), Point(0.72f, 0.18f), Point(0.76f, 0.20f), Point(0.38f, 0.58f))),
        ColoringRegion("telescopic_boom_2", "Telescopic Boom Extension", listOf(Point(0.72f, 0.18f), Point(0.88f, 0.08f), Point(0.90f, 0.10f), Point(0.76f, 0.20f))),
        ColoringRegion("cable_hoist", "Steel Cable Hoist", rectPoints(0.88f, 0.10f, 0.89f, 0.44f)),
        ColoringRegion("pulley_block", "Heavy Pulley Block", rectPoints(0.86f, 0.44f, 0.91f, 0.50f)),
        ColoringRegion("lifting_hook", "Steel Crane Hook", circlePoints(0.885f, 0.54f, 0.035f))
    )),

    // 18. GREEN FARM TRACTOR
    createPage("vehicle_farm_tractor", "Farm Tractor", CATEGORY_VEHICLES, "🌾", "Easy", listOf(
        ColoringRegion("wheat_field", "Harvest Wheat Ground", rectPoints(0.04f, 0.80f, 0.96f, 0.92f)),
        ColoringRegion("giant_rear_tire", "Giant Treaded Rear Tire", circlePoints(0.28f, 0.66f, 0.16f)),
        ColoringRegion("rear_rim", "Yellow Rear Rim", circlePoints(0.28f, 0.66f, 0.09f)),
        ColoringRegion("rear_hub", "Rear Wheel Hub", circlePoints(0.28f, 0.66f, 0.04f)),
        ColoringRegion("small_front_tire", "Small Front Tire", circlePoints(0.76f, 0.72f, 0.09f)),
        ColoringRegion("front_rim", "Yellow Front Rim", circlePoints(0.76f, 0.72f, 0.05f)),
        ColoringRegion("hood", "Green Engine Hood", rectPoints(0.48f, 0.52f, 0.78f, 0.68f)),
        ColoringRegion("grille", "Front Radiator Grille", rectPoints(0.78f, 0.54f, 0.82f, 0.66f)),
        ColoringRegion("headlight", "Tractor Worklight", circlePoints(0.80f, 0.54f, 0.025f)),
        ColoringRegion("fender", "Curved Rear Mudguard", fanPoints(0.28f, 0.66f, 0.16f, 0.20f, 180.0, 360.0)),
        ColoringRegion("cab_seat", "Driver Seat & Controls", rectPoints(0.34f, 0.46f, 0.46f, 0.58f)),
        ColoringRegion("steering_wheel", "Steering Column", rectPoints(0.46f, 0.46f, 0.50f, 0.54f)),
        ColoringRegion("exhaust_stack", "Tall Exhaust Pipe", rectPoints(0.66f, 0.34f, 0.69f, 0.52f)),
        ColoringRegion("canopy_roof", "Sun Canopy Roof", rectPoints(0.24f, 0.32f, 0.54f, 0.36f)),
        ColoringRegion("canopy_posts", "Roll Bar Posts", rectPoints(0.26f, 0.36f, 0.52f, 0.46f)),
        ColoringRegion("hitch", "Rear Tow Hitch", rectPoints(0.10f, 0.68f, 0.16f, 0.74f))
    )),

    // 19. RESCUE AMBULANCE
    createPage("vehicle_ambulance", "Rescue Ambulance", CATEGORY_VEHICLES, "🚑", "Easy", listOf(
        ColoringRegion("hospital_route", "City Street Route", rectPoints(0.04f, 0.78f, 0.96f, 0.88f)),
        ColoringRegion("box_body", "Patient Medical Box", rectPoints(0.12f, 0.38f, 0.66f, 0.72f)),
        ColoringRegion("cab", "Driver Cab Section", rectPoints(0.66f, 0.46f, 0.88f, 0.72f)),
        ColoringRegion("hood", "Forward Hood Slope", rectPoints(0.78f, 0.56f, 0.88f, 0.72f)),
        ColoringRegion("windshield", "Windshield Glass", rectPoints(0.68f, 0.48f, 0.78f, 0.58f)),
        ColoringRegion("door_window", "Cab Door Window", rectPoints(0.68f, 0.48f, 0.74f, 0.58f)),
        ColoringRegion("patient_window", "Frosted Rear Window", rectPoints(0.22f, 0.44f, 0.36f, 0.54f)),
        ColoringRegion("star_of_life_h", "Star of Life Horizontal", rectPoints(0.42f, 0.48f, 0.58f, 0.52f)),
        ColoringRegion("star_of_life_v", "Star of Life Vertical", rectPoints(0.48f, 0.42f, 0.52f, 0.58f)),
        ColoringRegion("orange_stripe", "Emergency Stripe", rectPoints(0.12f, 0.60f, 0.88f, 0.65f)),
        ColoringRegion("siren_flasher_1", "Roof Strobe Left", rectPoints(0.22f, 0.34f, 0.30f, 0.38f)),
        ColoringRegion("siren_flasher_2", "Roof Strobe Front", rectPoints(0.70f, 0.42f, 0.78f, 0.46f)),
        ColoringRegion("wheel_front", "Front Ambulance Tire", circlePoints(0.76f, 0.72f, 0.09f)),
        ColoringRegion("wheel_rear", "Rear Ambulance Tire", circlePoints(0.26f, 0.72f, 0.09f)),
        ColoringRegion("rim_front", "Front Chrome Rim", circlePoints(0.76f, 0.72f, 0.045f)),
        ColoringRegion("rim_rear", "Rear Chrome Rim", circlePoints(0.26f, 0.72f, 0.045f))
    )),

    // 20. RECYCLING GARBAGE TRUCK
    createPage("vehicle_garbage_truck", "Garbage Truck", CATEGORY_VEHICLES, "🚛", "Medium", listOf(
        ColoringRegion("road", "Clean City Road", rectPoints(0.04f, 0.78f, 0.96f, 0.88f)),
        ColoringRegion("packer_body", "Waste Compactor Body", rectPoints(0.14f, 0.36f, 0.64f, 0.72f)),
        ColoringRegion("hopper_tailgate", "Rear Loading Hopper", rectPoints(0.08f, 0.42f, 0.16f, 0.74f)),
        ColoringRegion("recycle_arrow_1", "Green Recycling Logo", starPoints(0.38f, 0.52f, 0.06f, 0.03f, 3)),
        ColoringRegion("cab", "Forward Control Cab", rectPoints(0.64f, 0.42f, 0.88f, 0.72f)),
        ColoringRegion("windshield", "Panoramic Windshield", rectPoints(0.74f, 0.46f, 0.86f, 0.58f)),
        ColoringRegion("door_glass", "Low Vision Door Glass", rectPoints(0.66f, 0.48f, 0.74f, 0.62f)),
        ColoringRegion("flashing_beacon", "Amber Warning Beacon", circlePoints(0.74f, 0.38f, 0.025f)),
        ColoringRegion("wheel_front", "Steer Tire", circlePoints(0.76f, 0.72f, 0.09f)),
        ColoringRegion("wheel_drive_1", "Drive Tire 1", circlePoints(0.26f, 0.72f, 0.09f)),
        ColoringRegion("wheel_drive_2", "Drive Tire 2", circlePoints(0.44f, 0.72f, 0.09f)),
        ColoringRegion("fuel_tank", "Compressed Gas Cylinder", rectPoints(0.54f, 0.64f, 0.64f, 0.70f)),
        ColoringRegion("curbside_bin", "Green Compost Bin", rectPoints(0.90f, 0.62f, 0.96f, 0.78f)),
        ColoringRegion("bin_lid", "Bin Wheeled Lid", ovalPoints(0.93f, 0.61f, 0.04f, 0.015f)),
        ColoringRegion("front_bumper", "Heavy Push Bar", rectPoints(0.88f, 0.68f, 0.94f, 0.74f)),
        ColoringRegion("headlight", "Headlamp", circlePoints(0.88f, 0.62f, 0.025f))
    )),

    // 21. CHOPPER MOTORCYCLE
    createPage("vehicle_motorcycle", "Cruiser Motorcycle", CATEGORY_VEHICLES, "🏍️", "Medium", listOf(
        ColoringRegion("asphalt", "Highway Asphalt", rectPoints(0.04f, 0.80f, 0.96f, 0.90f)),
        ColoringRegion("front_spoke_wheel", "Tall Front Spoke Wheel", circlePoints(0.80f, 0.68f, 0.13f)),
        ColoringRegion("front_rim", "Chrome Front Rim", circlePoints(0.80f, 0.68f, 0.07f)),
        ColoringRegion("rear_fat_wheel", "Fat Rear Wheel", circlePoints(0.24f, 0.68f, 0.12f)),
        ColoringRegion("rear_rim", "Solid Rear Rim", circlePoints(0.24f, 0.68f, 0.06f)),
        ColoringRegion("extended_fork", "Raked Front Fork", listOf(Point(0.58f, 0.40f), Point(0.80f, 0.68f), Point(0.78f, 0.70f), Point(0.56f, 0.42f))),
        ColoringRegion("headlight", "Chrome Bullet Headlight", circlePoints(0.72f, 0.46f, 0.035f)),
        ColoringRegion("handlebars", "Ape Hanger Handlebars", rectPoints(0.56f, 0.32f, 0.60f, 0.42f)),
        ColoringRegion("teardrop_tank", "Teardrop Gas Tank", ovalPoints(0.50f, 0.48f, 0.10f, 0.06f)),
        ColoringRegion("tank_flame", "Custom Flame Graphic", trianglePoints(0.44f, 0.48f, 0.56f, 0.46f, 0.50f, 0.51f)),
        ColoringRegion("v_twin_engine", "V-Twin Engine Block", rectPoints(0.38f, 0.56f, 0.54f, 0.68f)),
        ColoringRegion("cooling_fins", "Cylinder Cooling Fins", rectPoints(0.40f, 0.58f, 0.52f, 0.62f)),
        ColoringRegion("exhaust_pipe", "Chrome Slashcut Exhaust", rectPoints(0.28f, 0.66f, 0.52f, 0.70f)),
        ColoringRegion("leather_seat", "Stepped Leather Seat", ovalPoints(0.36f, 0.52f, 0.08f, 0.04f)),
        ColoringRegion("rear_fender", "Bobbed Rear Fender", fanPoints(0.24f, 0.68f, 0.12f, 0.15f, 180.0, 310.0)),
        ColoringRegion("chain_drive", "Drive Belt Cover", rectPoints(0.24f, 0.66f, 0.38f, 0.70f))
    )),

    // 22. CLASSIC BICYCLE
    createPage("vehicle_bicycle", "Classic Bicycle", CATEGORY_VEHICLES, "🚲", "Easy", listOf(
        ColoringRegion("path", "Park Bike Path", rectPoints(0.04f, 0.82f, 0.96f, 0.90f)),
        ColoringRegion("front_tire", "Front Bicycle Tire", circlePoints(0.74f, 0.66f, 0.14f)),
        ColoringRegion("front_spokes", "Front Wheel Spokes", circlePoints(0.74f, 0.66f, 0.08f)),
        ColoringRegion("rear_tire", "Rear Bicycle Tire", circlePoints(0.26f, 0.66f, 0.14f)),
        ColoringRegion("rear_spokes", "Rear Wheel Spokes", circlePoints(0.26f, 0.66f, 0.08f)),
        ColoringRegion("frame_top_tube", "Diamond Frame Top Tube", rectPoints(0.38f, 0.46f, 0.64f, 0.49f)),
        ColoringRegion("frame_down_tube", "Down Tube", listOf(Point(0.64f, 0.46f), Point(0.46f, 0.68f), Point(0.44f, 0.65f), Point(0.62f, 0.44f))),
        ColoringRegion("seat_tube", "Seat Tube Mast", rectPoints(0.38f, 0.42f, 0.42f, 0.68f)),
        ColoringRegion("leather_saddle", "Comfort Saddle", ovalPoints(0.38f, 0.42f, 0.07f, 0.025f)),
        ColoringRegion("front_fork", "Steering Fork", rectPoints(0.64f, 0.44f, 0.74f, 0.66f)),
        ColoringRegion("handlebars", "Swept Handlebars", ovalPoints(0.66f, 0.38f, 0.08f, 0.02f)),
        ColoringRegion("wicker_basket", "Handlebar Basket", rectPoints(0.68f, 0.40f, 0.78f, 0.48f)),
        ColoringRegion("basket_flower", "Basket Daisy Flower", starPoints(0.73f, 0.42f, 0.035f, 0.015f, 5)),
        ColoringRegion("chain_ring", "Front Sprocket Crank", circlePoints(0.44f, 0.67f, 0.04f)),
        ColoringRegion("pedal", "Foot Pedal", rectPoints(0.42f, 0.69f, 0.48f, 0.72f)),
        ColoringRegion("bell", "Handlebar Ding Bell", circlePoints(0.63f, 0.36f, 0.02f))
    )),

    // 23. TURBO SPEEDBOAT
    createPage("vehicle_speed_boat", "Turbo Speedboat", CATEGORY_VEHICLES, "🚤", "Easy", listOf(
        ColoringRegion("lake_water", "Deep Lake Water", rectPoints(0.04f, 0.78f, 0.96f, 0.92f)),
        ColoringRegion("spray_wake", "Rooster Tail Spray", listOf(Point(0.12f, 0.74f), Point(0.01f, 0.54f), Point(0.08f, 0.76f))),
        ColoringRegion("v_hull", "Speedboat V-Hull", listOf(Point(0.16f, 0.62f), Point(0.84f, 0.56f), Point(0.92f, 0.66f), Point(0.20f, 0.76f))),
        ColoringRegion("deck_top", "Polished Fiber Deck", listOf(Point(0.36f, 0.54f), Point(0.84f, 0.56f), Point(0.48f, 0.58f))),
        ColoringRegion("cockpit", "Driver Cockpit Basin", rectPoints(0.36f, 0.54f, 0.62f, 0.64f)),
        ColoringRegion("windshield", "Tinted Wrap Windshield", listOf(Point(0.58f, 0.48f), Point(0.68f, 0.56f), Point(0.56f, 0.56f))),
        ColoringRegion("steering_wheel", "Marine Helm Wheel", circlePoints(0.58f, 0.54f, 0.025f)),
        ColoringRegion("bucket_seat_1", "Sport Seat 1", rectPoints(0.40f, 0.54f, 0.46f, 0.62f)),
        ColoringRegion("bucket_seat_2", "Sport Seat 2", rectPoints(0.48f, 0.54f, 0.54f, 0.62f)),
        ColoringRegion("racing_stripe", "Hull Racing Stripe", rectPoints(0.22f, 0.65f, 0.82f, 0.69f)),
        ColoringRegion("outboard_motor", "Dual Outboard Engine", rectPoints(0.10f, 0.58f, 0.18f, 0.74f)),
        ColoringRegion("engine_cowl", "Engine Cowling", ovalPoints(0.14f, 0.58f, 0.04f, 0.03f)),
        ColoringRegion("propeller_sub", "Bronze Propeller", circlePoints(0.10f, 0.74f, 0.025f)),
        ColoringRegion("navigation_light", "Bow Green Light", circlePoints(0.88f, 0.58f, 0.015f)),
        ColoringRegion("bow_cleat", "Tie-down Cleat", rectPoints(0.78f, 0.54f, 0.82f, 0.56f)),
        ColoringRegion("sun", "Summer Sun", circlePoints(0.86f, 0.16f, 0.08f))
    )),

    // 24. OCEAN SAILBOAT
    createPage("vehicle_sailboat", "Ocean Sailboat", CATEGORY_VEHICLES, "⛵", "Easy", listOf(
        ColoringRegion("sea_wave", "Blue Sea Waves", rectPoints(0.04f, 0.78f, 0.96f, 0.92f)),
        ColoringRegion("hull", "Fiberglass Sloop Hull", listOf(Point(0.18f, 0.66f), Point(0.86f, 0.66f), Point(0.74f, 0.80f), Point(0.26f, 0.80f))),
        ColoringRegion("hull_stripe", "Waterline Stripe", rectPoints(0.20f, 0.72f, 0.82f, 0.75f)),
        ColoringRegion("cabin_trunk", "Cabin Deck House", rectPoints(0.42f, 0.60f, 0.66f, 0.66f)),
        ColoringRegion("portlight", "Cabin Window", ovalPoints(0.54f, 0.63f, 0.04f, 0.015f)),
        ColoringRegion("mast", "Aluminum Mast", rectPoints(0.52f, 0.14f, 0.55f, 0.66f)),
        ColoringRegion("boom", "Horizontal Sail Boom", rectPoints(0.26f, 0.58f, 0.53f, 0.61f)),
        ColoringRegion("mainsail", "Triangular Mainsail", trianglePoints(0.28f, 0.58f, 0.52f, 0.18f, 0.52f, 0.58f)),
        ColoringRegion("mainsail_stripe_1", "Mainsail Stripe Top", trianglePoints(0.44f, 0.32f, 0.52f, 0.20f, 0.52f, 0.32f)),
        ColoringRegion("mainsail_stripe_2", "Mainsail Stripe Mid", trianglePoints(0.36f, 0.46f, 0.52f, 0.34f, 0.52f, 0.46f)),
        ColoringRegion("jib_sail", "Forward Jib Headsail", trianglePoints(0.56f, 0.22f, 0.82f, 0.64f, 0.56f, 0.64f)),
        ColoringRegion("rudder", "Underwater Rudder", trianglePoints(0.20f, 0.76f, 0.22f, 0.86f, 0.26f, 0.78f)),
        ColoringRegion("pennant_flag", "Masthead Wind Pennant", trianglePoints(0.54f, 0.12f, 0.62f, 0.14f, 0.54f, 0.16f)),
        ColoringRegion("life_ring", "Lifebuoy Ring", circlePoints(0.34f, 0.68f, 0.035f)),
        ColoringRegion("cloud_1", "Puffy Sea Cloud", ovalPoints(0.20f, 0.22f, 0.10f, 0.04f)),
        ColoringRegion("sun", "Seaside Sun", circlePoints(0.86f, 0.14f, 0.08f))
    )),

    // 25. ALIEN UFO SAUCER
    createPage("vehicle_ufo", "Star Alien Saucer", CATEGORY_VEHICLES, "🛸", "Easy", listOf(
        ColoringRegion("night_space", "Cosmic Night", rectPoints(0.04f, 0.04f, 0.96f, 0.96f)),
        ColoringRegion("tractor_beam", "Glowing Tractor Beam Cone", trianglePoints(0.42f, 0.64f, 0.58f, 0.64f, 0.50f, 0.92f)),
        ColoringRegion("saucer_rim", "Metallic Saucer Disc", ovalPoints(0.50f, 0.56f, 0.38f, 0.12f)),
        ColoringRegion("saucer_core", "Inner Drive Hull", ovalPoints(0.50f, 0.54f, 0.26f, 0.08f)),
        ColoringRegion("cockpit_dome", "Glass Cockpit Dome", fanPoints(0.50f, 0.48f, 0.04f, 0.18f, 180.0, 360.0)),
        ColoringRegion("alien_head", "Alien Pilot Head", ovalPoints(0.50f, 0.42f, 0.045f, 0.06f)),
        ColoringRegion("alien_eye_l", "Giant Black Eye L", ovalPoints(0.48f, 0.41f, 0.015f, 0.02f)),
        ColoringRegion("alien_eye_r", "Giant Black Eye R", ovalPoints(0.52f, 0.41f, 0.015f, 0.02f)),
        ColoringRegion("light_port_1", "Photon Port 1", circlePoints(0.20f, 0.56f, 0.03f)),
        ColoringRegion("light_port_2", "Photon Port 2", circlePoints(0.32f, 0.58f, 0.03f)),
        ColoringRegion("light_port_3", "Photon Port 3", circlePoints(0.44f, 0.60f, 0.03f)),
        ColoringRegion("light_port_4", "Photon Port 4", circlePoints(0.56f, 0.60f, 0.03f)),
        ColoringRegion("light_port_5", "Photon Port 5", circlePoints(0.68f, 0.58f, 0.03f)),
        ColoringRegion("light_port_6", "Photon Port 6", circlePoints(0.80f, 0.56f, 0.03f)),
        ColoringRegion("planet_saturn", "Distant Saturn Ring", circlePoints(0.18f, 0.22f, 0.05f)),
        ColoringRegion("star_twinkle", "Starlight", starPoints(0.84f, 0.24f, 0.05f, 0.02f))
    )),

    // 26. LUNAR MOON ROVER
    createPage("vehicle_moon_rover", "Lunar Moon Rover", CATEGORY_VEHICLES, "🌑", "Medium", listOf(
        ColoringRegion("lunar_surface", "Moon Dust Crater Ground", ovalPoints(0.50f, 0.90f, 0.46f, 0.08f)),
        ColoringRegion("rover_chassis", "Tubular Aluminum Frame", rectPoints(0.22f, 0.62f, 0.78f, 0.70f)),
        ColoringRegion("mesh_tire_f", "Wire Mesh Front Tire", circlePoints(0.72f, 0.72f, 0.11f)),
        ColoringRegion("mesh_tire_r", "Wire Mesh Rear Tire", circlePoints(0.28f, 0.72f, 0.11f)),
        ColoringRegion("mesh_hub_f", "Front Hub Motor", circlePoints(0.72f, 0.72f, 0.05f)),
        ColoringRegion("mesh_hub_r", "Rear Hub Motor", circlePoints(0.28f, 0.72f, 0.05f)),
        ColoringRegion("fender_f", "Dust Fender Front", fanPoints(0.72f, 0.72f, 0.11f, 0.14f, 220.0, 360.0)),
        ColoringRegion("fender_r", "Dust Fender Rear", fanPoints(0.28f, 0.72f, 0.11f, 0.14f, 180.0, 320.0)),
        ColoringRegion("astronaut_seat_1", "Lawn Chair Seat 1", rectPoints(0.40f, 0.52f, 0.52f, 0.62f)),
        ColoringRegion("astronaut_seat_2", "Lawn Chair Seat 2", rectPoints(0.54f, 0.52f, 0.64f, 0.62f)),
        ColoringRegion("control_console", "T-Handle JoyStick", rectPoints(0.50f, 0.46f, 0.54f, 0.54f)),
        ColoringRegion("dish_antenna", "High Gain Dish Antenna", circlePoints(0.34f, 0.42f, 0.07f)),
        ColoringRegion("dish_feed", "Feed Horn", circlePoints(0.34f, 0.42f, 0.02f)),
        ColoringRegion("tv_camera", "Color TV Camera", rectPoints(0.74f, 0.48f, 0.82f, 0.54f)),
        ColoringRegion("battery_pack", "Thermal Battery Blanket", rectPoints(0.66f, 0.58f, 0.76f, 0.64f)),
        ColoringRegion("earth_in_sky", "Blue Marble Earth", circlePoints(0.82f, 0.18f, 0.08f))
    )),

    // 27. MARS EXPLORER LANDER
    createPage("vehicle_mars_rover", "Mars Lander", CATEGORY_VEHICLES, "🪐", "Medium", listOf(
        ColoringRegion("red_dunes", "Mars Red Dunes", rectPoints(0.04f, 0.82f, 0.96f, 0.92f)),
        ColoringRegion("lander_body", "Hexagonal Avionics Bus", circlePoints(0.50f, 0.56f, 0.16f, 6)),
        ColoringRegion("thermal_gold", "Gold Mylar Blanket", circlePoints(0.50f, 0.56f, 0.10f, 6)),
        ColoringRegion("solar_wing_l", "Solar Array Left", rectPoints(0.14f, 0.52f, 0.34f, 0.60f)),
        ColoringRegion("solar_wing_r", "Solar Array Right", rectPoints(0.66f, 0.52f, 0.86f, 0.60f)),
        ColoringRegion("solar_cell_l1", "Solar Cell Grid L", rectPoints(0.16f, 0.54f, 0.32f, 0.58f)),
        ColoringRegion("solar_cell_r1", "Solar Cell Grid R", rectPoints(0.68f, 0.54f, 0.84f, 0.58f)),
        ColoringRegion("landing_leg_l", "Tripod Leg Left", listOf(Point(0.38f, 0.68f), Point(0.20f, 0.84f), Point(0.24f, 0.84f), Point(0.42f, 0.68f))),
        ColoringRegion("landing_leg_c", "Center Leg Shock", rectPoints(0.48f, 0.70f, 0.52f, 0.84f)),
        ColoringRegion("landing_leg_r", "Tripod Leg Right", listOf(Point(0.62f, 0.68f), Point(0.80f, 0.84f), Point(0.76f, 0.84f), Point(0.58f, 0.68f))),
        ColoringRegion("footpad_l", "Crushable Footpad L", ovalPoints(0.22f, 0.84f, 0.04f, 0.015f)),
        ColoringRegion("footpad_c", "Crushable Footpad C", ovalPoints(0.50f, 0.84f, 0.04f, 0.015f)),
        ColoringRegion("footpad_r", "Crushable Footpad R", ovalPoints(0.78f, 0.84f, 0.04f, 0.015f)),
        ColoringRegion("mastcam_neck", "Camera Mast Mastcam", rectPoints(0.48f, 0.36f, 0.52f, 0.44f)),
        ColoringRegion("mastcam_head", "Stereo Camera Head", rectPoints(0.44f, 0.30f, 0.56f, 0.36f)),
        ColoringRegion("robotic_arm", "Soil Sampling Robotic Arm", listOf(Point(0.56f, 0.64f), Point(0.68f, 0.74f), Point(0.64f, 0.80f), Point(0.54f, 0.68f)))
    )),

    // 28. ZEPPELIN AIRSHIP
    createPage("vehicle_blimp", "Zeppelin Airship", CATEGORY_VEHICLES, "☁️", "Easy", listOf(
        ColoringRegion("sky", "Clear Blue Sky", rectPoints(0.04f, 0.04f, 0.96f, 0.96f)),
        ColoringRegion("envelope", "Streamlined Airship Hull", ovalPoints(0.50f, 0.40f, 0.38f, 0.18f)),
        ColoringRegion("hull_nose", "Mooring Spindle Nose", circlePoints(0.88f, 0.40f, 0.03f)),
        ColoringRegion("hull_stripe_top", "Upper Hull Stripe", rectPoints(0.20f, 0.34f, 0.80f, 0.38f)),
        ColoringRegion("hull_stripe_bot", "Lower Hull Stripe", rectPoints(0.20f, 0.42f, 0.80f, 0.46f)),
        ColoringRegion("empennage_top", "Top Rudder Fin", trianglePoints(0.18f, 0.32f, 0.12f, 0.16f, 0.26f, 0.32f)),
        ColoringRegion("empennage_bot", "Bottom Rudder Fin", trianglePoints(0.18f, 0.48f, 0.12f, 0.64f, 0.26f, 0.48f)),
        ColoringRegion("gondola", "Passenger Cabin Gondola", rectPoints(0.42f, 0.56f, 0.64f, 0.66f)),
        ColoringRegion("window_1", "Panoramic Window 1", rectPoints(0.44f, 0.58f, 0.48f, 0.63f)),
        ColoringRegion("window_2", "Panoramic Window 2", rectPoints(0.50f, 0.58f, 0.54f, 0.63f)),
        ColoringRegion("window_3", "Panoramic Window 3", rectPoints(0.56f, 0.58f, 0.60f, 0.63f)),
        ColoringRegion("propeller_pod", "Engine Nacelle Pod", rectPoints(0.36f, 0.54f, 0.42f, 0.60f)),
        ColoringRegion("propeller_fan", "Engine Fan Blade", ovalPoints(0.35f, 0.57f, 0.015f, 0.04f)),
        ColoringRegion("cloud_drift_1", "Passing Cloud Left", ovalPoints(0.16f, 0.78f, 0.12f, 0.05f)),
        ColoringRegion("cloud_drift_2", "Passing Cloud Right", ovalPoints(0.82f, 0.74f, 0.10f, 0.04f)),
        ColoringRegion("sun", "Glowing Sun", circlePoints(0.86f, 0.14f, 0.08f))
    )),

    // 29. CABLE TRAM STREETCAR
    createPage("vehicle_tram", "City Cable Tram", CATEGORY_VEHICLES, "🚋", "Easy", listOf(
        ColoringRegion("street_track", "Streetcar Rail Tracks", rectPoints(0.04f, 0.80f, 0.96f, 0.90f)),
        ColoringRegion("tram_body", "Yellow Streetcar Body", rectPoints(0.14f, 0.38f, 0.86f, 0.74f)),
        ColoringRegion("roof", "Curved Trolley Roof", ovalPoints(0.50f, 0.38f, 0.36f, 0.05f)),
        ColoringRegion("pantograph_base", "Trolley Base", rectPoints(0.46f, 0.28f, 0.54f, 0.34f)),
        ColoringRegion("pantograph_arm", "Overhead Diamond Pole", diamondPoints(0.50f, 0.22f, 0.06f, 0.08f)),
        ColoringRegion("contact_wire", "Overhead Power Wire", rectPoints(0.04f, 0.14f, 0.96f, 0.16f)),
        ColoringRegion("windshield_front", "Front Driver Window", rectPoints(0.74f, 0.44f, 0.84f, 0.56f)),
        ColoringRegion("window_side_1", "Passenger Window 1", rectPoints(0.18f, 0.44f, 0.28f, 0.56f)),
        ColoringRegion("window_side_2", "Passenger Window 2", rectPoints(0.32f, 0.44f, 0.42f, 0.56f)),
        ColoringRegion("window_side_3", "Passenger Window 3", rectPoints(0.46f, 0.44f, 0.56f, 0.56f)),
        ColoringRegion("window_side_4", "Passenger Window 4", rectPoints(0.60f, 0.44f, 0.70f, 0.56f)),
        ColoringRegion("lower_panel_stripe", "Body Maroon Stripe", rectPoints(0.14f, 0.62f, 0.86f, 0.67f)),
        ColoringRegion("headlamp", "Center Glow Headlamp", circlePoints(0.86f, 0.62f, 0.03f)),
        ColoringRegion("wheel_front", "Steel Track Wheel F", circlePoints(0.72f, 0.76f, 0.08f)),
        ColoringRegion("wheel_rear", "Steel Track Wheel R", circlePoints(0.28f, 0.76f, 0.08f)),
        ColoringRegion("cowcatcher_bumper", "Safety Life Guard", rectPoints(0.86f, 0.72f, 0.92f, 0.76f))
    )),

    // 30. ROTATING CEMENT MIXER
    createPage("vehicle_cement_mixer", "Cement Mixer", CATEGORY_VEHICLES, "🥣", "Medium", listOf(
        ColoringRegion("jobsite_pavement", "Fresh Concrete Pad", rectPoints(0.04f, 0.80f, 0.96f, 0.90f)),
        ColoringRegion("chassis", "Heavy Truck Chassis", rectPoints(0.12f, 0.66f, 0.88f, 0.74f)),
        ColoringRegion("cab", "Forward Concrete Cab", rectPoints(0.66f, 0.44f, 0.88f, 0.72f)),
        ColoringRegion("windshield", "Cab Windshield", rectPoints(0.74f, 0.48f, 0.86f, 0.58f)),
        ColoringRegion("drum_barrel", "Revolving Spiral Drum", ovalPoints(0.42f, 0.50f, 0.24f, 0.18f)),
        ColoringRegion("drum_stripe_1", "Spiral Stripe Orange", rectPoints(0.30f, 0.42f, 0.42f, 0.58f)),
        ColoringRegion("drum_stripe_2", "Spiral Stripe White", rectPoints(0.44f, 0.42f, 0.56f, 0.58f)),
        ColoringRegion("hopper_funnel", "Top Feed Hopper", trianglePoints(0.14f, 0.38f, 0.24f, 0.38f, 0.18f, 0.48f)),
        ColoringRegion("discharge_chute", "Pivoting Discharge Chute", rectPoints(0.08f, 0.54f, 0.18f, 0.68f)),
        ColoringRegion("water_tank", "Pressurized Water Tank", ovalPoints(0.64f, 0.48f, 0.04f, 0.06f)),
        ColoringRegion("wheel_front", "Heavy Front Tire", circlePoints(0.76f, 0.74f, 0.09f)),
        ColoringRegion("wheel_rear_1", "Heavy Rear Tire 1", circlePoints(0.24f, 0.74f, 0.09f)),
        ColoringRegion("wheel_rear_2", "Heavy Rear Tire 2", circlePoints(0.40f, 0.74f, 0.09f)),
        ColoringRegion("rim_front", "Front Chrome Rim", circlePoints(0.76f, 0.74f, 0.045f)),
        ColoringRegion("rim_rear_1", "Rear Chrome Rim 1", circlePoints(0.24f, 0.74f, 0.045f)),
        ColoringRegion("rim_rear_2", "Rear Chrome Rim 2", circlePoints(0.40f, 0.74f, 0.045f))
    )),

    // 31. WAREHOUSE FORKLIFT
    createPage("vehicle_forklift", "Warehouse Forklift", CATEGORY_VEHICLES, "📦", "Medium", listOf(
        ColoringRegion("warehouse_floor", "Polished Concrete Floor", rectPoints(0.04f, 0.82f, 0.96f, 0.90f)),
        ColoringRegion("body_counterweight", "Heavy Rear Counterweight", ovalPoints(0.24f, 0.64f, 0.12f, 0.14f)),
        ColoringRegion("operator_seat", "Forklift Seat", rectPoints(0.34f, 0.54f, 0.44f, 0.66f)),
        ColoringRegion("rops_overhead_guard", "Overhead Safety Guard", rectPoints(0.24f, 0.34f, 0.52f, 0.38f)),
        ColoringRegion("guard_post_1", "Guard Post Rear", rectPoints(0.26f, 0.38f, 0.30f, 0.64f)),
        ColoringRegion("guard_post_2", "Guard Post Front", rectPoints(0.48f, 0.38f, 0.52f, 0.64f)),
        ColoringRegion("steering_wheel", "Mini Steering Knob", circlePoints(0.48f, 0.52f, 0.025f)),
        ColoringRegion("mast_vertical", "Twin Mast Channel Rails", rectPoints(0.56f, 0.28f, 0.64f, 0.76f)),
        ColoringRegion("carriage", "Fork Lift Carriage", rectPoints(0.58f, 0.58f, 0.74f, 0.66f)),
        ColoringRegion("tine_horizontal", "Steel Lifting Forks", rectPoints(0.66f, 0.74f, 0.92f, 0.78f)),
        ColoringRegion("wooden_pallet", "Wooden Shipping Pallet", rectPoints(0.70f, 0.70f, 0.92f, 0.74f)),
        ColoringRegion("cargo_box", "Cardboard Parcel Crate", rectPoints(0.70f, 0.52f, 0.90f, 0.70f)),
        ColoringRegion("fragile_sticker", "Fragile Arrow Badge", trianglePoints(0.78f, 0.58f, 0.84f, 0.58f, 0.81f, 0.64f)),
        ColoringRegion("wheel_front", "Solid Drive Wheel", circlePoints(0.54f, 0.76f, 0.08f)),
        ColoringRegion("wheel_rear", "Solid Steer Wheel", circlePoints(0.26f, 0.76f, 0.06f)),
        ColoringRegion("warning_beacon", "Strobe Flasher", circlePoints(0.38f, 0.32f, 0.025f))
    )),

    // 32. SPEEDY GO-KART
    createPage("vehicle_go_kart", "Speedy Go-Kart", CATEGORY_VEHICLES, "🏁", "Easy", listOf(
        ColoringRegion("kart_track", "Asphalt Circuit", rectPoints(0.04f, 0.82f, 0.96f, 0.90f)),
        ColoringRegion("bumper_guard", "Polyethylene Side Pods", rectPoints(0.18f, 0.66f, 0.82f, 0.74f)),
        ColoringRegion("front_fairing", "Nose Cone Fairing", trianglePoints(0.74f, 0.64f, 0.92f, 0.70f, 0.74f, 0.74f)),
        ColoringRegion("number_plate", "Competition Number Plate", rectPoints(0.70f, 0.56f, 0.78f, 0.66f)),
        ColoringRegion("bucket_seat", "High-G Racing Seat", rectPoints(0.32f, 0.48f, 0.44f, 0.66f)),
        ColoringRegion("steering_wheel", "Butterfly Steering Wheel", ovalPoints(0.60f, 0.56f, 0.04f, 0.03f)),
        ColoringRegion("steering_column", "Chrome Steering Rod", rectPoints(0.54f, 0.56f, 0.60f, 0.68f)),
        ColoringRegion("engine_head", "Air-Cooled Engine Cylinder", rectPoints(0.24f, 0.52f, 0.32f, 0.64f)),
        ColoringRegion("exhaust_muffler", "Expansion Chamber Exhaust", rectPoints(0.12f, 0.58f, 0.24f, 0.64f)),
        ColoringRegion("slick_tire_front", "Front Slick Tire", circlePoints(0.78f, 0.72f, 0.08f)),
        ColoringRegion("slick_tire_rear", "Rear Wide Slick", circlePoints(0.22f, 0.72f, 0.10f)),
        ColoringRegion("rim_front", "Alloy Front Hub", circlePoints(0.78f, 0.72f, 0.04f)),
        ColoringRegion("rim_rear", "Alloy Rear Hub", circlePoints(0.22f, 0.72f, 0.05f)),
        ColoringRegion("checkered_flag", "Checkered Finish Flag", rectPoints(0.08f, 0.22f, 0.24f, 0.34f)),
        ColoringRegion("flag_pole", "Flag Pole", rectPoints(0.06f, 0.20f, 0.08f, 0.48f)),
        ColoringRegion("pedal_box", "Throttle & Brake Pedals", rectPoints(0.70f, 0.68f, 0.74f, 0.72f))
    )),

    // 33. SUNNY MOTOR SCOOTER
    createPage("vehicle_scooter", "Sunny Motor Scooter", CATEGORY_VEHICLES, "🛵", "Easy", listOf(
        ColoringRegion("street", "Seaside Boardwalk", rectPoints(0.04f, 0.82f, 0.96f, 0.90f)),
        ColoringRegion("curved_leg_shield", "Curved Front Leg Shield", listOf(Point(0.64f, 0.42f), Point(0.74f, 0.46f), Point(0.64f, 0.72f), Point(0.56f, 0.68f))),
        ColoringRegion("headlight_pod", "Round Handlebar Headlight", circlePoints(0.68f, 0.38f, 0.045f)),
        ColoringRegion("handlebars", "Chrome Handlebar Grips", rectPoints(0.60f, 0.36f, 0.76f, 0.40f)),
        ColoringRegion("mirrors", "Round Chrome Mirror", circlePoints(0.60f, 0.30f, 0.02f)),
        ColoringRegion("footboard", "Floorboard Deck", rectPoints(0.44f, 0.68f, 0.62f, 0.74f)),
        ColoringRegion("rear_cowl", "Bulbous Engine Cowling", ovalPoints(0.34f, 0.62f, 0.14f, 0.10f)),
        ColoringRegion("cowl_vent", "Side Air Vents", rectPoints(0.30f, 0.60f, 0.38f, 0.64f)),
        ColoringRegion("dual_saddle", "Vintage Leather Seat", ovalPoints(0.42f, 0.52f, 0.12f, 0.04f)),
        ColoringRegion("front_mudguard", "Swooping Front Mudguard", fanPoints(0.74f, 0.72f, 0.09f, 0.13f, 220.0, 360.0)),
        ColoringRegion("wheel_front", "Front Whitewall Tire", circlePoints(0.74f, 0.72f, 0.09f)),
        ColoringRegion("wheel_rear", "Rear Whitewall Tire", circlePoints(0.26f, 0.72f, 0.09f)),
        ColoringRegion("rim_front", "Classic Split Rim F", circlePoints(0.74f, 0.72f, 0.045f)),
        ColoringRegion("rim_rear", "Classic Split Rim R", circlePoints(0.26f, 0.72f, 0.045f)),
        ColoringRegion("kickstand", "Center Kickstand", rectPoints(0.40f, 0.74f, 0.42f, 0.82f)),
        ColoringRegion("taillight", "Red Chrome Taillight", circlePoints(0.18f, 0.58f, 0.02f))
    )),

    // 34. ISLAND HOVERCRAFT
    createPage("vehicle_hovercraft", "Island Hovercraft", CATEGORY_VEHICLES, "💨", "Medium", listOf(
        ColoringRegion("ocean_spray", "Water Spray Cushion", ovalPoints(0.50f, 0.88f, 0.46f, 0.08f)),
        ColoringRegion("rubber_skirt", "Black Flexible Air Skirt", ovalPoints(0.50f, 0.72f, 0.40f, 0.12f)),
        ColoringRegion("main_deck", "Aluminum Buoyancy Hull", rectPoints(0.16f, 0.54f, 0.84f, 0.66f)),
        ColoringRegion("passenger_cabin", "Passenger Observation Cabin", rectPoints(0.30f, 0.44f, 0.74f, 0.56f)),
        ColoringRegion("window_panes", "Cabin View Window Strip", rectPoints(0.34f, 0.46f, 0.70f, 0.52f)),
        ColoringRegion("cockpit_bridge", "Raised Pilot Wheelhouse", rectPoints(0.66f, 0.38f, 0.78f, 0.48f)),
        ColoringRegion("bridge_glass", "Forward Bridge Windshield", rectPoints(0.72f, 0.40f, 0.78f, 0.46f)),
        ColoringRegion("thrust_duct_1", "Propulsion Duct Left", circlePoints(0.26f, 0.42f, 0.11f)),
        ColoringRegion("thrust_fan_1", "Variable Pitch Propeller L", circlePoints(0.26f, 0.42f, 0.05f)),
        ColoringRegion("thrust_rudder_1", "Directional Air Rudder L", rectPoints(0.24f, 0.32f, 0.28f, 0.52f)),
        ColoringRegion("radar_dome", "Navigation Radome", circlePoints(0.72f, 0.34f, 0.025f)),
        ColoringRegion("lift_fan_grille", "Bow Lift Fan Intake", ovalPoints(0.80f, 0.58f, 0.05f, 0.03f)),
        ColoringRegion("life_ring", "Orange Lifebuoy", circlePoints(0.48f, 0.56f, 0.03f)),
        ColoringRegion("side_stripe", "Coast Guard Orange Stripe", rectPoints(0.16f, 0.60f, 0.84f, 0.63f)),
        ColoringRegion("water_splash_l", "Side Splash L", trianglePoints(0.12f, 0.80f, 0.02f, 0.74f, 0.08f, 0.84f)),
        ColoringRegion("water_splash_r", "Side Splash R", trianglePoints(0.88f, 0.80f, 0.98f, 0.74f, 0.92f, 0.84f))
    )),

    // 35. POLAR SNOWMOBILE
    createPage("vehicle_snowmobile", "Polar Snowmobile", CATEGORY_VEHICLES, "❄️", "Easy", listOf(
        ColoringRegion("snowdrift", "Packed Powder Snowdrift", ovalPoints(0.50f, 0.88f, 0.46f, 0.08f)),
        ColoringRegion("front_cowl", "Aerodynamic Engine Cowling", listOf(Point(0.54f, 0.50f), Point(0.84f, 0.58f), Point(0.82f, 0.70f), Point(0.50f, 0.66f))),
        ColoringRegion("windshield", "High Windshield Screen", listOf(Point(0.56f, 0.36f), Point(0.70f, 0.48f), Point(0.54f, 0.48f))),
        ColoringRegion("headlight", "Bright Halogen Headlamp", circlePoints(0.80f, 0.58f, 0.03f)),
        ColoringRegion("handlebar", "Heated Handlebar Grips", rectPoints(0.50f, 0.42f, 0.56f, 0.46f)),
        ColoringRegion("dual_seat", "Two-Up Padded Seat", rectPoints(0.28f, 0.56f, 0.50f, 0.64f)),
        ColoringRegion("tunnel_chassis", "Aluminum Tunnel Frame", rectPoints(0.20f, 0.64f, 0.52f, 0.72f)),
        ColoringRegion("ski_runner", "Front Composite Ski", rectPoints(0.68f, 0.76f, 0.92f, 0.80f)),
        ColoringRegion("ski_spindle", "Front Ski Shock Spindle", rectPoints(0.74f, 0.68f, 0.78f, 0.76f)),
        ColoringRegion("rubber_track", "Studded Continuous Track", ovalPoints(0.32f, 0.76f, 0.18f, 0.06f)),
        ColoringRegion("rear_idler", "Rear Suspension Idler Wheel", circlePoints(0.20f, 0.76f, 0.045f)),
        ColoringRegion("bogie_wheel", "Center Bogie Wheel", circlePoints(0.34f, 0.76f, 0.04f)),
        ColoringRegion("snow_flap", "Rear Rubber Snow Flap", rectPoints(0.14f, 0.68f, 0.18f, 0.78f)),
        ColoringRegion("snow_spray", "Snow Spray Tail", trianglePoints(0.14f, 0.76f, 0.02f, 0.68f, 0.08f, 0.82f)),
        ColoringRegion("snowflake_1", "Falling Snowflake 1", starPoints(0.20f, 0.22f, 0.04f, 0.02f, 6)),
        ColoringRegion("snowflake_2", "Falling Snowflake 2", starPoints(0.84f, 0.26f, 0.04f, 0.02f, 6))
    )),

    // 36. MIGHTY HARBOR TUGBOAT
    createPage("vehicle_tugboat", "Mighty Harbor Tug", CATEGORY_VEHICLES, "🛳️", "Medium", listOf(
        ColoringRegion("harbor_water", "Harbor Channel Water", rectPoints(0.04f, 0.78f, 0.96f, 0.92f)),
        ColoringRegion("displacement_hull", "Sturdy Steel Tug Hull", listOf(Point(0.12f, 0.58f), Point(0.88f, 0.58f), Point(0.82f, 0.78f), Point(0.18f, 0.78f))),
        ColoringRegion("bow_pusher_fender", "Heavy Tire Rope Fender", circlePoints(0.86f, 0.64f, 0.06f)),
        ColoringRegion("side_tire_1", "Side Bumper Tire 1", circlePoints(0.38f, 0.64f, 0.035f)),
        ColoringRegion("side_tire_2", "Side Bumper Tire 2", circlePoints(0.56f, 0.64f, 0.035f)),
        ColoringRegion("wheelhouse", "Elevated Pilot House", rectPoints(0.44f, 0.38f, 0.68f, 0.54f)),
        ColoringRegion("wheelhouse_windows", "Wheelhouse Windows Strip", rectPoints(0.50f, 0.40f, 0.66f, 0.46f)),
        ColoringRegion("flying_bridge", "Open Bridge Wings", rectPoints(0.42f, 0.36f, 0.70f, 0.40f)),
        ColoringRegion("smokestack", "Tugboat Steam Funnel", trapezoidPoints(0.32f, 0.40f, 0.30f, 0.34f, 0.42f, 0.50f)),
        ColoringRegion("funnel_band", "Funnel Shipping Stripe", rectPoints(0.33f, 0.36f, 0.41f, 0.40f)),
        ColoringRegion("smoke_puff", "Diesel Smoke Billow", circlePoints(0.34f, 0.22f, 0.05f)),
        ColoringRegion("mast_radar", "Radar Scanner Mast", rectPoints(0.54f, 0.24f, 0.56f, 0.36f)),
        ColoringRegion("radar_scanner", "Rotating Radar Bar", rectPoints(0.50f, 0.22f, 0.60f, 0.25f)),
        ColoringRegion("towing_winch", "Heavy Towing Winch", rectPoints(0.22f, 0.54f, 0.32f, 0.62f)),
        ColoringRegion("towing_hawser", "Heavy Towing Cable", rectPoints(0.10f, 0.58f, 0.22f, 0.60f)),
        ColoringRegion("life_ring", "Lifebuoy", circlePoints(0.64f, 0.56f, 0.025f))
    )),

    // 37. GRAND CRUISE LINER
    createPage("vehicle_cruise_liner", "Grand Cruise Ship", CATEGORY_VEHICLES, "🚢", "Medium", listOf(
        ColoringRegion("ocean_calm", "Calm Tropical Waters", rectPoints(0.04f, 0.80f, 0.96f, 0.92f)),
        ColoringRegion("ship_hull", "Immense Cruise Ship Hull", listOf(Point(0.12f, 0.60f), Point(0.88f, 0.56f), Point(0.80f, 0.80f), Point(0.18f, 0.80f))),
        ColoringRegion("bulbous_bow", "Bulbous Bow Bulb", circlePoints(0.82f, 0.78f, 0.04f)),
        ColoringRegion("deck_tier_1", "Stateroom Balcony Deck 1", rectPoints(0.20f, 0.52f, 0.78f, 0.60f)),
        ColoringRegion("deck_tier_2", "Stateroom Balcony Deck 2", rectPoints(0.22f, 0.44f, 0.74f, 0.52f)),
        ColoringRegion("bridge_deck", "Command Bridge Wings", rectPoints(0.66f, 0.44f, 0.78f, 0.50f)),
        ColoringRegion("lido_deck", "Top Lido Pool Deck", rectPoints(0.26f, 0.38f, 0.68f, 0.44f)),
        ColoringRegion("swimming_pool", "Pool Water Basin", ovalPoints(0.48f, 0.38f, 0.06f, 0.02f)),
        ColoringRegion("twin_funnel_1", "Forward Cruise Funnel", trapezoidPoints(0.36f, 0.42f, 0.26f, 0.38f, 0.44f, 0.38f)),
        ColoringRegion("twin_funnel_2", "Aft Cruise Funnel", trapezoidPoints(0.56f, 0.62f, 0.26f, 0.58f, 0.64f, 0.38f)),
        ColoringRegion("funnel_crest_1", "Funnel Crown 1", ovalPoints(0.39f, 0.26f, 0.035f, 0.015f)),
        ColoringRegion("funnel_crest_2", "Funnel Crown 2", ovalPoints(0.59f, 0.26f, 0.035f, 0.015f)),
        ColoringRegion("lifeboat_1", "Lifeboat 1", ovalPoints(0.32f, 0.52f, 0.045f, 0.02f)),
        ColoringRegion("lifeboat_2", "Lifeboat 2", ovalPoints(0.44f, 0.52f, 0.045f, 0.02f)),
        ColoringRegion("lifeboat_3", "Lifeboat 3", ovalPoints(0.56f, 0.52f, 0.045f, 0.02f)),
        ColoringRegion("portholes", "Lower Dining Portholes", rectPoints(0.24f, 0.66f, 0.72f, 0.70f))
    )),

    // 38. EXPRESS DELIVERY VAN
    createPage("vehicle_delivery_van", "Parcel Express Van", CATEGORY_VEHICLES, "📬", "Easy", listOf(
        ColoringRegion("street", "Delivery Street", rectPoints(0.04f, 0.80f, 0.96f, 0.90f)),
        ColoringRegion("cargo_box", "Cargo Van Body", rectPoints(0.12f, 0.38f, 0.72f, 0.72f)),
        ColoringRegion("sliding_door", "Side Loading Slider Door", rectPoints(0.40f, 0.42f, 0.58f, 0.72f)),
        ColoringRegion("door_handle", "Flush Door Handle", rectPoints(0.42f, 0.56f, 0.46f, 0.58f)),
        ColoringRegion("van_cab", "Driver Cab Section", rectPoints(0.68f, 0.44f, 0.88f, 0.72f)),
        ColoringRegion("windshield", "Cab Windshield", rectPoints(0.72f, 0.46f, 0.84f, 0.58f)),
        ColoringRegion("door_window", "Cab Door Window", rectPoints(0.62f, 0.46f, 0.70f, 0.58f)),
        ColoringRegion("delivery_badge", "Express Courier Lightning", starPoints(0.26f, 0.52f, 0.06f, 0.025f, 4)),
        ColoringRegion("roof_rack", "Overhead Ladder Rack", rectPoints(0.16f, 0.32f, 0.68f, 0.36f)),
        ColoringRegion("side_rub_strip", "Black Protective Bumper Strip", rectPoints(0.12f, 0.64f, 0.88f, 0.68f)),
        ColoringRegion("headlight", "High-Beam Headlight", circlePoints(0.87f, 0.62f, 0.025f)),
        ColoringRegion("wheel_front", "Front Van Tire", circlePoints(0.76f, 0.74f, 0.09f)),
        ColoringRegion("wheel_rear", "Rear Van Tire", circlePoints(0.26f, 0.74f, 0.09f)),
        ColoringRegion("rim_front", "Steel Rim Front", circlePoints(0.76f, 0.74f, 0.045f)),
        ColoringRegion("rim_rear", "Steel Rim Rear", circlePoints(0.26f, 0.74f, 0.045f)),
        ColoringRegion("parcel_box", "Express Parcel Delivery", rectPoints(0.90f, 0.68f, 0.96f, 0.78f))
    )),

    // 39. CLASSIC PICKUP TRUCK
    createPage("vehicle_pickup", "Classic Pickup", CATEGORY_VEHICLES, "🛻", "Easy", listOf(
        ColoringRegion("country_lane", "Country Dirt Lane", rectPoints(0.04f, 0.80f, 0.96f, 0.90f)),
        ColoringRegion("rounded_cab", "Rounded Cab Roof", ovalPoints(0.48f, 0.44f, 0.16f, 0.12f)),
        ColoringRegion("windshield", "Curved Windshield Glass", ovalPoints(0.56f, 0.42f, 0.08f, 0.06f)),
        ColoringRegion("side_window", "Door Window Glass", rectPoints(0.42f, 0.40f, 0.50f, 0.50f)),
        ColoringRegion("cargo_bed", "Wooden Slatted Cargo Bed", rectPoints(0.12f, 0.52f, 0.38f, 0.68f)),
        ColoringRegion("tailgate", "Stenciled Tailgate", rectPoints(0.10f, 0.52f, 0.14f, 0.68f)),
        ColoringRegion("hood_slope", "Bulbous Front Hood", rectPoints(0.60f, 0.52f, 0.86f, 0.68f)),
        ColoringRegion("chrome_grille", "Horizontal Chrome Grille", rectPoints(0.84f, 0.54f, 0.88f, 0.66f)),
        ColoringRegion("headlight", "Bullet Headlamp", circlePoints(0.86f, 0.56f, 0.025f)),
        ColoringRegion("running_board", "Low Step Running Board", rectPoints(0.36f, 0.70f, 0.64f, 0.74f)),
        ColoringRegion("curved_fender_f", "Flared Front Fender", fanPoints(0.74f, 0.72f, 0.10f, 0.14f, 210.0, 360.0)),
        ColoringRegion("curved_fender_r", "Flared Rear Fender", fanPoints(0.26f, 0.72f, 0.10f, 0.14f, 180.0, 330.0)),
        ColoringRegion("wheel_front", "Front Whitewall Wheel", circlePoints(0.74f, 0.72f, 0.10f)),
        ColoringRegion("wheel_rear", "Rear Whitewall Wheel", circlePoints(0.26f, 0.72f, 0.10f)),
        ColoringRegion("hubcap_front", "Chrome Moon Hubcap F", circlePoints(0.74f, 0.72f, 0.05f)),
        ColoringRegion("hubcap_rear", "Chrome Moon Hubcap R", circlePoints(0.26f, 0.72f, 0.05f))
    )),

    // 40. ARMORED BATTLE TANK
    createPage("vehicle_tank", "Armored Battle Tank", CATEGORY_VEHICLES, "🛡️", "Easy", listOf(
        ColoringRegion("ground_dirt", "Tactical Terrain Ground", rectPoints(0.06f, 0.86f, 0.94f, 0.94f)),
        ColoringRegion("tread_belt", "Continuous Rubber Track Belt", ovalPoints(0.50f, 0.76f, 0.38f, 0.08f)),
        ColoringRegion("road_wheel_1", "Heavy Road Wheel 1", circlePoints(0.22f, 0.76f, 0.055f)),
        ColoringRegion("road_wheel_2", "Heavy Road Wheel 2", circlePoints(0.36f, 0.76f, 0.055f)),
        ColoringRegion("road_wheel_3", "Heavy Road Wheel 3", circlePoints(0.50f, 0.76f, 0.055f)),
        ColoringRegion("road_wheel_4", "Heavy Road Wheel 4", circlePoints(0.64f, 0.76f, 0.055f)),
        ColoringRegion("road_wheel_5", "Heavy Road Wheel 5", circlePoints(0.78f, 0.76f, 0.055f)),
        ColoringRegion("tank_chassis", "Low Slung Armored Hull", trapezoidPoints(0.18f, 0.58f, 0.82f, 0.58f, 0.86f, 0.70f)),
        ColoringRegion("turret", "Armored Rotary Turret", ovalPoints(0.44f, 0.50f, 0.18f, 0.10f)),
        ColoringRegion("cannon_barrel", "Long Cannon Barrel", rectPoints(0.58f, 0.46f, 0.92f, 0.50f)),
        ColoringRegion("muzzle_brake", "Cannon Muzzle Brake", rectPoints(0.90f, 0.44f, 0.96f, 0.52f)),
        ColoringRegion("hatch_cupola", "Commander Hatch Dome", ovalPoints(0.38f, 0.40f, 0.07f, 0.035f)),
        ColoringRegion("star_insignia", "Allied White Star Emblem", starPoints(0.44f, 0.50f, 0.045f, 0.02f, 5))
    ))
)

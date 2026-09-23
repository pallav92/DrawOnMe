import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = mapOf<String, String>().toMutableMap().also { map ->
    if (keystorePropertiesFile.exists()) {
        keystorePropertiesFile.readLines().forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isNotEmpty() && !trimmed.startsWith("#") && trimmed.contains("=")) {
                val (key, value) = trimmed.split("=", limit = 2)
                map[key.trim()] = value.trim()
            }
        }
    }
}

android {
    namespace = "com.draw.onme"
    compileSdk {
        version = release(37)
    }

    if (keystorePropertiesFile.exists()) {
        signingConfigs {
            create("release") {
                val storeFilePath = keystoreProperties["storeFile"] ?: ""
                storeFile = if (file(storeFilePath).exists()) {
                    file(storeFilePath)
                } else {
                    rootProject.file(storeFilePath)
                }
                storePassword = keystoreProperties["storePassword"] ?: ""
                keyAlias = keystoreProperties["keyAlias"] ?: ""
                keyPassword = keystoreProperties["keyPassword"] ?: ""
            }
        }
    }

    defaultConfig {
        applicationId = "com.draw.onme"
        minSdk = 24
        targetSdk = 37
        versionCode = 2
        versionName = "1.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }

}

base {
    archivesName.set("DrawOnMe")
}

val androidComponents = extensions.getByType<com.android.build.api.variant.ApplicationAndroidComponentsExtension>()
val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

androidComponents.onVariants { variant ->
    val baseName = "DrawOnMe_${variant.name}_${android.defaultConfig.versionName}_${android.defaultConfig.versionCode}_$today"
    variant.outputs.forEach { it.outputFileName.set("$baseName.apk") }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.print)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.json)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
}
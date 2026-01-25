plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlinx.serialization)
    id("kotlin-parcelize")
}

android {
    namespace = "io.github.sceneview.sample.armodelviewer"
    compileSdk = 36
    defaultConfig {
        applicationId = "io.github.sceneview.sample.armodelviewer"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    androidResources {
        noCompress.add("filamat")
        noCompress.add("ktx")
    }
}

dependencies {
    implementation(projects.samples.common)
    implementation(libs.fuel)
    implementation(libs.coil)
    implementation(libs.kotlinx.serialization.json)

    // ArSceneView
    releaseImplementation(libs.arsceneview)
    debugImplementation(projects.arsceneview)
}

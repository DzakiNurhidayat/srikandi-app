plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    alias(libs.plugins.kotlin.plugin.serialization) apply false
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}
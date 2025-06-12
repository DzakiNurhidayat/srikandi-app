import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.plugin.serialization)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "org.example.project"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.example.project"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Compose dependencies
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // Lifecycle
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            // Serialization
            implementation(libs.kotlin.plugin.serialization)

            // Project dependencies
            implementation(projects.shared)
        }

        androidMain.dependencies {
            // Android specific Compose dependencies
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.composeui.tooling.preview)
            implementation(libs.androidx.compose.foundation)
            implementation(libs.coil)
            implementation(libs.media3.exoplayer)
            implementation(libs.media3.ui)


            // Firebase
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.messaging)
        }

        all {
            languageSettings {
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
            }
        }
    }
}

dependencies {
    // Compose tooling
    implementation(compose.uiTooling)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room)
    ksp(libs.room.compiler)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization)

    // OkHttp
    implementation(libs.okHttp)
    implementation(libs.okHttp.logging)

    // Compose UI
    implementation(libs.composeui)
    implementation(libs.composeui.tooling.preview)
    implementation(libs.composeui.material3)

    // Navigation
    implementation(libs.androidx.hilt.navigation.compose.v120)
}
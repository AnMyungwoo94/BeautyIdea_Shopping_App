plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
}

android {
    namespace = "com.myungwoo.model"
    compileSdk = BuildConstants.compileSdkVersion

    defaultConfig {
        minSdk = BuildConstants.minSdkVersion
    }

    compileOptions {
        sourceCompatibility = BuildConstants.javaVersion
        targetCompatibility = BuildConstants.javaVersion
    }

    kotlinOptions {
        jvmTarget = BuildConstants.kotlinJvmTarget
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("com.google.code.gson:gson:2.10.1")
}
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.myungwoo.data"
    compileSdk = BuildConstants.compileSdkVersion

    defaultConfig {
        minSdk = BuildConstants.minSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = BuildConstants.javaVersion
        targetCompatibility =  BuildConstants.javaVersion
    }
    kotlinOptions {
        jvmTarget = BuildConstants.kotlinJvmTarget
    }
}

dependencies {
    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")

    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:datastore"))
}
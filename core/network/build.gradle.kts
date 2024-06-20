import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
    kotlin("kapt")
}

android {
    namespace = "com.myungwoo.network"
    compileSdk = BuildConstants.compileSdkVersion

    defaultConfig {
        minSdk = BuildConstants.minSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "firebase_base_url", getProperty("firebase_base_url"))
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
        targetCompatibility = BuildConstants.javaVersion
    }
    kotlinOptions {
        jvmTarget = BuildConstants.kotlinJvmTarget
    }
}

fun getProperty(key: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(key)
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // 네트워크 통신
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")

    // 파이어베이스
    implementation("com.google.firebase:firebase-auth-ktx:23.0.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")

    implementation(project(":core:common"))
    implementation(project(":core:model"))
}
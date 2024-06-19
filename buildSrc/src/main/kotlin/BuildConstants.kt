import org.gradle.api.JavaVersion

object BuildConstants {
    const val compileSdkVersion = 34
    const val minSdkVersion = 26
    const val targetSdkVersion = 34
    const val appVersionCode = 14
    const val appVersionName = "1.0"

    val javaVersion = JavaVersion.VERSION_1_8
    const val kotlinJvmTarget = "1.8"
}
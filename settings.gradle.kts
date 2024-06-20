@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://devrepo.kakao.com/nexus/content/groups/public/") }
        maven { url = uri("https://jitpack.io") } // 이니시스 결제
    }
}

rootProject.name = "BeautyIdea_APP"
include(":app")
include(":core:model")
include(":core:network")
include(":core:datastore")
include(":core:common")
include(":core:data")

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
    }
}

rootProject.name = "Voices"

include(":app")
include(":core:common")
include(":core:ui")
include(":core:network")
include(":core:database")
include(":feature:auth")
include(":feature:chat")
include(":feature:history")
include(":feature:settings")
include(":feature:profile")

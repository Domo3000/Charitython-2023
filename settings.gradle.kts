pluginManagement {
    repositories {
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
    }
}

rootProject.name = "GreenHeroes"

include(":shared")
include(":browser")
include(":server")

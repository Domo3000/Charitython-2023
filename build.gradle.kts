plugins {
    kotlin("multiplatform") version "1.8.21" apply false
    kotlin("plugin.serialization") version "1.8.21" apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.4.0"
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xcontext-receivers")
            jvmTarget = "17"
        }
    }
}

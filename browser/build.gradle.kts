plugins {
    kotlin("js")
}

kotlin {
    js {
        binaries.executable()
        browser {
            webpackTask {
                outputFileName = "main.js"
            }
        }
    }
}

dependencies {
    val ktorVersion = findProperty("ktorVersion")
    val coroutineVersion = findProperty("coroutineVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")

    val suffix = "-pre.546"
    fun kotlinw(target: String, version: String): String = "org.jetbrains.kotlin-wrappers:kotlin-$target:$version$suffix"
    implementation(kotlinw("react", "18.2.0"))
    implementation(kotlinw("react-dom", "18.2.0"))
    implementation(kotlinw("emotion", "11.10.8"))
    implementation(kotlinw("csstype", "3.1.2"))
    implementation(kotlinw("react-router-dom", "6.11.0"))

    fun kvision(target: String): String = "io.kvision:kvision-$target:6.3.2"
    implementation(kvision("react"))
    implementation(kvision("maps"))

    implementation(project(":shared"))
}

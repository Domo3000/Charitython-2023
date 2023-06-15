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

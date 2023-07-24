import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

application {
    mainClass.set("ApplicationKt")
}

dependencies {
    val ktorVersion = findProperty("ktorVersion")
    val exposedVersion = findProperty("exposedVersion")

    implementation(project(":shared"))

    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.20")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-rate-limit:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    implementation("org.postgresql:postgresql:42.6.0")
    runtimeOnly("com.h2database:h2:2.1.214")

    implementation("com.sksamuel.scrimage:scrimage-core:4.0.37")

    implementation("org.jetbrains.kotlin-wrappers:kotlin-css:1.0.0-pre.388")
    implementation("ch.qos.logback:logback-classic:1.2.11") // TODO 1.4.1

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("portfolio")
    archiveClassifier.set("")
    archiveVersion.set("v1")

    val debug = System.getenv("DEBUG")
    val projects = mutableListOf("browser")
    val environment = if(debug == "true") {
        "Development"
    } else {
        "Production"
    }

    for(project in projects) {
        dependsOn(":$project:browser${environment}Webpack")
        val js = tasks.getByPath(":$project:browser${environment}Webpack") as KotlinWebpack
        into("assets") { from(File(js.destinationDirectory, js.outputFileName)) }
    }

    manifest {
        attributes(Pair("Implementation-Version", "1.15"))
    }

    minimize {
        exclude(dependency("org.jetbrains.kotlin:kotlin-reflect"))
    }
}

tasks.getByName<JavaExec>("run") {
    dependsOn(tasks.withType<ShadowJar>())
    classpath(tasks.withType<ShadowJar>())
}

apply(plugin = "com.github.johnrengelman.shadow")
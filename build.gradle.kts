import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.20-Beta"
    kotlin("plugin.serialization") version "1.8.20-Beta"

    id("com.github.ben-manes.versions") version "0.45.0"
    application
}

group = "top.anagke.auto_ark"
version = "0.2.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("top.anagke:auto-android")

    implementation(kotlin("stdlib"))
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.tinylog:tinylog-impl:2.6.0")
    implementation("org.tinylog:tinylog-api-kotlin:2.6.0")
    implementation("org.tinylog:slf4j-tinylog:2.6.0")

    implementation("org.openpnp:opencv:4.6.0-0")
    implementation("top.anagke:kio:1.1.0")

    // https://mvnrepository.com/artifact/org.jsoup/jsoup
    implementation("org.jsoup:jsoup:1.15.3")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.5.0-RC")
    implementation("com.charleskorn.kaml:kaml:0.51.0")
    implementation("com.google.code.gson:gson:2.10.1")


    implementation("com.github.albfernandez:juniversalchardet:2.4.0")
    implementation("info.debatty:java-string-similarity:2.0.0")

    // Config Parsing
    implementation("com.sksamuel.hoplite:hoplite:1.0.3")
    implementation("com.sksamuel.hoplite:hoplite-yaml:2.7.1")

    // CLI Argument Parsing
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.wrapper {
    gradleVersion = "7.6"
}

application {
    mainClass.set("top.anagke.auto_ark.AppKt")
    executableDir = ""
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8")
}

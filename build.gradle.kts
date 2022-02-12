import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"

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

    implementation("ch.qos.logback:logback-classic:1.3.0-alpha5")
    implementation("io.github.microutils:kotlin-logging:2.1.21")

    implementation("org.openpnp:opencv:4.5.1-2")
    implementation("top.anagke:kio:1.1.0")

    // https://mvnrepository.com/artifact/org.jsoup/jsoup
    implementation("org.jsoup:jsoup:1.14.3")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.2")
    implementation("com.charleskorn.kaml:kaml:0.38.0")
    implementation("com.google.code.gson:gson:2.8.9")


    implementation("com.github.albfernandez:juniversalchardet:2.4.0")
    implementation("info.debatty:java-string-similarity:2.0.0")

    // Config Parsing
    implementation("com.sksamuel.hoplite:hoplite:1.0.3")
    implementation("com.sksamuel.hoplite:hoplite-yaml:1.4.16")


    // CLI Argument Parsing
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.4")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.wrapper {
    gradleVersion = "7.3.2"
}

distributions {
    main {
        contents {
            from(".") {
                include("bin/**")
                include("config_base.yaml")
            }
        }
    }
}

application {
    mainClass.set("top.anagke.auto_ark.AppKt")
    executableDir = ""
}
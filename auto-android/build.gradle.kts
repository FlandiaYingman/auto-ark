import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"

    application
}

group = "top.anagke"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("stdlib-jdk8"))

    implementation("ch.qos.logback:logback-classic:1.3.0-alpha5")
    implementation("io.github.microutils:kotlin-logging:2.1.16")

    implementation("org.openpnp:opencv:4.5.1-2")
    implementation("top.anagke:kio:1.1.0")

    // https://mvnrepository.com/artifact/org.jsoup/jsoup
    implementation("org.jsoup:jsoup:1.14.3")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.1")
    implementation("com.charleskorn.kaml:kaml:0.38.0")
    implementation("com.google.code.gson:gson:2.8.9")


    implementation("com.github.albfernandez:juniversalchardet:2.4.0")
    implementation("info.debatty:java-string-similarity:2.0.0")

    // Config Parsing
    implementation("com.sksamuel.hoplite:hoplite:1.0.3")
    implementation("com.sksamuel.hoplite:hoplite-toml:1.4.16")


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
                include("base-config.toml")
            }
        }
    }
}

application {
    mainClass.set("top.anagke.MainKt")
    executableDir = ""
}
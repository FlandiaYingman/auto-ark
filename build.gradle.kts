import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.31"

    id("org.openjfx.javafxplugin") version "0.0.10"
    application
}


application {
    mainClass.set("top.anagke.auto_ark.AutoArkKt")
}

group = "top.anagke.auto_ark"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("stdlib-jdk8"))

    implementation("ch.qos.logback:logback-classic:1.3.0-alpha5")
    implementation("io.github.microutils:kotlin-logging:2.0.11")

    implementation("org.openpnp:opencv:4.5.1-2")
    implementation("top.anagke:kio:1.1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.0")
    implementation("com.charleskorn.kaml:kaml:0.36.0")
    implementation("com.google.code.gson:gson:2.8.8")

    implementation("com.github.albfernandez:juniversalchardet:2.4.0")

    implementation("org.openjfx:javafx:11")
    implementation("org.openjfx:javafx-base:11")
    implementation("org.openjfx:javafx-graphics:11")
    implementation("org.openjfx:javafx-controls:11")
    implementation("org.openjfx:javafx-fxml:11")
    implementation("org.openjfx:javafx-swing:11")
    implementation("org.openjfx:javafx-media:11")
    implementation("org.openjfx:javafx-web:11")
}

javafx {
    version = "11.0.+"
    modules = listOf("javafx.controls", "javafx.fxml")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.wrapper {
    gradleVersion = "7.2"
}
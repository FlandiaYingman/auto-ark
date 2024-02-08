import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")

    id("org.bytedeco.gradle-javacpp-platform")
    id("com.github.ben-manes.versions")
}

group = "top.anagke"
version = "0.2.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    // Log Frameworks
    implementation("org.tinylog:tinylog-impl:2.7.0")
    implementation("org.tinylog:tinylog-api-kotlin:2.7.0")
    implementation("org.tinylog:slf4j-tinylog:2.7.0")

    // OpenCV
    implementation("org.bytedeco:opencv-platform:4.9.0-1.5.10")
    implementation("top.anagke:kio:1.1.0")

    // Utilities
    implementation("info.debatty:java-string-similarity:2.0.0")
    implementation("org.reflections:reflections:0.10.2")

    implementation("net.java.dev.jna:jna:5.14.0")
    implementation("net.java.dev.jna:jna-platform:5.14.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}
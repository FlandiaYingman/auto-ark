import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.0-Beta3"
    kotlin("plugin.serialization") version "2.0.0-Beta3"

    id("org.bytedeco.gradle-javacpp-platform") version "1.5.10"
    id("com.github.ben-manes.versions") version "0.51.0"
    application
}

group = "top.anagke.auto_ark"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":auto-android"))

    implementation(kotlin("stdlib"))

    implementation("org.tinylog:tinylog-impl:2.7.0")
    implementation("org.tinylog:tinylog-api-kotlin:2.7.0")
    implementation("org.tinylog:slf4j-tinylog:2.7.0")

    // OpenCV
    implementation("org.bytedeco:opencv-platform:4.9.0-1.5.10")
    implementation("top.anagke:kio:1.1.0")

    // https://mvnrepository.com/artifact/org.jsoup/jsoup
    implementation("org.jsoup:jsoup:1.17.2")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.2")
    implementation("com.charleskorn.kaml:kaml:0.57.0")
    implementation("com.google.code.gson:gson:2.10.1")


    implementation("info.debatty:java-string-similarity:2.0.0")

    // Config Parsing
    implementation("com.sksamuel.hoplite:hoplite:1.0.3")
    implementation("com.sksamuel.hoplite:hoplite-yaml:2.8.0.RC3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

application {
    mainClass.set("top.anagke.auto_ark.AppKt")
    executableDir = ""
    applicationDefaultJvmArgs += "-Dfile.encoding=UTF-8"
}

plugins {
    kotlin("jvm") version "2.1.20-Beta1"
    kotlin("plugin.serialization") version "2.1.20-Beta1"

    id("org.bytedeco.gradle-javacpp-platform") version "1.5.10"
    id("com.github.ben-manes.versions") version "0.51.0"
    application
}

group = "dev.flandia"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("org.tinylog:tinylog-impl:2.8.0-M1")
    implementation("org.tinylog:tinylog-api-kotlin:2.8.0-M1")
    implementation("org.tinylog:slf4j-tinylog:2.8.0-M1")

    implementation("com.charleskorn.kaml:kaml:0.67.0")
    implementation("com.google.code.gson:gson:2.11.0")

    implementation("com.sksamuel.hoplite:hoplite:1.0.3")
    implementation("com.sksamuel.hoplite:hoplite-yaml:2.9.0")

    implementation("org.bytedeco:opencv-platform:4.10.0-1.5.11")

    implementation("org.reflections:reflections:0.10.2")

    implementation("net.java.dev.jna:jna:5.16.0")
    implementation("net.java.dev.jna:jna-platform:5.16.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.0-RC")
}

kotlin {
    compilerOptions {
        jvmToolchain(23)
    }
}

application {
    mainClass.set("dev.flandia.ark.AppKt")
    executableDir = ""
    applicationDefaultJvmArgs += "-Dfile.encoding=UTF-8"
}

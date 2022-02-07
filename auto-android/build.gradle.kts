import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"

    application
}

group = "top.anagke"
version = "0.1.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("stdlib-jdk8"))

    // Log Frameworks
    implementation("ch.qos.logback:logback-core:1.3.0-alpha5")
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha5")
    implementation("io.github.microutils:kotlin-logging:2.1.21")

    // OpenCV
    implementation("org.bytedeco:opencv-platform:4.5.3-1.5.6")
    implementation("top.anagke:kio:1.1.0")

    // Utilities
    implementation("info.debatty:java-string-similarity:2.0.0")
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.lz4:lz4-java:1.8.0")

    //Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.1")
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
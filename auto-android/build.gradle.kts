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
    implementation("org.tinylog:tinylog-impl:2.4.1")
    implementation("org.tinylog:tinylog-api-kotlin:2.4.1")
    implementation("org.tinylog:slf4j-tinylog:2.4.1")

    // OpenCV
    implementation("org.bytedeco:opencv-platform:4.5.5-1.5.7")
    implementation("top.anagke:kio:1.1.0")

    // Utilities
    implementation("info.debatty:java-string-similarity:2.0.0")
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.lz4:lz4-java:1.8.0")

    implementation("net.java.dev.jna:jna:5.11.0")
    implementation("net.java.dev.jna:jna-platform:5.11.0")

    //Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.2")
    implementation("org.junit.jupiter:junit-jupiter:5.8.2")
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
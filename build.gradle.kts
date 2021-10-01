plugins {
    java
    application

    kotlin("jvm") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.31"
}


application {
    mainClass.set("top.anagke.auto_ark.MainKt")
}

group "top.anagke.auto_ark"
version "0.1"

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

    implementation("net.java.dev.jna:jna:5.9.0")
    implementation("net.java.dev.jna:jna-platform:5.9.0")
    implementation("com.github.albfernandez:juniversalchardet:2.4.0")
}
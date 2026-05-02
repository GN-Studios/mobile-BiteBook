plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
    id("com.google.devtools.ksp") version "2.2.21-2.0.5"
    id("androidx.room") version "2.8.4"
    application
}

group = "com.example.bitebook"
version = "1.0.0"

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:3.2.0")
    implementation("io.ktor:ktor-server-netty-jvm:3.2.0")
    implementation("io.ktor:ktor-server-call-logging-jvm:3.2.0")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:3.2.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.2.0")
    implementation("ch.qos.logback:logback-classic:1.5.18")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.sqlite:sqlite-bundled:2.6.2")
    ksp("androidx.room:room-compiler:2.8.4")

    implementation("com.google.firebase:firebase-admin:9.7.0")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.example.bitebook.MainKt")
}

room {
    schemaDirectory("$projectDir/schemas")
}

tasks.test {
    useJUnitPlatform()
}

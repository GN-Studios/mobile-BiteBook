plugins {
    kotlin("jvm") version "2.2.21"
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("androidx.room:room-runtime:2.6.1")\
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
}

kotlin {
    jvmToolchain(21)
}


tasks.test {
    useJUnitPlatform()
}
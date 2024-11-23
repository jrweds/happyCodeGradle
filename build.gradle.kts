plugins {
    kotlin("jvm") version "2.0.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    // Dependencia para JavaMail
    implementation("com.sun.mail:javax.mail:1.6.2") // Agregamos JavaMail aquí
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

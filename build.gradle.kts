plugins {
    kotlin("jvm") version "2.0.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven ( "https://oss.sonatype.org/content/repositories/snapshots/" )

}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    //dependencia JavaMail
    implementation("com.sun.mail:javax.mail:1.6.2")
    //biblioteca JNativeHook
    implementation ("com.github.kwhat:jnativehook:2.2.2")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

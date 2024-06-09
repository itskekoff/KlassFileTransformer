plugins {
    kotlin("jvm") version "1.9.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "ru.itskekoff.transformer"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("")
    manifest {
        attributes(mapOf("Main-Class" to "ru.itskekoff.transformer.JarTransformer"))
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

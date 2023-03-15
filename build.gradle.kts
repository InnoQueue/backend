import org.jetbrains.kotlin.cli.jvm.main
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val springBootVersion: String by project
val javaVersion: String by project

plugins {
    id("org.springframework.boot") version "2.7.8"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.spring") version "1.8.10"
    kotlin("plugin.jpa") version "1.8.10"
    id("org.jetbrains.dokka") version "1.7.20"
}

group = "com.innopolis"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    val kotlinVersion: String by project
    val flywayVersion: String by project
    val firebaseVersion: String by project
    val jsonVersion: String by project
    val springdocVersion: String by project
    val junitVersion: String by project
    val mockkVersion: String by project
    val testcontainersVersion: String by project
    val postgresqlVersion: String by project
    val jacksonVersion: String by project

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    implementation("com.google.firebase:firebase-admin:$firebaseVersion")
    implementation("org.json:json:$jsonVersion")
    implementation("org.springdoc:springdoc-openapi-ui:$springdocVersion")

    runtimeOnly("org.postgresql:postgresql:$postgresqlVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
}

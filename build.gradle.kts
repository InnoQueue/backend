val springBootVersion: String by project
val javaVersion: String by project

plugins {
    id("org.springframework.boot") version "2.7.8"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.spring") version "1.8.10"
    kotlin("plugin.jpa") version "1.8.10"
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
    id("jacoco")
    id("org.jetbrains.dokka") version "1.7.20"
    id("info.solidsoft.pitest") version "1.9.0"
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
    implementation("org.springdoc:springdoc-openapi-ui:$springdocVersion")

    runtimeOnly("org.postgresql:postgresql:$postgresqlVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy("jacocoTestCoverageVerification", "jacocoTestReport")
    doLast {
        println("View code coverage at:")
        println("file://$buildDir/reports/jacoco/test/html/index.html")
    }
}

tasks.withType<JacocoReport> {
    reports {
        xml.apply {
            isEnabled = true
        }
    }
}

tasks.withType<JacocoCoverageVerification> {
//    dependsOn("pitest")
    violationRules {
        rule {
            limit {
                minimum = BigDecimal(0.8)
            }
        }
    }
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    jvmTarget = javaVersion
}
tasks.withType<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>().configureEach {
    jvmTarget = javaVersion
}

detekt {
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
    config =
        files("$projectDir/config/detekt.yml") // point to your custom config defining rules to run, overwriting default behavior
    baseline = file("$projectDir/config/baseline.xml") // a way of suppressing issues before introducing detekt
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
        xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
        txt.required.set(true) // similar to the console output, contains issue signature to manually edit baseline files
        sarif.required.set(true) // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with Github Code Scanning
        md.required.set(true) // simple Markdown format
    }
}

pitest {
    setProperty("junit5PluginVersion", "1.0.0")
    setProperty("testPlugin", "junit5")
    setProperty("mutationThreshold", 55)
    setProperty("targetClasses", listOf("com.innopolis.innoqueue.domain.notification.service.NotificationService*"))
    setProperty("targetTests", listOf("com.innopolis.innoqueue.domain.notification.service.NotificationServiceTest*"))
    setProperty("outputFormats", listOf("HTML", "XML"))
}

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.18"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.4.32"
    kotlin("plugin.spring") version "1.4.32"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
}

group = "com.badger"
version = "3.0.0"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:2.7.18")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springdoc:springdoc-openapi-ui:1.7.0")
    implementation("com.github.stelar7:R4J:2.6.2")
    implementation("org.springframework.boot:spring-boot-starter-security:2.7.18")
    implementation("org.cache2k:cache2k-core:2.6.1.Final")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.18")
}

// Force coroutines to 1.5.2 — Spring Boot 2.7's BOM would upgrade it to 1.6.4
// which requires Kotlin metadata 1.6 but this project uses Kotlin 1.4.32.
dependencyManagement {
    dependencies {
        dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
        dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.5.2")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

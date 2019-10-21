import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


val versions = mapOf(
    "kotlin" to "1.3.41"
)

plugins {
    id("maven-publish")
    kotlin("jvm").version("1.3.41")
    id("maven")
    java
}

group = "info.thale"
version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

dependencies {

    // http4k
    implementation("org.http4k:http4k-core:3.179.1")

    // logging
    implementation("org.slf4j:slf4j-api:1.7.26")
    implementation("ch.qos.logback:logback-classic:1.0.13")

    // kotlin
    implementation(kotlin("stdlib-jdk8"))

    // auth
    implementation("com.google.api-client:google-api-client:1.30.2")
    implementation("com.google.auth:google-auth-library-oauth2-http:0.17.1")
    implementation("com.auth0:java-jwt:3.8.2")

    // test
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")
    testImplementation("io.mockk:mockk:1.9.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}

sourceSets {
    main {
        java {
            srcDir("src/main/kotlin/")
        }
    }
    test {
        java {
            srcDir("src/test/kotlin/")
        }
    }
}

configurations.all {
    resolutionStrategy {
        failOnVersionConflict()
        force("org.jetbrains.kotlin:kotlin-stdlib:${versions["kotlin"]}")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${versions["kotlin"]}")
        force("org.jetbrains.kotlin:kotlin-reflect:${versions["kotlin"]}")
        force("org.jetbrains.kotlin:kotlin-stdlib-common:${versions["kotlin"]}")
        force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${versions["kotlin"]}")
        force("org.slf4j:slf4j-api:1.7.26")
        force("com.google.http-client:google-http-client-jackson2:1.31.0")
        force("com.google.guava:guava:28.0-android")
        force("com.google.http-client:google-http-client:1.31.0")
        force("commons-codec:commons-codec:1.12")
        force("io.mockk:mockk:1.9.3")
    }
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform { }
}
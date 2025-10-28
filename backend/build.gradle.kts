plugins {
    id("org.springframework.boot") version "3.3.4" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
    id("com.diffplug.spotless") version "6.25.0"
    id("jacoco")
}

repositories {
    mavenCentral()
}

spotless {
    java {
        googleJavaFormat()
        target("**/*.java")
    }
}

allprojects {
    group = "com.efaturaai"
    version = "0.0.1-SNAPSHOT"
}

subprojects {
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "jacoco")
    repositories {
        mavenCentral()
    }

    // Kotlin DSL ile DependencyManagement extension'ını açıkça konfigure et
    the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:3.3.4")
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        finalizedBy("jacocoTestReport")
    }

    tasks.withType<JacocoReport>().configureEach {
        reports { xml.required.set(true); html.required.set(true) }
    }
}

// Ensure integration tests are part of check via :api:itTest
tasks.register("itAll") {
    dependsOn(":api:itTest")
}

tasks.named("check") {
    dependsOn(":api:itTest")
}



plugins {
    id("java")
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":infra"))
    implementation(project(":soap"))
    implementation(project(":ubl"))
    implementation(project(":signer"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-amqp")

    implementation("org.springframework.security:spring-security-oauth2-jose")

    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp:1.42.1")

    // JSON logging
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    runtimeOnly("org.postgresql:postgresql:42.7.4")
    implementation("org.liquibase:liquibase-core")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // PDF
    implementation("com.github.librepdf:openpdf:1.3.40")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter:1.20.1")
    testImplementation("org.testcontainers:postgresql:1.20.1")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.testcontainers:rabbitmq:1.20.1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.test {
    useJUnitPlatform()
}

sourceSets {
    create("it") {
        java.srcDir("src/it/java")
        resources.srcDir("src/it/resources")
        compileClasspath += files(sourceSets["main"].output, configurations.getByName("testRuntimeClasspath"))
        runtimeClasspath += files(output, compileClasspath)
    }
}

configurations {
    getByName("itImplementation").extendsFrom(getByName("testImplementation"))
    getByName("itRuntimeOnly").extendsFrom(getByName("testRuntimeOnly"))
}

tasks.register<Test>("itTest") {
    description = "Runs integration tests."
    group = "verification"
    testClassesDirs = sourceSets["it"].output.classesDirs
    classpath = sourceSets["it"].runtimeClasspath
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "it")
    shouldRunAfter("test")
}

tasks.named<ProcessResources>("processItResources") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// JaCoCo: unit 80% lines, integration 50%
tasks.register<JacocoReport>("jacocoItReport") {
    dependsOn("itTest")
    executionData.setFrom(fileTree(buildDir).include("jacoco/itTest.exec"))
    sourceDirectories.setFrom(files(sourceSets["main"].allSource.srcDirs))
    classDirectories.setFrom(files(sourceSets["main"].output))
    reports { xml.required.set(true); html.required.set(true) }
}

tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    violationRules {
        rule { limit { counter = "LINE"; minimum = "0.80".toBigDecimal() } }
    }
}

tasks.register<JacocoCoverageVerification>("jacocoItCoverageVerification") {
    dependsOn("itTest")
    executionData.setFrom(fileTree(layout.buildDirectory.get().asFile).include("jacoco/itTest.exec"))
    classDirectories.setFrom(files(sourceSets["main"].output))
    violationRules {
        rule { limit { counter = "LINE"; minimum = "0.50".toBigDecimal() } }
    }
}

tasks.named("check") {
    dependsOn("jacocoTestCoverageVerification", "jacocoItCoverageVerification")
}

tasks.named("check") {
    dependsOn("itTest")
}



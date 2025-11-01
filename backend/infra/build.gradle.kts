plugins { id("java-library") }

dependencies {
    implementation(project(":core"))
    // Note: ai-core and ai-ops dependencies are only needed if InvoiceSendFailedConsumer is used
    // They are added via api module, not directly here to avoid build issues
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("io.minio:minio:8.5.10")
    implementation("io.micrometer:micrometer-core")
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(21)) } }

tasks.test { useJUnitPlatform() }
plugins { id("java-library") }

dependencies {
    implementation(project(":core"))
    implementation(project(":ai-core"))
    implementation(project(":ai-ops"))
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("io.minio:minio:8.5.10")
    implementation("io.micrometer:micrometer-core")
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(21)) } }

tasks.test { useJUnitPlatform() }



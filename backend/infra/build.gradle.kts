plugins { id("java-library") }

dependencies {
    implementation(project(":core"))
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("com.fasterxml.jackson.core:jackson-databind")
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(21)) } }

tasks.test { useJUnitPlatform() }



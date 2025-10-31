plugins {
    id("java-library")
}

dependencies {
    api(project(":core"))
    api(project(":ai-core"))
    api(project(":ai-ops"))
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-cache")
    api("com.github.ben-manes.caffeine:caffeine:3.1.8")
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.test {
    useJUnitPlatform()
}

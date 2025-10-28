plugins { id("java-library") }

dependencies {
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
    implementation("org.glassfish.jaxb:jaxb-runtime:4.0.5")
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.0")
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(21)) } }

tasks.test { useJUnitPlatform() }



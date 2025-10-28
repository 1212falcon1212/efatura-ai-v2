plugins { id("java-library") }

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.apache.santuario:xmlsec:4.0.2")
    implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.0")
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(21)) } }

tasks.test { useJUnitPlatform() }



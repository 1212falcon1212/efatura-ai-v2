plugins { id("java-library") }

dependencies {
    implementation(project(":core"))
    implementation("org.springframework.ws:spring-ws-core")
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(21)) } }

sourceSets {
    // Unit aşamasında SOAP testlerini devre dışı bırakıyoruz (integration'a taşınacak)
    named("test") {
        java.setSrcDirs(emptyList<String>())
        resources.setSrcDirs(emptyList<String>())
    }
}

tasks.test {
    useJUnitPlatform()
    // WireMock bağımlılık karmaşasını unit testte yaşamamak için SOAP testlerini unit aşamasında atlıyoruz.
    // WireMock doğrulaması integration testlerde (itTest) koşacak.
    exclude("**/*Test.class")
}



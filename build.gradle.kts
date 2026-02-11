plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.kotlin.plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.9.25"
}

extra["snippetsDir"] = file("build/generated-snippets")
extra["springAiVersion"] = "1.0.0-M6"
println(extra.properties)

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
    annotation("org.springframework.stereotype.Component")
}

group = "com.learner"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    maven(url = uri("https://repo.spring.io/milestone"))
    maven(url = uri("https://repo.spring.io/snapshot"))
}



dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Email
    implementation("org.springframework.boot:spring-boot-starter-mail")

//    // dotenv
//    val dotenvVersion = "6.5.1"
//    implementation("io.github.cdimascio:dotenv-kotlin:$dotenvVersion")

    // JSON
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // JWT
    val jsonVersion = "0.11.2"
    implementation("io.jsonwebtoken:jjwt-impl:$jsonVersion")
    implementation("io.jsonwebtoken:jjwt-jackson:$jsonVersion")
    implementation("io.jsonwebtoken:jjwt-api:$jsonVersion")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    // Kafka
    implementation("org.springframework.kafka:spring-kafka")

    // UUID

    val uuidVersion = "4.0.1"
    implementation("com.fasterxml.uuid:java-uuid-generator:$uuidVersion")

    // Spring AI
//    val springAiVersion = "1.0.0-SNAPSHOT"
//    implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter:$springAiVersion")
//    implementation("org.springframework.ai:spring-ai-vertex-ai-gemini-spring-boot-starter:$springAiVersion")
//    implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter")
//    implementation("org.springframework.ai:spring-ai-bom:1.0.0-SNAPSHOT")
//    implementation(platform("org.springframework.ai:spring-ai-bom:1.0.0-SNAPSHOT"))

    implementation(platform("org.springframework.ai:spring-ai-bom:1.0.0-M7"))
//    implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter")
    implementation("org.springframework.ai:spring-ai-starter-model-openai")




    // Query JDSL
    val jdslVersion = "3.5.5"
    implementation("com.linecorp.kotlin-jdsl:jpql-dsl:$jdslVersion")
    implementation("com.linecorp.kotlin-jdsl:jpql-render:$jdslVersion")
    implementation("com.linecorp.kotlin-jdsl:spring-data-jpa-support:$jdslVersion")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // logging
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.1")

    // Database
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")

    //json
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")


    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    val kotestVersion = "5.9.1"
    val kotestExtension = "1.3.0"
    val h2Version = "2.3.230"
    val mockVersion = "1.13.12"

    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:$kotestExtension")
    testImplementation("com.h2database:h2:$h2Version")
    testImplementation("io.mockk:mockk:$mockVersion")
}

//dependencyManagement {
//    imports {
//        mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
//    }
//}


kotlin {
    compilerOptions.freeCompilerArgs.addAll("-Xjsr305=strict")
}

tasks.named<Test>("test") {
//    outputs.dir(extra["snippetsDir"]!!)
    useJUnitPlatform()
}

tasks.named("asciidoctor") {
//    inputs.dir(extra["snippetsDir"]!!)
    dependsOn("test")
}

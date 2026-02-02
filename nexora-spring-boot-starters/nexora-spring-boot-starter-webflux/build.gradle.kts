plugins {
    id("nexora.java-conventions")
    id("nexora.publishing-conventions")
}

dependencies {
    api(platform(libs.spring.boot.dependencies))

    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // Spring Boot configuration processor for metadata generation
    annotationProcessor(platform(libs.spring.boot.dependencies))
    annotationProcessor(libs.spring.boot.configuration.processor)

    api(project(":nexora-common"))

    // Spring Boot
    api("org.springframework.boot:spring-boot-starter-webflux")

    // Reactor
    api("io.projectreactor:reactor-core")

    // Validation
    api("jakarta.validation:jakarta.validation-api")

    // Jackson
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Test dependencies
    testImplementation(libs.spring.boot.starter.test)
    testImplementation("io.projectreactor:reactor-test")
}

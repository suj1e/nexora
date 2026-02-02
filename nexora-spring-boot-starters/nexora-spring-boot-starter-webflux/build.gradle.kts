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
    api(libs.spring.boot.starter.webflux)

    // Reactor
    api(libs.reactor.core)

    // Validation
    api(libs.jakarta.validation.api)

    // Jackson
    api(libs.jackson.databind)
    api(libs.jackson.datatype.jsr310)

    // Test dependencies
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.reactor.test)
}

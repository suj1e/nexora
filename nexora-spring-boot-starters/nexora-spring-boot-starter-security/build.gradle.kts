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

    // Depend on common module for shared classes (Result, BusinessException, LoginResponse)
    api(project(":nexora-common"))

    // Jackson for JSON serialization
    api(libs.jackson.databind)

    // Spring Security and Web
    api(libs.spring.boot.starter)
    api(libs.spring.boot.starter.security)
    api(libs.spring.boot.starter.web)
    api(libs.spring.security.web)

    // WebFlux for reactive support
    api(libs.spring.boot.starter.webflux)

    // Reactor Core
    api(libs.reactor.core)

    // Jasypt for encryption
    api(libs.jasypt.spring.boot.starter)

    // JWT
    api(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    // Servlet API (optional at compile time)
    compileOnly(libs.jakarta.servlet.api)

    // JPA (optional, for RefreshToken support)
    // Changed to api to make EntityScan and JPA annotations available
    api(libs.spring.boot.starter.data.jpa)
    api(libs.jakarta.persistence.api)

    // Validation API for @Validated and constraint annotations
    compileOnly(libs.spring.boot.starter.validation)

    // Test dependencies
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.jakarta.servlet.api)
}

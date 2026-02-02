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

    // Depend on common module for shared classes
    api(project(":nexora-common"))

    api(libs.spring.boot.starter)
    api(libs.resilience4j.spring.boot3)
    api(libs.resilience4j.all)
    api(libs.jackson.databind)

    // WebFlux for reactive fallback handlers
    api("org.springframework.boot:spring-boot-starter-webflux")

    // Validation API for @Validated and constraint annotations
    compileOnly(libs.spring.boot.starter.validation)

    // Test dependencies
    testImplementation(libs.spring.boot.starter.test)
}

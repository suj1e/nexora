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

    // Jackson for JSON serialization
    api(libs.jackson.databind)

    // Jakarta validation (optional)
    compileOnly("jakarta.validation:jakarta.validation-api:3.0.2")

    // Test dependencies
    testImplementation(libs.spring.boot.starter.test)
}

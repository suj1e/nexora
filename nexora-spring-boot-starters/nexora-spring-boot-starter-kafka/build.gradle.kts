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

    api(libs.spring.boot.starter)
    api(libs.spring.kafka)
    compileOnly(libs.spring.boot.starter.data.jpa)
    compileOnly(libs.jakarta.persistence.api)
    api(libs.jackson.databind)

    // Test dependencies
    testImplementation(libs.spring.boot.starter.test)
}

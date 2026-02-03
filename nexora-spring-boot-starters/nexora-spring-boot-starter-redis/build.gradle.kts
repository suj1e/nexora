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
    api(libs.redisson.spring.boot.starter)
    api(libs.redisson.spring.data)
    api(libs.spring.boot.starter.cache)
    api(libs.caffeine)
    api(libs.jackson.databind)
    api(libs.jackson.datatype.jsr310)

    // Validation API for @Validated and constraint annotations
    compileOnly(libs.spring.boot.starter.validation)

    // Test dependencies
    testImplementation(libs.spring.boot.starter.test)
}

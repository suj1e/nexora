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

    // Spring Boot annotations and auto-configuration
    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springframework.boot:spring-boot-starter-actuator")

    // Micrometer for metrics
    api("io.micrometer:micrometer-core")

    // Test dependencies
    testImplementation(libs.spring.boot.starter.test)
}

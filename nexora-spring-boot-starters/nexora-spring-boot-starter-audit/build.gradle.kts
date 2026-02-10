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
    api(project(":nexora-spring-boot-starter-data-jpa"))

    // Spring AOP (for aspect) - using direct dependency as starter may not be available in Boot 4.0.2 milestone
    api("org.springframework:spring-aop")

    // Spring Web (for HttpServletRequest)
    compileOnly(libs.jakarta.servlet.api)

    // Test dependencies
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.h2)
}

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

    // Spring Data JPA
    api(libs.spring.boot.starter.data.jpa)

    // Spring Data JDBC (optional, for lighter weight)
    compileOnly("org.springframework.boot:spring-boot-starter-data-jdbc")

    // Test dependencies
    testImplementation(libs.spring.boot.starter.test)
    testImplementation("com.h2database:h2")
}

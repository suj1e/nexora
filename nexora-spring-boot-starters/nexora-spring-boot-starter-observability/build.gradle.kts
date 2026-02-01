plugins {
    id("java-library")
}

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://maven.aliyun.com/repository/spring") }
    mavenCentral()
}

dependencies {
    api(platform(libs.spring.boot.dependencies))

    // Depend on common module for shared classes
    api(project(":nexora-common"))

    // Spring Boot annotations and auto-configuration
    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springframework.boot:spring-boot-starter-actuator")

    // Micrometer for metrics
    api("io.micrometer:micrometer-core")

    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // Configuration processor - use full dependency with Spring Boot version
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.5.10")

    // Test dependencies
    testImplementation(libs.spring.boot.starter.test)
}

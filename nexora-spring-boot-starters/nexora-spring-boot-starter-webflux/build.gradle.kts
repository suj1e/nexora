plugins {
    id("java-library")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://maven.aliyun.com/repository/spring") }
    mavenCentral()
}

dependencies {
    api(platform(libs.spring.boot.dependencies))

    api(project(":nexora-common"))

    // Spring Boot
    api("org.springframework.boot:spring-boot-starter-webflux")

    // Reactor
    api("io.projectreactor:reactor-core")

    // Validation
    api("jakarta.validation:jakarta.validation-api")

    // Jackson
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // Configuration processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.5.10")

    // Test dependencies
    testImplementation(libs.spring.boot.starter.test)
    testImplementation("io.projectreactor:reactor-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

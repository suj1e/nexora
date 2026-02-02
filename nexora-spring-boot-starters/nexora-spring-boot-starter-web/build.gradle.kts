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
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // Spring Boot configuration processor for metadata generation
    annotationProcessor(platform(libs.spring.boot.dependencies))
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Depend on common module for shared classes
    api(project(":nexora-common"))

    api(libs.spring.boot.starter.web)
    compileOnly(libs.spring.boot.starter.validation)
    compileOnly("jakarta.persistence:jakarta.persistence-api")

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.validation)
    testImplementation("jakarta.persistence:jakarta.persistence-api")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

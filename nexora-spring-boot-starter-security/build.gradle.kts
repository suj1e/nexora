plugins {
    id("java-library")
}

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://maven.aliyun.com/repository/spring") }
    mavenCentral()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    api(platform(libs.spring.boot.dependencies))
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // Depend on common module for shared classes (Result, BusinessException, LoginResponse)
    api(project(":nexora-common"))

    // Jackson for JSON serialization
    api(libs.jackson.databind)

    // Spring Security and Web
    api(libs.spring.boot.starter)
    api(libs.spring.boot.starter.security)
    api(libs.spring.boot.starter.web)
    api(libs.spring.security.web)

    // Jasypt for encryption
    api(libs.jasypt.spring.boot.starter)

    // JWT
    api(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    // Servlet API (optional at compile time)
    compileOnly(libs.jakarta.servlet.api)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.jakarta.servlet.api)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

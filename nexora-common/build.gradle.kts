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

    // Jackson for JSON serialization
    api(libs.jackson.databind)

    // Lombok (optional at compile time)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // Jakarta validation (optional)
    compileOnly("jakarta.validation:jakarta.validation-api:3.0.2")

    // Testing
    testImplementation(libs.spring.boot.starter.test)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

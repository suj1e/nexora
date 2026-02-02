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

    // Spring Boot configuration processor for metadata generation
    annotationProcessor(platform(libs.spring.boot.dependencies))
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    api(libs.spring.boot.starter)
    api(libs.spring.boot.starter.data.redis)
    api(libs.spring.boot.starter.cache)
    api(libs.caffeine)
    api(libs.lettuce.core)
    api(libs.jackson.databind)
    api(libs.jackson.datatype.jsr310)

    // Validation API for @Validated and constraint annotations
    compileOnly(libs.spring.boot.starter.validation)

    testImplementation(libs.spring.boot.starter.test)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

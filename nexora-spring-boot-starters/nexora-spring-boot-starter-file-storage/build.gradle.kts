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
    api(libs.spring.boot.starter.web)
    api(libs.spring.boot.starter.validation)

    // Optional cloud storage dependencies
    compileOnly("com.aliyun.oss:aliyun-sdk-oss:3.17.4")
    compileOnly("software.amazon.awssdk:s3:2.25.0")
    compileOnly("io.minio:minio:8.5.7")
    api("commons-io:commons-io:2.15.1")

    // Test dependencies
    testImplementation(libs.spring.boot.starter.test)
}

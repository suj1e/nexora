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

    // Depend on common module for shared classes
    api(project(":nexora-common"))

    // Spring Data JPA
    api(libs.spring.boot.starter.data.jpa)

    // Spring Data JDBC (optional, for lighter weight)
    compileOnly("org.springframework.boot:spring-boot-starter-data-jdbc")

    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // Configuration processor - use full dependency with Spring Boot version
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.5.10")

    // Testing
    testImplementation(libs.spring.boot.starter.test)
    testImplementation("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

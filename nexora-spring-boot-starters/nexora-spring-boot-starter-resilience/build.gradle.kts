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

    // Depend on common module for shared classes
    api(project(":nexora-common"))

    api(libs.spring.boot.starter)
    api(libs.resilience4j.spring.boot3)
    api(libs.resilience4j.all)
    api(libs.jackson.databind)

    // WebFlux for reactive fallback handlers
    api("org.springframework.boot:spring-boot-starter-webflux")

    testImplementation(libs.spring.boot.starter.test)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

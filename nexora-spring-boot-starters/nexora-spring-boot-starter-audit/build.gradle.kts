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
    api(project(":nexora-spring-boot-starter-data-jpa"))

    // Spring AOP (for aspect)
    api("org.springframework.boot:spring-boot-starter-aop")

    // Spring Web (for HttpServletRequest)
    compileOnly(libs.jakarta.servlet.api)

    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // Configuration processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.5.10")

    // Testing
    testImplementation(libs.spring.boot.starter.test)
    testImplementation("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

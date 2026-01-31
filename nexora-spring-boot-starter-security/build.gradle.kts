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

    api(libs.spring.boot.starter)
    api(libs.spring.boot.starter.security)
    api(libs.spring.security.web)
    api(libs.jasypt.spring.boot.starter)
    api(libs.jjwt.api)
    compileOnly(libs.jakarta.servlet.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.jakarta.servlet.api)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

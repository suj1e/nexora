plugins {
    java
    `java-library`
    `maven-publish`
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    withSourcesJar()

    // Javadoc generation is disabled due to doclint configuration issues
    // Can be enabled per-module if needed
    // withJavadocJar()
}

tasks.withType<Test> {
    useJUnitPlatform()

    // Optimize test execution
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)

    // JVM args for better diagnostics
    jvmArgs("-XX:+HeapDumpOnOutOfMemoryError")

    // Ensure tests are run consistently
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
    options.encoding = "UTF-8"
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

// Add JUnit platform launcher for test execution
dependencies {
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

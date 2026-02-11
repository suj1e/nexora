import org.gradle.external.javadoc.CoreJavadocOptions

plugins {
    java
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

// Configure javadoc to be less strict
tasks.withType<Javadoc>().configureEach {
    options {
        (this as CoreJavadocOptions).apply {
            addStringOption("Xdoclint:none", "-quiet")
            addStringOption("encoding", "UTF-8")
            addBooleanOption("html5", true)
        }
    }
    // Exclude generated sources and problematic files
    exclude("**/generated/**")
    isFailOnError = false
}

// Disable javadoc tasks entirely (let gradle-maven-publish-plugin handle it)
// This avoids Lombok annotation processing issues in javadoc
tasks.named("javadoc") {
    enabled = false
}

tasks.withType<Test> {
    useJUnitPlatform()

    // Optimize test execution
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)

    // JVM args for better diagnostics
    jvmArgs("-XX:+HeapDumpOnOutOfMemoryError")
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

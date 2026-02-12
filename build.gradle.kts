plugins {
    id("java")
    alias(libs.plugins.spring.boot) apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

// Load local.properties for credentials (gitignored)
val localPropsFile = rootProject.file("local.properties")
if (localPropsFile.exists()) {
    localPropsFile.readLines().forEach { line ->
        val trimmed = line.trim()
        if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
            val idx = trimmed.indexOf('=')
            if (idx > 0) {
                val key = trimmed.substring(0, idx).trim()
                val value = trimmed.substring(idx + 1).trim()
                if (!project.extensions.extraProperties.has(key)) {
                    project.extensions.extraProperties.set(key, value)
                }
            }
        }
    }
}

allprojects {
    group = "io.github.suj1e"
    version = project.findProperty("projectVersion")?.toString() ?: (property("version") as String)

    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/spring") }
        mavenCentral()
    }
}

// Task to publish all subprojects
tasks.register("publishAll") {
    group = "publishing"
    description = "Publish all subprojects to configured repositories"
    dependsOn(subprojects.map { "${it.path}:publish" })
}

// Task to verify all subprojects
tasks.register("verifyAll") {
    group = "verification"
    description = "Run all verification tasks including tests and checks"
    dependsOn(subprojects.map { "${it.path}:check" })
}

// Task for full CI build
tasks.register("ciBuild") {
    group = "build"
    description = "Run full CI build including clean, build, and verify"
    dependsOn(":clean", ":build", ":verifyAll")
}

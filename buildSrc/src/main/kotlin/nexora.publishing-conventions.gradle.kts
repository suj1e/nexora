import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

plugins {
    `maven-publish`
}

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set(project.name)
                description.set("Nexora Spring Boot Starters - ${project.name}")
                url.set("https://github.com/suj1e/nexora")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("suj1e")
                        name.set("SuJie")
                        email.set("sujie@nexora.io")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/suj1e/nexora.git")
                    developerConnection.set("scm:git:ssh://github.com/suj1e/nexora.git")
                    url.set("https://github.com/suj1e/nexora")
                }
            }
        }
    }

    repositories {
        val isSnapshot = project.version.toString().endsWith("-SNAPSHOT")

        fun getProperty(names: List<String>, defaultValue: String): String {
            for (name in names) {
                val projectProp = project.findProperty(name)
                if (projectProp != null) return projectProp.toString()

                val systemProp = System.getProperty(name)
                if (systemProp != null) return systemProp

                val envProp = System.getenv(name)
                if (envProp != null) return envProp
            }
            return defaultValue
        }

        val mavenUsername = getProperty(listOf("MAVEN_USERNAME", "OSSRH_USERNAME"), "")
        val mavenPassword = getProperty(listOf("MAVEN_PASSWORD", "OSSRH_TOKEN"), "")

        // Maven Central Portal (new API)
        if (mavenUsername.isNotEmpty() && mavenPassword.isNotEmpty()) {
            maven {
                name = "MavenCentral"
                if (isSnapshot) {
                    url = uri("https://central.sonatype.com/repository/maven-snapshots/")
                } else {
                    url = uri("https://central.sonatype.com/repository/maven-releases/")
                }
                credentials {
                    username = mavenUsername
                    password = mavenPassword
                }
            }
        }
    }
}

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
                        id.set("sujie")
                        name.set("SuJie")
                        email.set("sujie@nexora.io")
                        timezone.set("Asia/Shanghai")
                        url.set("https://github.com/suj1e")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com:suj1e/nexora.git")
                    developerConnection.set("scm:git:ssh://github.com:suj1e/nexora.git")
                    url.set("https://github.com/suj1e/nexora")
                }

                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/suj1e/nexora/issues")
                }

                ciManagement {
                    system.set("GitHub Actions")
                    url.set("https://github.com/suj1e/nexora/actions")
                }
            }
        }
    }

    repositories {
        // Determine if this is a snapshot version
        val isSnapshot = project.version.toString().endsWith("-SNAPSHOT")

        // Helper function to get property from multiple possible sources
        fun getProperty(names: List<String>, defaultValue: String): String {
            for (name in names) {
                // Try project property first
                val projectProp = project.findProperty(name)
                if (projectProp != null) return projectProp.toString()

                // Try system property (passed with -P)
                val systemProp = System.getProperty(name)
                if (systemProp != null) return systemProp

                // Try environment variable
                val envProp = System.getenv(name)
                if (envProp != null) return envProp

                // Try environment variable with alternate naming (e.g., YUNXIAO_SNAPSHOT_URL)
                val envAlt = System.getenv(
                    name.uppercase()
                        .replace("Yunxiao", "YUNXIAO")
                        .replace("Codeup", "CODEUP")
                )
                if (envAlt != null) return envAlt
            }
            return defaultValue
        }

        // Snapshot repository - Yunxiao
        if (isSnapshot) {
            maven {
                name = "YunxiaoSnapshot"
                val snapshotUrl = getProperty(
                    listOf("YunxiaoSnapshotRepositoryUrl", "yunxiaoSnapshotRepositoryUrl",
                          "codeupSnapshotUrl", "YUNXIAO_SNAPSHOT_URL"),
                    "https://packages.aliyun.com/maven/repository/snapshot"
                )
                url = uri(snapshotUrl)
                credentials {
                    username = getProperty(
                        listOf("YUNXIAO_USERNAME", "yunxiaoUsername",
                              "codeupUsername", "CODEUP_USERNAME"),
                        ""
                    )
                    password = getProperty(
                        listOf("YUNXIAO_PASSWORD", "yunxiaoPassword",
                              "codeupPassword", "CODEUP_PASSWORD"),
                        ""
                    )
                }
            }
        }

        // Release repository - Yunxiao
        if (!isSnapshot) {
            maven {
                name = "YunxiaoRelease"
                val releaseUrl = getProperty(
                    listOf("YunxiaoReleaseRepositoryUrl", "yunxiaoReleaseRepositoryUrl",
                          "codeupReleaseUrl", "YUNXIAO_RELEASE_URL"),
                    "https://packages.aliyun.com/maven/repository/release"
                )
                url = uri(releaseUrl)
                credentials {
                    username = getProperty(
                        listOf("YUNXIAO_USERNAME", "yunxiaoUsername",
                              "codeupUsername", "CODEUP_USERNAME"),
                        ""
                    )
                    password = getProperty(
                        listOf("YUNXIAO_PASSWORD", "yunxiaoPassword",
                              "codeupPassword", "CODEUP_PASSWORD"),
                        ""
                    )
                }
            }
        }
    }
}

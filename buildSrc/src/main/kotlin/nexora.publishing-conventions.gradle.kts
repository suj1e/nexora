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
        // Determine if this is a snapshot version
        val isSnapshot = project.version.toString().endsWith("-SNAPSHOT")

        // Helper function to get property from multiple possible sources
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

        // Maven Central (Sonatype) - Snapshot
        if (isSnapshot) {
            val ossrhUsername = getProperty(listOf("OSSRH_USERNAME", "MAVEN_USERNAME"), "")
            val ossrhPassword = getProperty(listOf("OSSRH_TOKEN", "OSSRH_PASSWORD", "MAVEN_PASSWORD"), "")

            if (ossrhUsername.isNotEmpty() && ossrhPassword.isNotEmpty()) {
                maven {
                    name = "OssrhSnapshot"
                    url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                    credentials {
                        username = ossrhUsername
                        password = ossrhPassword
                    }
                }
            }
        }

        // Maven Central (Sonatype) - Release (via staging)
        if (!isSnapshot) {
            val ossrhUsername = getProperty(listOf("OSSRH_USERNAME", "MAVEN_USERNAME"), "")
            val ossrhPassword = getProperty(listOf("OSSRH_TOKEN", "OSSRH_PASSWORD", "MAVEN_PASSWORD"), "")

            if (ossrhUsername.isNotEmpty() && ossrhPassword.isNotEmpty()) {
                maven {
                    name = "OssrhStaging"
                    url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                    credentials {
                        username = ossrhUsername
                        password = ossrhPassword
                    }
                }
            }
        }

        // Aliyun Yunxiao - Snapshot (fallback for Chinese users)
        if (isSnapshot) {
            val snapshotUrl = getProperty(
                listOf("YUNXIAO_SNAPSHOT_URL", "YunxiaoSnapshotRepositoryUrl"),
                ""
            )
            if (snapshotUrl.isNotEmpty()) {
                maven {
                    name = "YunxiaoSnapshot"
                    url = uri(snapshotUrl)
                    credentials {
                        username = getProperty(listOf("YUNXIAO_USERNAME"), "")
                        password = getProperty(listOf("YUNXIAO_PASSWORD"), "")
                    }
                }
            }
        }

        // Aliyun Yunxiao - Release (fallback for Chinese users)
        if (!isSnapshot) {
            val releaseUrl = getProperty(
                listOf("YUNXIAO_RELEASE_URL", "YunxiaoReleaseRepositoryUrl"),
                ""
            )
            if (releaseUrl.isNotEmpty()) {
                maven {
                    name = "YunxiaoRelease"
                    url = uri(releaseUrl)
                    credentials {
                        username = getProperty(listOf("YUNXIAO_USERNAME"), "")
                        password = getProperty(listOf("YUNXIAO_PASSWORD"), "")
                    }
                }
            }
        }
    }
}

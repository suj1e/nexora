import org.gradle.api.publish.PublishingExtension

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
        // Snapshot repository - Yunxiao
        maven {
            name = "YunxiaoSnapshot"
            val snapshotUrl = project.findProperty("YUNXIAO_SNAPSHOT_URL") as String?
                ?: project.findProperty("yunxiaoSnapshotRepositoryUrl") as String?
                ?: System.getenv("YUNXIAO_SNAPSHOT_URL")
                ?: "https://packages.aliyun.com/maven/repository/2381107-snapshotXXXXX"
            url = uri(snapshotUrl)
            credentials {
                username = project.findProperty("YUNXIAO_USERNAME") as String?
                    ?: System.getenv("YUNXIAO_USERNAME")
                    ?: ""
                password = project.findProperty("YUNXIAO_PASSWORD") as String?
                    ?: System.getenv("YUNXIAO_PASSWORD")
                    ?: ""
            }
        }

        // Release repository - Yunxiao
        maven {
            name = "YunxiaoRelease"
            val releaseUrl = project.findProperty("YUNXIAO_RELEASE_URL") as String?
                ?: project.findProperty("yunxiaoReleaseRepositoryUrl") as String?
                ?: System.getenv("YUNXIAO_RELEASE_URL")
                ?: "https://packages.aliyun.com/maven/repository/2381107-releaseXXXXX"
            url = uri(releaseUrl)
            credentials {
                username = project.findProperty("YUNXIAO_USERNAME") as String?
                    ?: System.getenv("YUNXIAO_USERNAME")
                    ?: ""
                password = project.findProperty("YUNXIAO_PASSWORD") as String?
                    ?: System.getenv("YUNXIAO_PASSWORD")
                    ?: ""
            }
        }
    }
}

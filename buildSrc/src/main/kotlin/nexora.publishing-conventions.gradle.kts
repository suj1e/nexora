plugins {
    id("com.vanniktech.maven.publish")
}

configure<com.vanniktech.maven.publish.MavenPublishBaseExtension> {
    coordinates(
        groupId = project.group.toString(),
        artifactId = project.name,
        version = project.version.toString()
    )

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

    // Central Portal API for releases only
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)

    // Sign all publications (required for releases only, snapshots don't need signing)
    if (!project.version.toString().endsWith("-SNAPSHOT")) {
        signAllPublications()
    }
}

// Configure snapshot repository separately (Portal API doesn't support snapshots)
// Snapshots are deployed directly to: https://central.sonatype.com/repository/maven-snapshots/
publishing {
    repositories {
        maven {
            name = "MavenCentralSnapshots"
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
            credentials {
                username = project.findProperty("mavenCentralUsername")?.toString()
                    ?: System.getenv("ORG_GRADLE_PROJECT_mavenCentralUsername")
                    ?: ""
                password = project.findProperty("mavenCentralPassword")?.toString()
                    ?: System.getenv("ORG_GRADLE_PROJECT_mavenCentralPassword")
                    ?: ""
            }
        }
    }
}

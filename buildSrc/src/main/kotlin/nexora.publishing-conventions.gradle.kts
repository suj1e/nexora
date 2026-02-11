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

    // Configure Central Portal
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}

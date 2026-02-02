import org.gradle.api.publish.PublishingExtension
import org.gradle.plugins.signing.Sign

plugins {
    signing
}

// Configure signing for publication
configure<SigningExtension> {
    // Only sign non-SNAPSHOT versions
    setRequired {
        !project.version.toString().endsWith("-SNAPSHOT")
    }

    // Use in-memory PGP keys from environment variables
    val signingKey = System.getenv("PGP_SIGNING_KEY")
    val signingPassword = System.getenv("PGP_SIGNING_PASSWORD")

    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)

        // Sign the maven publication
        val publishing = project.extensions.getByName("publishing") as PublishingExtension
        sign(publishing.publications.getByName("maven"))
    }
}

// Only execute signing task if key is available
tasks.withType<Sign>().configureEach {
    onlyIf {
        System.getenv("PGP_SIGNING_KEY") != null &&
        System.getenv("PGP_SIGNING_PASSWORD") != null &&
        !project.version.toString().endsWith("-SNAPSHOT")
    }
}

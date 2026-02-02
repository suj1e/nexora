rootProject.name = "nexora"

// Include common module
include("nexora-common")

// Explicitly declare all starter modules for better IDE support
// and build predictability (instead of dynamic scanning)
listOf(
    "nexora-spring-boot-starter-web",
    "nexora-spring-boot-starter-webflux",
    "nexora-spring-boot-starter-data-jpa",
    "nexora-spring-boot-starter-redis",
    "nexora-spring-boot-starter-kafka",
    "nexora-spring-boot-starter-resilience",
    "nexora-spring-boot-starter-security",
    "nexora-spring-boot-starter-file-storage",
    "nexora-spring-boot-starter-audit",
    "nexora-spring-boot-starter-observability"
).forEach { moduleName ->
    include(moduleName)
    project(":$moduleName").projectDir = file("nexora-spring-boot-starters/$moduleName")
}

rootProject.name = "nexora-spring-boot-starters"

// Include common module
include("nexora-common")

// Dynamically include all modules with nexora-spring-boot-starter- prefix
rootDir.listFiles { file -> file.isDirectory && !file.name.startsWith(".") }
    ?.filter { it.name.startsWith("nexora-spring-boot-starter-") }
    ?.sorted()
    ?.forEach { include(it.name) }

rootProject.name = "nexora-spring-boot-starters"

// Dynamically include all modules with nexora-spring-boot-starter- prefix
rootDir.listFiles { file -> file.isDirectory && !file.name.startsWith(".") }
    ?.filter { it.name.startsWith("nexora-spring-boot-starter-") }
    ?.sorted()
    ?.forEach { include(it.name) }

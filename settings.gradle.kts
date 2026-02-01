rootProject.name = "nexora"

// Include common module
include("nexora-common")

// Dynamically include all starter modules
rootDir.listFiles { file -> file.isDirectory && !file.name.startsWith(".") }
    ?.filter { it.name == "nexora-spring-boot-starters" }
    ?.first()
    ?.listFiles { it.isDirectory && !it.name.startsWith(".") }
    ?.filter { it.name.startsWith("nexora-spring-boot-starter-") }
    ?.sorted()
    ?.forEach {
        include("nexora-spring-boot-starters:${it.name}")
    }

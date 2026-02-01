rootProject.name = "nexora"

// Include common module
include("nexora-common")

// Dynamically include all starter modules as direct subprojects
val startersDir = rootDir.listFiles { file -> file.isDirectory && !file.name.startsWith(".") }
    ?.find { it.name == "nexora-spring-boot-starters" }

startersDir
    ?.listFiles { it.isDirectory && !it.name.startsWith(".") }
    ?.filter { it.name.startsWith("nexora-spring-boot-starter-") }
    ?.sorted()
    ?.forEach { starterDir ->
        include(starterDir.name)
        project(":${starterDir.name}").projectDir = starterDir
    }

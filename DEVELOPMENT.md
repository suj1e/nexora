# Development Guide

This guide provides comprehensive information for contributors to the Nexora Spring Boot Starters project.

## Table of Contents

- [Project Overview](#project-overview)
- [Prerequisites](#prerequisites)
- [Build System](#build-system)
- [Project Structure](#project-structure)
- [Development Workflow](#development-workflow)
- [Adding a New Starter](#adding-a-new-starter)
- [Testing](#testing)
- [Release Process](#release-process)
- [Troubleshooting](#troubleshooting)

## Project Overview

**Nexora** is a multi-module Gradle project providing Spring Boot starters for microservice infrastructure. Each starter uses Spring Boot's auto-configuration mechanism for zero-configuration functionality.

**Group ID:** `com.nexora`
**Version:** `1.0.0`
**Java Version:** 21

### Available Starters

| Starter | Description | Key Features |
|---------|-------------|--------------|
| nexora-spring-boot-starter-web | Web API essentials | GlobalExceptionHandler, Result<T>, BusinessException |
| nexora-spring-boot-starter-webflux | Reactive Web support | GlobalWebFluxExceptionHandler, reactive types |
| nexora-spring-boot-starter-data-jpa | JPA with auditing | BaseEntity, auditing, EntityScan |
| nexora-spring-boot-starter-redis | Multi-level caching | Caffeine L1 + Redis L2, cache helpers |
| nexora-spring-boot-starter-kafka | Event-driven architecture | EventPublisher, DLQ, Outbox pattern |
| nexora-spring-boot-starter-resilience | Resilience patterns | CircuitBreaker, Retry, TimeLimiter |
| nexora-spring-boot-starter-security | JWT & encryption | JwtTokenProvider, Jasypt integration |
| nexora-spring-boot-starter-file-storage | Multi-cloud storage | Aliyun OSS, AWS S3, MinIO support |
| nexora-spring-boot-starter-audit | Entity auditing | @CreatedDate, @LastModifiedDate, @CreatedBy |
| nexora-spring-boot-starter-observability | Metrics & tracing | Micrometer integration |

## Prerequisites

- **JDK 21** - [Eclipse Temurin](https://adoptium.net/) or [Amazon Corretto](https://corretto.aws/) recommended
- **Gradle 9.0+** - Project uses Gradle wrapper (no installation needed)
- **Docker** (optional) - For container-based testing

## Build System

### Gradle Convention Plugins

The project uses buildSrc convention plugins to eliminate duplication:

- **nexora.java-conventions** - Java 21, source jars, test configuration
- **nexora.publishing-conventions** - Maven publishing with rich POM
- **nexora.signing-conventions** - GPG signing for releases

### Version Catalog

All dependencies are managed via `gradle/libs.versions.toml`:

```toml
[versions]
spring-boot = "3.5.10"
# ...

[libraries]
spring-boot-starter-web = { module = "org.springframework.boot:spring-boot-starter-web" }
# ...
```

### Build Commands

```bash
# Build all modules
./gradlew build

# Run tests
./gradlew check

# Publish to local Maven repository
./gradlew publishToMavenLocal

# Full CI build
./gradlew ciBuild

# Publish to remote repository
./deploy.sh --type snapshot
```

## Project Structure

```
nexora/
├── buildSrc/                              # Convention plugins
│   └── src/main/kotlin/
│       ├── nexora.java-conventions.gradle.kts
│       ├── nexora.publishing-conventions.gradle.kts
│       └── nexora.signing-conventions.gradle.kts
├── nexora-common/                         # Shared utilities
│   └── src/main/java/com/nexora/common/
│       ├── api/                            # Result<T>, BusinessException
│       ├── model/                         # LoginResponse, ProblemDetail
│       └── security/                       # PasswordUtil
├── gradle/
│   ├── libs.versions.toml                 # Version catalog
│   └── verification-metadata.xml          # Dependency checksums
├── nexora-spring-boot-starters/          # Starters directory
│   ├── nexora-spring-boot-starter-web/
│   │   ├── build.gradle.kts
│   │   └── src/main/java/com/nexora/web/
│   │       ├── autoconfigure/              # AutoConfiguration classes
│   │       ├── handler/                     # Exception handlers
│   │       └── properties/                  # ConfigurationProperties
│   └── ...
└── .github/workflows/                     # CI/CD
```

## Development Workflow

### 1. Setup Development Environment

```bash
# Clone the repository
git clone https://github.com/suj1e/nexora.git
cd nexora

# Run initial build
./gradlew build
```

### 2. Make Changes

```bash
# Create a feature branch
git checkout -b feature/my-starter

# Make your changes...
# Edit code, add tests, etc.

# Run tests and checks
./gradlew check

# Build and verify
./gradlew build
```

### 3. Commit Changes

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
feat: add new feature
fix: fix bug in existing functionality
docs: update documentation
refactor: code refactoring
test: add or update tests
chore: maintenance tasks
```

### 4. Create Pull Request

```bash
git push origin feature/my-starter
# Then create PR on GitHub
```

## Adding a New Starter

### Step 1: Create Module Directory

```bash
mkdir nexora-spring-boot-starters/nexora-spring-boot-starter-mystarter
```

### Step 2: Create build.gradle.kts

```kotlin
plugins {
    id("nexora.java-conventions")
    id("nexora.publishing-conventions")
}

dependencies {
    api(platform(libs.spring.boot.dependencies))

    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // Spring Boot configuration processor
    annotationProcessor(platform(libs.spring.boot.dependencies))
    annotationProcessor(libs.spring.boot.configuration.processor)

    // Depend on common module for shared classes
    api(project(":nexora-common"))

    // Add your dependencies here
    api(libs.spring.boot.starter)

    // Test dependencies
    testImplementation(libs.spring.boot.starter.test)
}
```

### Step 3: Create Auto-Configuration

```kotlin
// src/main/java/com/nexora/mystarter/autoconfigure/MyStarterAutoConfiguration.java
package com.nexora.mystarter.autoconfigure;

import com.nexora.mystarter.properties.MyStarterProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ConditionalOnClass(MyService.class) // Only activate if class is present
@EnableConfigurationProperties(MyStarterProperties.class)
@ComponentScan(basePackageClasses = MyService.class)
public class MyStarterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MyService myService(MyStarterProperties properties) {
        return new MyService(properties);
    }
}
```

### Step 4: Create Properties Class

```kotlin
// src/main/java/com/nexora/mystarter/properties/MyStarterProperties.java
package com.nexora.mystarter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "nexora.mystarter")
public class MyStarterProperties {

    @NotBlank
    private String enabled = "true";

    @Min(1)
    private int maxConnections = 10;

    // Getters and setters...
}
```

### Step 5: Register Auto-Configuration

Create `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`:

```
com.nexora.mystarter.autoconfigure.MyStarterAutoConfiguration
```

### Step 6: Add to settings.gradle.kts

```kotlin
include("nexora-spring-boot-starter-mystarter")
project(":nexora-spring-boot-starter-mystarter").projectDir =
    file("nexora-spring-boot-starters/nexora-spring-boot-starter-mystarter")
```

### Step 7: Write Tests

```kotlin
@ExtendWith(MockitoExtension.class)
class MyStarterAutoConfigurationTest {

    @Test
    void shouldConfigureMyService() {
        // Test your auto-configuration
    }
}
```

## Testing

### Running Tests

```bash
# All tests
./gradlew test

# Specific module tests
./gradlew :nexora-spring-boot-starter-web:test

# With coverage
./gradlew test jacocoTestReport
```

### Test Structure

```
src/test/java/com/nexora/
├── autoconfigure/     # Auto-configuration tests
│   └── MyAutoConfigurationTest.java
├── handler/           # Handler tests
└── properties/         # Properties tests
```

### Test Best Practices

- Use JUnit 5 with `@ExtendWith(MockitoExtension.class)`
- Mock external dependencies
- Test both enabled and disabled configurations
- Use `@SpringBootTest` for integration tests

## Release Process

### Snapshot Releases

Snapshots are automatically published on push to `main` branch.

### Release Releases

1. **Create a version tag:**
   ```bash
   git tag -a v1.0.1 -m "Release v1.0.1"
   git push origin v1.0.1
   ```

2. **GitHub Actions will:**
   - Validate the semver format
   - Run full test suite
   - Publish to release repository
   - Sign artifacts with GPG (if configured)

### Version Bump

Update version in `gradle.properties`:

```properties
version=1.0.1-SNAPSHOT
```

## Troubleshooting

### Build Issues

**Issue:** Configuration cache error after updating dependencies

**Solution:**
```bash
./gradlew clean --no-configuration-cache
./gradlew build
```

**Issue:** Dependency verification failed

**Solution:**
```bash
# Regenerate verification metadata
./gradlew --write-verification-metadata sha256 help
```

### IDE Setup

**IntelliJ IDEA:**

1. Import project as Gradle project
2. Enable "Annotation Processing" for Lombok
3. Set JDK to 21
4. Enable "Delegate IDE build/run actions to Gradle"

**VS Code:**
1. Install "Extension Pack for Java"
2. Install "Gradle for Java"
3. Set `java.jdt.ls.java.home` to JDK 21 path

### Dependency Conflicts

**Issue:** Two versions of the same dependency

**Solution:**
```bash
./gradlew dependencyInsight --dependency io.projectreactor:reactor-core
```

### Publishing Issues

**Issue:** 401 Unauthorized when publishing

**Solution:** Check credentials in `~/.gradle/gradle.properties`:

```properties
yunxiaoUsername=your_username
yunxiaoPassword=your_password
yunxiaoSnapshotRepositoryUrl=https://...
yunxiaoReleaseRepositoryUrl=https://...
```

### Common Gotchas

1. **Convention plugins not loading:** Run `./gradlew clean build` to rebuild buildSrc
2. **Tests not running:** Ensure `./gradlew test --no-daemon` (no cached daemon)
3. **Version catalog changes not recognized:** Run `./gradlew --refresh-dependencies`
4. **Configuration cache problems:** Delete `.gradle/configuration-cache` directory

## Additional Resources

- [Gradle User Guide](https://docs.gradle.org/9.0.0/userguide/userguide.html)
- [Spring Boot Auto-Configuration](https://docs.spring.io/spring-boot/docs/3.5.10/reference/html/features/developing-auto-configuration.html)
- [Custom Starters Documentation](https://docs.spring.io/spring-boot/docs/3.5.10/reference/html/feature/deploying.html#feature.boot.custom-starter)
- [Version Catalog Gradle Plugin](https://docs.gradle.org/current/userguide/platforms.html#sub:toml-dependency-catalogs)

## Getting Help

- **Issues:** Create an issue on [GitHub](https://github.com/suj1e/nexora/issues)
- **Discussions:** Start a discussion on [GitHub Discussions](https://github.com/suj1e/nexora/discussions)
- **Documentation:** See [CLAUDE.md](CLAUDE.md) for architectural overview

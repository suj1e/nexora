# Changelog

All notable changes to the Nexora project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 1.0.0 (2026-02-10)


### Features

* add file storage starter with multi-backend support ([d64192f](https://github.com/suj1e/nexora/commit/d64192f3363b8245895f85feb0536651f85fffa1))
* add Java 21 features and RFC 7807 ProblemDetail support ([e70e1dd](https://github.com/suj1e/nexora/commit/e70e1ddea8896416608ad568f3551f30d6779908))
* add nexora-common module and enhance security starter ([940f016](https://github.com/suj1e/nexora/commit/940f016b1e6efb6a9ddb51afc1b9c37352c258bd))
* add SMS login feature to security starter ([44afa4a](https://github.com/suj1e/nexora/commit/44afa4a6eaf95683dc752c2ab4868608cfc3a3d9))
* **redis:** replace Spring Data Redis with Redisson 4.1.0 ([72e7550](https://github.com/suj1e/nexora/commit/72e755048281f0428d585fe87858f9b63c1e7d9f))


### Bug Fixes

* add default version handling in deploy.sh ([6696ecd](https://github.com/suj1e/nexora/commit/6696ecd0fdee38abc6fd30b7c6de35d297a9e3b2))
* change enforcedPlatform to platform for Gradle 9.0 compatibility ([f00f1d0](https://github.com/suj1e/nexora/commit/f00f1d0f33d64aebd5f7008a1aba82e7452d4e87))
* compatibility with Spring Boot 4.0.2 milestone API changes ([83b55e0](https://github.com/suj1e/nexora/commit/83b55e0b1f34a476ce45a3cd60dd9e94828aa805))
* correct deploy.sh color codes and gradle detection ([b11317b](https://github.com/suj1e/nexora/commit/b11317b6f87c40da3ecb68da490397717c167188))
* correct module names in documentation ([f4d1513](https://github.com/suj1e/nexora/commit/f4d1513e81473e2683f4023116d4b4a9ab845ffb))
* improve deploy.sh color handling and remove escape codes ([048208b](https://github.com/suj1e/nexora/commit/048208bbbf91382288274a132f00a14e80fe2be9))
* **redis:** remove redisson-spring-boot-starter to avoid auto-config conflict ([48df404](https://github.com/suj1e/nexora/commit/48df404ad90a30656878f6b72aeed5115cf07143))
* remove --scan flag from deploy.sh to fix dependency verification error ([ced2b36](https://github.com/suj1e/nexora/commit/ced2b36e244539ea5501b4464dda1f7150d2c783))
* resolve build and test failures across all modules ([61031ca](https://github.com/suj1e/nexora/commit/61031ca91a430c7b1f74fa71e119568ed83b4272))
* resolve deploy.sh publishing issues ([535bd06](https://github.com/suj1e/nexora/commit/535bd0630b2bb696ca0d94a46963d52cfd4f6108))
* workflows use gradle instead of gradlew wrapper ([8766676](https://github.com/suj1e/nexora/commit/87666769a1b13ad81071856c3fe5728076b00497))


### Documentation

* enhance project documentation and deployment automation ([b715a35](https://github.com/suj1e/nexora/commit/b715a35547e8bc8b38900c3ee53712bf679fcec1))
* unify project naming to 'Nexora' across all documentation ([c374942](https://github.com/suj1e/nexora/commit/c374942463923fa917976b71afca5f263a5c2fa6))
* update README and CLAUDE.md based on actual code ([be5fcdd](https://github.com/suj1e/nexora/commit/be5fcddae28cb76db164ff17e01a80f61347e51d))

## [Unreleased]

### Planned
- Support for Spring Boot 3.6.x
- Additional cloud storage providers (Azure Blob, Google Cloud Storage)
- Distributed tracing enhancements
- Metrics and observability improvements

## [1.0.0] - 2025-01-XX

### Added
- Initial release of Nexora

#### nexora-spring-boot-starter-web
- Global exception handler with `@RestControllerAdvice`
- Unified API response format `Result<T>`
- Business exception base class `BusinessException`
- Automatic exception to HTTP status code mapping
- Request/response logging aspect

#### nexora-spring-boot-starter-webflux
- Reactive web support with WebFlux
- `GlobalWebFluxExceptionHandler` for reactive streams
- Reactive `Result<T>` support
- Reactive exception handling and mapping

#### nexora-spring-boot-starter-data-jpa
- JPA auditing support with `BaseEntity`
- Automatic `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, `@LastModifiedBy` population
- Entity scanning configuration
- Soft delete support (via `@SQLDelete`)

#### nexora-spring-boot-starter-redis
- Multi-level caching (Caffeine L1 + Redis L2)
- JSON serialization with Jackson `JavaTimeModule`
- Per-cache TTL configuration
- Cache key prefix support for multi-environment isolation
- Cache helper utilities (`CacheHelper`, `RedisCacheHelper`)
- Null value caching support
- Transaction-aware cache manager

#### nexora-spring-boot-starter-kafka
- Event publisher with transactional support
- Dead Letter Queue (DLQ) with configurable retry attempts
- Outbox pattern for reliable event publishing
- Automatic DLQ topic creation (`{topic}.dlq`)
- Non-retryable exception configuration

#### nexora-spring-boot-starter-resilience
- Circuit Breaker configuration
- Retry with exponential backoff support
- Time Limiter for timeout protection
- Rate Limiter (optional)
- Event listeners for state change logging
- Per-instance configuration support

#### nexora-spring-boot-starter-security
- JWT token provider (generate, validate, refresh)
- JWT claims parsing and validation
- Jasypt integration for property encryption
- `ENC()` wrapper for encrypted configuration values
- Password utility with BCrypt hashing

#### nexora-spring-boot-starter-file-storage
- Unified file storage interface
- Local storage implementation
- Aliyun OSS storage implementation
- AWS S3 storage implementation
- MinIO storage implementation
- File validation (size, extension)
- Automatic path handling (date-based, UUID filenames)
- Hash calculation (MD5, SHA-256, SHA-512)
- Presigned URL generation for cloud storage

#### nexora-spring-boot-starter-audit
- Entity auditing with `@CreatedBy`, `@LastModifiedBy`
- Custom `AuditorAware` implementation
- JPA integration for audit field population
- Request context propagation

#### nexora-spring-boot-starter-observability
- Micrometer integration
- Custom metrics support
- Meter registry configuration
- Metrics publishing configuration

### Build System
- Gradle 9.0 with Kotlin DSL
- Convention plugins for build configuration elimination
- Version catalog (`libs.versions.toml`) for dependency management
- Configuration cache enabled for performance
- GPG signing support for release artifacts
- Rich POM metadata generation

### CI/CD
- GitHub Actions workflows for CI
- Automated snapshot publishing on push to main
- Automated release publishing on tag creation
- Dependency graph submission for supply chain security
- Build scan integration with Develocity

### Documentation
- Comprehensive README with usage examples
- DEVELOPMENT.md for contributors
- CONTRIBUTING.md with contribution guidelines
- CLAUDE.md for AI-assisted development

## [0.1.0] - 2024-XX-XX

### Added
- Initial proof of concept
- Basic starter structure
- Auto-configuration framework

---

## Versioning Scheme

- **Major version** (X.0.0): Breaking changes, major refactorings
- **Minor version** (0.X.0): New features, backward compatible
- **Patch version** (0.0.X): Bug fixes, minor improvements

## Release Types

### Snapshot Release
- Published automatically on push to `main` branch
- Version format: `X.X.X-SNAPSHOT`
- For testing and development

### Release Release
- Published on git tag creation (format: `vX.X.X`)
- Requires version format validation (semver)
- GPG signed artifacts
- For production use

## Migration Guides

### Upgrading from 0.x to 1.0.0

See [MIGRATION-1.0.0.md](docs/MIGRATION-1.0.0.md) for detailed migration guide.

---

[Unreleased]: https://github.com/suj1e/nexora/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/suj1e/nexora/releases/tag/v1.0.0
[0.1.0]: https://github.com/suj1e/nexora/releases/tag/v0.1.0

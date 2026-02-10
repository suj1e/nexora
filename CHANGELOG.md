# Changelog

All notable changes to the Nexora project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

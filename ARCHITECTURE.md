# Architecture

This document describes the architecture, design principles, and technical decisions behind the Nexora project.

## Table of Contents

- [Design Philosophy](#design-philosophy)
- [Architecture Overview](#architecture-overview)
- [Module Architecture](#module-architecture)
- [Design Patterns](#design-patterns)
- [Technical Decisions](#technical-decisions)
- [Component Dependencies](#component-dependencies)
- [Extension Points](#extension-points)
- [Performance Considerations](#performance-considerations)
- [Version Compatibility](#version-compatibility)

## Design Philosophy

### Core Principles

1. **Convention over Configuration**
   - Sensible defaults for common use cases
   - Zero-configuration for most features
   - Override when needed

2. **Modularity**
   - Each starter solves one specific problem
   - Minimal cross-dependencies
   - Use only what you need

3. **Extensibility**
   - `@ConditionalOnMissingBean` for easy overrides
   - Interface-based design for custom implementations
   - Hooks for custom behavior

4. **Production Ready**
   - Comprehensive error handling
   - Observability built-in
   - Performance optimized

5. **Developer Experience**
   - Clear error messages
   - Comprehensive documentation
   - IDE-friendly with metadata

## Architecture Overview

### Project Structure

```
nexora/
├── buildSrc/                              # Convention plugins
│   └── src/main/kotlin/
│       ├── nexora.java-conventions.gradle.kts      # Java conventions
│       ├── nexora.publishing-conventions.gradle.kts # Publishing conventions
│       └── nexora.signing-conventions.gradle.kts   # Signing conventions
│
├── nexora-common/                         # Shared utilities
│   └── src/main/java/com/nexora/common/
│       ├── api/                            # Result<T>, BusinessException
│       ├── model/                         # LoginResponse, ProblemDetail
│       └── security/                       # PasswordUtil
│
├── nexora-spring-boot-starters/          # Starters directory
│   ├── nexora-spring-boot-starter-web/    # Web API essentials
│   ├── nexora-spring-boot-starter-webflux/ # Reactive Web support
│   ├── nexora-spring-boot-starter-data-jpa/ # JPA with auditing
│   ├── nexora-spring-boot-starter-redis/  # Multi-level caching
│   ├── nexora-spring-boot-starter-kafka/  # Event-driven architecture
│   ├── nexora-spring-boot-starter-resilience/ # Resilience patterns
│   ├── nexora-spring-boot-starter-security/ # JWT & encryption
│   ├── nexora-spring-boot-starter-file-storage/ # Multi-cloud storage
│   ├── nexora-spring-boot-starter-audit/  # Entity auditing
│   └── nexora-spring-boot-starter-observability/ # Metrics & tracing
│
├── gradle/
│   ├── libs.versions.toml                 # Version catalog
│   └── verification-metadata.xml          # Dependency checksums
│
├── build.gradle.kts                       # Root build configuration
├── settings.gradle.kts                    # Module declarations
└── gradle.properties                      # Gradle configuration
```

### Layer Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Application Layer                        │
│                  (Your Microservice)                        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Nexora Starters Layer                     │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────────┐ │
│  │   Web    │ │  Redis   │ │  Kafka   │ │    Security      │ │
│  │ Starter  │ │ Starter  │ │ Starter  │ │    Starter       │ │
│  └──────────┘ └──────────┘ └──────────┘ └──────────────────┘ │
│  ┌──────────┐ ┌───────────────────────────────────────────┐ │
│  │Resilience│ │         File Storage & Audit              │ │
│  │ Starter  │ │              Starters                     │ │
│  └──────────┘ └───────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      Spring Boot Layer                       │
│              (Auto-Configuration & Context)                 │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Infrastructure Layer                      │
│  (Redis, Kafka, Database, S3, OSS, MinIO, etc.)            │
└─────────────────────────────────────────────────────────────┘
```

## Module Architecture

### Auto-Configuration Pattern

Each starter follows Spring Boot's auto-configuration pattern:

```
┌─────────────────────────────────────────────────────────┐
│  META-INF/spring/org.springframework.boot.              │
│         autoconfigure.AutoConfiguration.imports         │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│         @AutoConfiguration Class                        │
│  @ConditionalOnClass                                    │
│  @EnableConfigurationProperties                         │
│  @ComponentScan                                         │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│         @ConfigurationProperties Class                  │
│  - Configuration binding                                │
│  - Validation with @Validated                           │
│  - IDE autocomplete support                            │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│         @Bean Definitions                               │
│  @ConditionalOnMissingBean (allows user override)       │
└─────────────────────────────────────────────────────────┘
```

### Starter Activation Conditions

| Starter | Activation Condition | Property |
|---------|---------------------|----------|
| Web | `@ConditionalOnWebApplication` | N/A |
| WebFlux | `@ConditionalOnWebApplication(type=REACTIVE)` | N/A |
| Data JPA | `@ConditionalOnClass(EntityManager.class)` | `nexora.data-jpa.enabled=true` |
| Redis | `@ConditionalOnClass(RedisConnectionFactory.class)` | `nexora.redis.enabled=true` |
| Kafka | `@ConditionalOnClass(KafkaTemplate.class)` | N/A |
| Resilience | `@ConditionalOnClass(CircuitBreaker.class)` | `nexora.resilience.*.enabled=true` |
| Security | `@ConditionalOnClass(JwtParser.class)` | `nexora.security.*.enabled=true` |
| File Storage | `@ConditionalOnClass(FileStorageService.class)` | N/A |
| Audit | `@ConditionalOnClass(AuditorAware.class)` | `nexora.audit.enabled=true` |
| Observability | `@ConditionalOnClass(MeterRegistry.class)` | `nexora.observability.enabled=true` |

## Design Patterns

### 1. Auto-Configuration Pattern

**Purpose**: Zero-configuration activation

```java
@AutoConfiguration
@ConditionalOnClass(RedisConnectionFactory.class)
@EnableConfigurationProperties(RedisProperties.class)
public class RedisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RedisCacheManager redisCacheManager(RedisProperties properties) {
        return new RedisCacheManager(properties);
    }
}
```

### 2. Strategy Pattern

**Purpose**: Pluggable implementations

Used in: File Storage, Cache implementations

```java
public interface FileStorageService {
    FileMetadata upload(MultipartFile file, String path);
    InputStream download(String key);
}

// Different strategies
public class LocalStorage implements FileStorageService { }
public class OssStorage implements FileStorageService { }
public class S3Storage implements FileStorageService { }
```

### 3. Template Method Pattern

**Purpose**: Common workflow with customizable steps

Used in: Exception handlers, Event publishers

```java
public abstract class AbstractExceptionHandler<T extends Exception> {
    public final Result<Void> handle(T ex) {
        logError(ex);
        ErrorCode code = determineCode(ex);
        String message = determineMessage(ex);
        return Result.fail(code, message);
    }

    protected abstract ErrorCode determineCode(T ex);
}
```

### 4. Builder Pattern

**Purpose**: Complex object construction

Used in: JWT tokens, Configuration objects

```java
public class Result<T> {
    private final boolean success;
    private final String code;
    private final String message;
    private final T data;

    public static <T> ResultBuilder<T> builder() {
        return new ResultBuilder<>();
    }
}
```

### 5. Chain of Responsibility

**Purpose**: Exception handling chain

Used in: Global exception handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception ex) {
        // Chain: BusinessException → MethodArgumentNotValidException → ...
        return getHandler(ex.getClass()).handle(ex);
    }
}
```

### 6. Observer Pattern

**Purpose**: Event-driven notifications

Used in: Resilience4j event listeners, Kafka events

```java
@Component
public class CircuitBreakerEventLogger {
    @EventListener
    public void onCircuitBreakerEvent(CircuitBreakerEvent event) {
        log.info("Circuit breaker event: {}", event);
    }
}
```

### 7. Outbox Pattern

**Purpose**: Reliable event publishing

Used in: Kafka starter

```java
@Entity
public class OutboxEvent {
    private String aggregateType;
    private String aggregateId;
    private String eventType;
    private String payload;
    private Instant createdAt;
    private boolean processed;
}
```

## Technical Decisions

### 1. Multi-Level Caching Architecture

**Decision**: Caffeine (L1) + Redis (L2)

**Rationale**:
- **Performance**: Local cache eliminates network latency
- **Consistency**: Redis provides distributed consistency
- **Cost-effective**: Reduces Redis read operations
- **Scalability**: Each node has local cache

**Trade-offs**:
- Eventual consistency between L1 caches
- Memory overhead per instance
- Complexity in cache invalidation

### 2. Reactive vs Imperative

**Decision**: Separate starters for Web (imperative) and WebFlux (reactive)

**Rationale**:
- Clear separation of concerns
- No performance penalty for non-reactive apps
- Easier migration path
- Different exception handling patterns

**Trade-offs**:
- Duplicate code for similar functionality
- More modules to maintain

### 3. Dependency Management

**Decision**: Gradle Version Catalog (`libs.versions.toml`)

**Rationale**:
- Single source of truth for versions
- Type-safe dependency access
- IDE autocomplete support
- Easy version upgrades

**Trade-offs**:
- Learning curve for new developers
- buildSrc cannot access version catalog

### 4. Configuration Encryption

**Decision**: Jasypt integration for property encryption

**Rationale**:
- Industry standard for Java encryption
- Transparent encryption/decryption
- No code changes needed
- Spring Boot compatible

**Trade-offs**:
- Runtime overhead for decryption
- Key management complexity

### 5. Event Publishing

**Decision**: Outbox pattern for reliable event publishing

**Rationale**:
- Transactional guarantees
- No message loss on failures
- Replay capability
- Audit trail

**Trade-offs**:
- Additional database table
- Polling overhead
- Eventual consistency

### 6. Resilience Patterns

**Decision**: Resilience4j integration

**Rationale**:
- Lightweight alternative to Hystrix
- Reactive support
- Modular design
- Active maintenance

**Trade-offs**:
- Learning curve for configuration
- Metrics integration complexity

### 7. Build System

**Decision**: Gradle 9.0 with Kotlin DSL

**Rationale**:
- Type-safe build scripts
- Better IDE support
- Convention plugins for DRY
- Performance advantages

**Trade-offs**:
- More verbose than Groovy
- Smaller community than Maven

## Component Dependencies

### Dependency Graph

```
                    ┌──────────────────┐
                    │   nexora-common  │
                    └──────────────────┘
                           │
         ┌─────────────────┼─────────────────┐
         │                 │                 │
         ▼                 ▼                 ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│  Web Starter │  │ WebFlux S.   │  │ Data-JPA S.  │
└──────────────┘  └──────────────┘  └──────────────┘
                                               │
                                               ▼
                                      ┌──────────────┐
                                      │ Audit Starter│
                                      └──────────────┘

┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ Redis Starter│  │ Kafka Starter│  │Resilience S. │
└──────────────┘  └──────────────┘  └──────────────┘

┌──────────────┐  ┌──────────────┐
│Security S.   │  │File-Storage S.│
└──────────────┘  └──────────────┘

┌──────────────┐
│ObservabilityS.│
└──────────────┘
```

### External Dependencies

| Starter | Key Dependencies |
|---------|-----------------|
| Common | Spring Boot Starter, Jackson, SLF4J, Lombok |
| Web | Spring Web, Spring Web MVC |
| WebFlux | Spring WebFlux, Reactor Core |
| Data JPA | Spring Data JPA, Hibernate |
| Redis | Spring Data Redis, Caffeine, Lettuce |
| Kafka | Spring Kafka, Spring Retry |
| Resilience | Resilience4j, Spring AOP |
| Security | Spring Security, JJWT, Jasypt |
| File Storage | AWS SDK, Aliyun SDK, MinIO |
| Audit | Spring Data JPA, Spring AOP |
| Observability | Micrometer, Spring Boot Actuator |

## Extension Points

### 1. Custom Exception Handlers

Override default exception handling:

```java
@RestControllerAdvice
public class CustomExceptionHandler extends GlobalExceptionHandler {
    @Override
    protected Result<Void> handleBusinessException(BusinessException ex) {
        // Custom handling
    }
}
```

### 2. Custom Cache Implementation

```java
@Component
@ConditionalOnProperty(name = "nexora.redis.custom.enabled", havingValue = "true")
public class CustomCacheManager implements CacheManager {
    // Custom implementation
}
```

### 3. Custom Event Publisher

```java
@Component
public class CustomEventPublisher extends EventPublisher {
    // Add retry logic, batching, etc.
}
```

### 4. Custom File Storage

```java
@Component
@ConditionalOnProperty(name = "nexora.file-storage.type", havingValue = "custom")
public class CustomFileStorage implements FileStorageService {
    // Custom storage implementation
}
```

### 5. Custom Circuit Breaker Events

```java
@Component
public class CustomCircuitBreakerEventListener {
    @EventListener
    public void onStateTransition(CircuitBreakerOnStateTransitionEvent event) {
        // Custom event handling
    }
}
```

## Performance Considerations

### 1. Caching Performance

**Multi-level cache hit rates**:
- L1 (Caffeine): ~95% hit rate, ~1ms latency
- L2 (Redis): ~5% hit rate, ~10ms latency
- Database: <1% hits, ~50ms latency

**Optimization**:
- Tune L1 cache size based on memory
- Use appropriate TTL values
- Monitor cache hit/miss ratios

### 2. Connection Pooling

**Default configurations**:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
  redis:
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
```

**Optimization**:
- Adjust pool size based on load
- Monitor connection wait times
- Use connection pooling for all external services

### 3. Reactive Performance

**When to use reactive**:
- High concurrency (>1000 req/s)
- I/O-bound operations
- Streaming data
- Microservice communication

**When NOT to use reactive**:
- Simple CRUD operations
- CPU-bound operations
- Blocking legacy dependencies

### 4. Serialization

**JSON serialization**:
- Jackson is default (Spring Boot)
- Use `@JsonView` for partial responses
- Consider Protocol Buffers for high-throughput

### 5. Thread Pool Configuration

**Default settings**:
```yaml
spring:
  task:
    execution:
      pool:
        core-size: 8
        max-size: 16
        queue-capacity: 100
```

## Version Compatibility

### Compatibility Matrix

| Nexora Version | Spring Boot | Spring Cloud | Spring Cloud Alibaba | Java |
|----------------|-------------|--------------|----------------------|------|
| 1.0.x | 3.5.x | 2025.0.x | 2025.0.0.0 | 21+ |
| 2.0.x (planned) | 3.6.x | 2025.1.x | TBD | 21+ |

### Upgrade Path

**1.0.0 → 1.0.1**: Patch release (bug fixes)
- No breaking changes
- Drop-in replacement

**1.0.x → 2.0.0**: Major release
- Breaking changes possible
- See migration guide

### Minimum Requirements

- **Java**: 21 (uses virtual threads, pattern matching, etc.)
- **Gradle**: 9.0+
- **Spring Boot**: 3.5.10+
- **Memory**: 512MB minimum (1GB recommended per instance)

## Future Considerations

### Planned Enhancements

1. **Native Image Support**
   - GraalVM native compilation
   - AOT processing
   - Reduced memory footprint

2. **Kubernetes Integration**
   - ConfigMap/Secret support
   - Service discovery
   - Health checks enhancement

3. **Distributed Tracing**
   - OpenTelemetry integration
   - Trace context propagation
   - Performance analysis

4. **Multi-tenancy**
   - Tenant isolation
   - Per-tenant configuration
   - Data partitioning

### Architectural Debates

1. **Micronaut Compatibility**
   - Should we support Micronaut?
   - Impact: Build complexity

2. **Quarkus Integration**
   - Should we support Quarkus?
   - Impact: Different injection model

3. **Module Granularity**
   - Are starters too coarse-grained?
   - Should we split Redis starter into Caffeine and Redis?

## References

- [Spring Boot Auto-Configuration](https://docs.spring.io/spring-boot/docs/3.5.10/reference/html/features/developing-auto-configuration.html)
- [Custom Starters](https://docs.spring.io/spring-boot/docs/3.5.10/reference/html/feature/deploying.html#features.deploying.custom-starter)
- [Resilience4j Documentation](https://resilience4j.readme.io/)
- [Gradle Best Practices](https://docs.gradle.org/current/userguide/userguide.html)

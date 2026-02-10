# Nexora

统一的微服务基础设施 Spring Boot Starters 库，提供开箱即用的自动配置。

Unified microservice infrastructure Spring Boot Starters library providing zero-configuration auto-configuration.

[![Build Status](https://github.com/suj1e/nexora/actions/workflows/ci.yml/badge.svg)](https://github.com/suj1e/nexora/actions/workflows/ci.yml)
[![Snapshot Version](https://img.shields.io/maven-central/v/com.nexora/nexora-spring-boot-starter-web?version=1.0.0-SNAPSHOT)](https://oss.sonatype.org/content/repositories/snapshots/com/nexora/)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21+-brightgreen.svg)](https://openjdk.org/projects/jdk/21)

## Table of Contents

- [Quick Start](#quick-start)
- [Modules](#modules)
- [Architecture](#architecture)
- [Usage](#usage)
- [Configuration](#configuration)
- [Module Details](#module-details)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## Quick Start

### 1. Add Dependencies

Add Nexora starters to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.nexora:nexora-spring-boot-starter-web:1.0.0")
    implementation("com.nexora:nexora-spring-boot-starter-redis:1.0.0")
    implementation("com.nexora:nexora-spring-boot-starter-kafka:1.0.0")
    // Add more starters as needed
}
```

Or `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>com.nexora</groupId>
        <artifactId>nexora-spring-boot-starter-web</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>com.nexora</groupId>
        <artifactId>nexora-spring-boot-starter-redis</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

### 2. Configure (Optional)

Most features work with zero configuration. Add `application.yml` for customization:

```yaml
nexora:
  redis:
    enabled: true
    cache-default-ttl: 30m
  kafka:
    dlq:
      enabled: true
```

### 3. Start Coding

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        return Result.success(userService.findById(id));
    }

    @PostMapping
    public Result<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        return Result.success(userService.create(request));
    }
}
```

That's it! The starter automatically provides:
- Unified response format `Result<T>`
- Global exception handling
- Request validation
- Logging

## Modules

| 模块 | 说明 | 核心组件 |
|------|------|----------|
| **nexora-spring-boot-starter-web** | Web 统一处理 | `GlobalExceptionHandler`, `Result<T>`, `BusinessException` |
| **nexora-spring-boot-starter-webflux** | 响应式 Web | `GlobalWebFluxExceptionHandler`, reactive types |
| **nexora-spring-boot-starter-data-jpa** | JPA 审计 | `BaseEntity`, `@CreatedDate`, `@LastModifiedDate` |
| **nexora-spring-boot-starter-redis** | 多级缓存 | `RedisCacheManager`, `CaffeineCacheManager`, `CacheHelper` |
| **nexora-spring-boot-starter-kafka** | 消息队列 | `EventPublisher`, `OutboxEvent`, DLQ 错误处理器 |
| **nexora-spring-boot-starter-resilience** | 熔断降级 | `CircuitBreakerRegistry`, `RetryRegistry`, `TimeLimiterRegistry` |
| **nexora-spring-boot-starter-security** | 安全工具 | `JwtTokenProvider`, `Encryptor` (Jasypt) |
| **nexora-spring-boot-starter-file-storage** | 文件存储 | `FileStorageService`, `LocalStorage`, `OssStorage`, `S3Storage` |
| **nexora-spring-boot-starter-audit** | 实体审计 | `@CreatedBy`, `@LastModifiedBy`, `AuditorAware` |
| **nexora-spring-boot-starter-observability** | 可观测性 | `MeterRegistry`, metrics, tracing |

## Architecture

### Component Overview

```
┌─────────────────────────────────────────────────────────────┐
│                   Your Application                          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Nexora Starters                          │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────────┐ │
│  │   Web    │ │  Redis   │ │  Kafka   │ │    Security      │ │
│  │   +WebFlux│ │  L1+L2   │ │  +DLQ    │ │    JWT+Encrypt   │ │
│  └──────────┘ └──────────┘ └──────────┘ └──────────────────┘ │
│  ┌──────────┐ ┌───────────────────────────────────────────┐ │
│  │Resilience│ │  Data-JPA │ File-Storage │  Audit │ Obs.  │ │
│  │CB+Retry  │ │  Audit   │ Multi-Cloud   │ Auditing│Met. │ │
│  └──────────┘ └───────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   Spring Boot 3.5.x                         │
│              (Auto-Configuration & Context)                 │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│              Infrastructure (Redis, Kafka, DB, etc.)        │
└─────────────────────────────────────────────────────────────┘
```

### Design Principles

1. **Zero Configuration** - Sensible defaults out of the box
2. **Modular** - Use only what you need
3. **Extensible** - Override any bean with `@ConditionalOnMissingBean`
4. **Production Ready** - Built-in resilience, observability, security

## Usage

### 添加依赖

### Maven Repository

Add the Maven repository (if using snapshots):

```xml
<repositories>
    <repository>
        <id>yunxiao-snapshots</id>
        <url>https://repo.rdc.aliyun.com/repository/xxxxx-snapshot</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

### Gradle

```gradle
dependencies {
    implementation("com.nexora:nexora-spring-boot-starter-web")
    implementation("com.nexora:nexora-spring-boot-starter-redis")
    implementation("com.nexora:nexora-spring-boot-starter-kafka")
    implementation("com.nexora:nexora-spring-boot-starter-resilience")
    implementation("com.nexora:nexora-spring-boot-starter-security")
    implementation("com.nexora:nexora-spring-boot-starter-file-storage")
}
```

### Configuration

大部分功能零配置开启，可通过配置调整行为：

```yaml
# Redis 多级缓存 (Caffeine L1 + Redis L2)
nexora:
  redis:
    enabled: true
    cache-default-ttl: 30m
    cache-ttl-mappings:
      user-cache: 1h
      product-cache: 10m
    use-cache-prefix: true
    key-prefix: "nexora:"
    cache-null-values: true
    enable-caffeine: true
    caffeine-spec: "maximumSize=1000,expireAfterWrite=5m"

# Kafka + DLQ + Outbox
spring:
  kafka:
    bootstrap-servers: ${KAFKA_SERVERS:localhost:9092}
nexora:
  kafka:
    dlq:
      enabled: true
      retry-attempts: 3
    outbox:
      enabled: false  # 开启需添加 JPA 依赖

# Resilience4j 熔断降级
nexora:
  resilience:
    circuit-breaker:
      enabled: true
      failure-rate-threshold: 50
      sliding-window-size: 10
      wait-duration-in-open-state: 10s
    retry:
      enabled: true
      max-attempts: 3
      enable-exponential-backoff: false
    time-limiter:
      enabled: false
      timeout-duration: 5s

# JWT + 配置加密
nexora:
  security:
    jwt:
      enabled: false
      secret: ${JWT_SECRET}
      expiration: 1h
      refresh-expiration: 7d
    jasypt:
      enabled: false
      password: ${JASYPT_PASSWORD}

# 文件存储
nexora:
  file-storage:
    type: local  # local, oss, s3, minio
    upload-path: /data/uploads
    base-url: https://cdn.example.com
    max-file-size: 10MB
    allowed-extensions: jpg,jpeg,png,pdf
    enable-date-path: true
    enable-uuid-filename: true
    # OSS 配置 (type=oss 时)
    oss:
      endpoint: oss-cn-hangzhou.aliyuncs.com
      access-key-id: ${OSS_ACCESS_KEY_ID}
      access-key-secret: ${OSS_ACCESS_KEY_SECRET}
      bucket: my-bucket
    # S3 配置 (type=s3 时)
    s3:
      region: us-east-1
      access-key-id: ${AWS_ACCESS_KEY_ID}
      secret-access-key: ${AWS_SECRET_ACCESS_KEY}
      bucket: my-bucket
    # MinIO 配置 (type=minio 时)
    minio:
      endpoint: http://localhost:9000
      access-key: ${MINIO_ACCESS_KEY}
      secret-key: ${MINIO_SECRET_KEY}
      bucket: my-bucket
```

## 模块详解

### Web Starter

- **自动包装 REST 响应**为统一格式 `Result<T>`
- **全局异常处理**，自动映射 HTTP 状态码
- **业务异常基类** `BusinessException`，便于业务错误定义

```java
// 自动包装响应
@GetMapping("/user/{id}")
public Result<User> getUser(@PathVariable Long id) {
    return Result.success(userService.findById(id));
}

// 抛出业务异常
throw new BusinessException("USER_NOT_FOUND", "用户不存在");
```

### Redis Starter

- **多级缓存**：Caffeine (本地 L1) + Redis (分布式 L2)
- **JSON 序列化**：支持任意对象缓存
- **TTL 配置**：支持全局默认和单个缓存配置
- **Key 前缀**：避免多环境 key 冲突

### Kafka Starter

- **事务性发布**：`EventPublisher` 支持事务
- **DLQ 支持**：失败消息自动发送到 `{topic}.dlq`
- **Outbox 模式**：可靠事件发布（需 JPA）

```java
// 发布事件
eventPublisher.publish("user-topic", new UserCreatedEvent(userId));
```

### Resilience Starter

- **熔断器**：防止级联故障
- **重试**：支持指数退避
- **时间限制器**：超时保护
- **事件监听器**：记录状态变化，便于监控

### Security Starter

- **JWT 工具**：生成、验证、刷新 token
- **配置加密**：使用 `ENC()` 加密敏感配置

```java
// JWT 操作
String token = jwtTokenProvider.generateToken(userId, claims);
Claims claims = jwtTokenProvider.getClaims(token);

// 配置加密
app:
  password: ENC(encryptedValueHere)
```

### File Storage Starter

- **统一存储接口**：支持本地、阿里云 OSS、AWS S3、MinIO
- **自动路由**：根据配置自动选择存储实现
- **文件验证**：支持文件大小、扩展名限制
- **路径处理**：支持日期分区、UUID 文件名
- **哈希计算**：支持 MD5、SHA-256、SHA-512

```java
// 上传文件
@Autowired
private FileStorageService fileStorageService;

@PostMapping("/upload")
public Result<FileMetadata> upload(@RequestParam("file") MultipartFile file) {
    FileMetadata metadata = fileStorageService.upload(file, "uploads/");
    return Result.ok(metadata);
}

// 下载文件
InputStream stream = fileStorageService.download("uploads/2024/01/15/abc.jpg");

// 获取公开 URL
String url = fileStorageService.getPublicUrl("uploads/2024/01/15/abc.jpg");

// 生成预签名 URL (云存储)
String presignedUrl = fileStorageService.getPresignedUrl("uploads/file.pdf", 3600);
```

## Development Standards

- 每个 Starter 只解决一类问题
- 依赖 Spring Boot 自动配置机制
- 提供合理的默认值
- 支持外部配置覆盖
- 完整的单元测试
- 详细的文档和示例

## Troubleshooting

### Common Issues

#### 1. Auto-configuration not activating

**Problem**: Starter beans are not being created.

**Solution**: Check that the required dependencies are present:

```kotlin
// Make sure you have the required dependencies
implementation("org.springframework.boot:spring-boot-starter-web")
implementation("com.nexora:nexora-spring-boot-starter-redis")
```

Enable debug logging to see why auto-configuration is skipped:

```yaml
logging:
  level:
    com.nexora: DEBUG
    org.springframework.boot.autoconfigure: DEBUG
```

#### 2. Cache not working

**Problem**: Cache is not caching or returning stale data.

**Solution**: Verify configuration:

```yaml
nexora:
  redis:
    enabled: true  # Must be true
    cache-default-ttl: 30m
    enable-caffeine: true  # For L1 cache
```

Check Redis connectivity:

```bash
redis-cli ping
```

Clear cache manually:

```bash
redis-cli KEYS "nexora:*" | xargs redis-cli DEL
```

#### 3. Kafka messages not being consumed

**Problem**: DLQ is filling up, messages not processed.

**Solution**: Check consumer configuration:

```yaml
spring:
  kafka:
    consumer:
      auto-offset-reset: earliest
      enable-auto-commit: false
```

Verify DLQ is enabled:

```yaml
nexora:
  kafka:
    dlq:
      enabled: true
      retry-attempts: 3
```

#### 4. Circuit breaker always open

**Problem**: All requests fail with "Circuit breaker is open".

**Solution**: Check configuration thresholds:

```yaml
nexora:
  resilience:
    circuit-breaker:
      failure-rate-threshold: 50  # Percentage (1-100)
      sliding-window-size: 10     # Number of calls
      wait-duration-in-open-state: 10s  # Time before retry
```

Wait for `wait-duration-in-open-state` before retrying.

#### 5. JWT token validation failing

**Problem**: `JwtValidationException` on token validation.

**Solution**: Check configuration:

```yaml
nexora:
  security:
    jwt:
      secret: ${JWT_SECRET}  # Must be same as generation
      expiration: 1h
```

Make sure:
- Secret matches between generation and validation
- Token is not expired
- Clock is synchronized across services

#### 6. File upload failing

**Problem**: Upload fails with size or extension error.

**Solution**: Configure limits:

```yaml
nexora:
  file-storage:
    max-file-size: 10MB
    allowed-extensions: jpg,jpeg,png,pdf,doc,docx

spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
```

#### 7. Build failures

**Problem**: `./gradlew build` fails with dependency errors.

**Solution**: Clean and rebuild:

```bash
./gradlew clean --no-configuration-cache
./gradlew build
```

### Debug Mode

Enable comprehensive debug logging:

```yaml
logging:
  level:
    com.nexora: DEBUG
    org.springframework: DEBUG
    io.lettuce: DEBUG  # Redis
    org.apache.kafka: DEBUG  # Kafka
```

### Health Checks

Check starter health with Actuator:

```bash
curl http://localhost:8080/actuator/health
```

Expected response:

```json
{
  "status": "UP",
  "components": {
    "redis": { "status": "UP" },
    "diskSpace": { "status": "UP" }
  }
}
```

### Getting Help

- **Documentation**: See [DEVELOPMENT.md](DEVELOPMENT.md) for technical details
- **Architecture**: See [ARCHITECTURE.md](ARCHITECTURE.md) for design details
- **Contributing**: See [CONTRIBUTING.md](CONTRIBUTING.md) for contribution guidelines
- **Issues**: [GitHub Issues](https://github.com/suj1e/nexora/issues)
- **Discussions**: [GitHub Discussions](https://github.com/suj1e/nexora/discussions)

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## License

Apache License 2.0

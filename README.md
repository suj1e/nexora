# Nexora Spring Boot Starters
统一的微服务基础设施组件库，提供开箱即用的自动配置。

## 模块列表

| 模块 | 说明 |
|------|------|
| nexora-redis-starter | Redis + Caffeine 多级缓存 |
| nexora-kafka-starter | Kafka + DLQ + Outbox |
| nexora-resilience-starter | Resilience4j 熔断降级限流 |
| nexora-security-starter | Jasypt 加密 + JWT 工具 |
| nexora-web-starter | Web 工具（异常处理、响应格式） |

## 使用方式

添加所需 Starter：

```gradle
dependencies {
    implementation("com.nexora:nexora-redis-starter")
    implementation("com.nexora:nexora-kafka-starter")
    implementation("com.nexora:nexora-resilience-starter")
    implementation("com.nexora:nexora-security-starter")
    implementation("com.nexora:nexora-web-starter")
}
```

### 配置（可选）

大部分功能零配置，部分功能可按需配置：

```yaml
# Kafka + DLQ + Outbox
spring:
  kafka:
    bootstrap-servers: ${KAFKA_SERVERS:localhost:9092}
common:
  kafka:
    dlq:
      enabled: true
      retry-attempts: 3
    outbox:
      enabled: true

# 缓存
spring:
  cache:
    type: caffeine
common:
  redis:
    enabled: true
    enable-caffeine: true

# Resilience4j
common:
  resilience:
    enabled: true
    circuit-breaker:
      failure-rate-threshold: 50
      sliding-window-size: 10

# JWT
common:
  security:
    jwt:
      enabled: true
      secret: ${JWT_SECRET}
      expiration: 1h
```

## 开发规范

- 每个 Starter 只解决一类问题
- 依赖 Spring Boot 自动配置机制
- 提供合理的默认值
- 支持外部配置覆盖
- 完整的单元测试
- 详细的文档和示例

## License
Apache License 2.0

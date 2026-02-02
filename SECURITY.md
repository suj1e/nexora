# Security Policy

## Supported Versions

Currently, only the latest version of Nexora receives security updates.

| Version | Supported          |
|---------|--------------------|
| 1.0.x   | :white_check_mark: |
| < 1.0   | :x:                |

## Reporting a Vulnerability

The Nexora team takes security vulnerabilities seriously. We appreciate your efforts to responsibly disclose your findings.

### How to Report

**Do NOT** publicly disclose the vulnerability before it has been fixed.

Instead, please send an email to: **security@nexora.io**

### What to Include

Please include the following information in your report:

1. **Description**: A clear description of the vulnerability
2. **Impact**: The potential impact of the vulnerability
3. **Steps to Reproduce**: Detailed steps to reproduce the issue
4. **Proof of Concept**: Code or screenshots demonstrating the vulnerability (if applicable)
5. **Affected Versions**: Which versions are affected
6. **Suggested Fix** (optional): Any suggestions for fixing the issue

### Response Timeline

- **Initial Response**: Within 48 hours
- **Detailed Response**: Within 7 days with:
  - Confirmation of the vulnerability
  - Severity assessment
  - Planned fix timeline
  - Coordination for disclosure

### Disclosure Process

1. **Confirmation**: We will confirm receipt of your report within 48 hours
2. **Investigation**: We will investigate and assess the severity
3. **Fix Development**: We will develop a fix
4. **Release**: We will release a new version with the fix
5. **Public Disclosure**: We will publicly disclose the vulnerability after the fix is released

### Coordinated Disclosure

We follow responsible disclosure practices:
- Vulnerabilities are disclosed publicly after a fix is available
- Credit is given to the reporter (unless you wish to remain anonymous)
- We will work with you on the timeline for public disclosure

## Security Best Practices

### Dependency Management

Nexora uses strict dependency verification to ensure supply chain security:

```properties
# gradle.properties
org.gradle.dependency.verification.mode=strict
```

All dependencies are verified with SHA-256 checksums in `gradle/verification-metadata.xml`.

### Configuration Security

#### Sensitive Configuration

Always use environment variables for sensitive configuration:

```yaml
# Good: Use environment variables
nexora:
  security:
    jwt:
      secret: ${JWT_SECRET}
    jasypt:
      password: ${JASYPT_PASSWORD}
```

#### Property Encryption

For encrypted properties, use Jasypt:

```yaml
# Encrypt sensitive values
app:
  password: ENC(encryptedValueHere)
```

Encrypt values using:

```bash
java -cp jasypt-*.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI \
  input="your-password" \
  password=${JASYPT_PASSWORD} \
  algorithm=PBEWITHHMACSHA512ANDAES_256
```

### JWT Security

When using the JWT token provider:

1. **Use strong secrets**: Minimum 256 bits (32 characters) for HMAC
2. **Rotate secrets**: Regularly rotate JWT secrets
3. **Set appropriate expiration**: Balance security and user experience
4. **Use HTTPS**: Always transmit JWT over HTTPS
5. **Validate tokens**: Always validate signature and expiration

```yaml
nexora:
  security:
    jwt:
      enabled: true
      secret: ${JWT_SECRET}  # Use strong, unique secret
      expiration: 1h         # Short-lived access tokens
      refresh-expiration: 7d # Longer-lived refresh tokens
```

### Database Security

#### Connection Security

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb?ssl=true
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

#### Query Security

- Use parameterized queries (JPA/Hibernate)
- Never concatenate user input into SQL
- Apply principle of least privilege for database users

### Redis Security

```yaml
spring:
  redis:
    host: ${REDIS_HOST}
    port: 6379
    password: ${REDIS_PASSWORD}  # Always use password in production
    ssl: true                    # Enable SSL in production
```

### Kafka Security

```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_SERVERS}
    properties:
      security.protocol: SASL_SSL
      sasl.mechanism: PLAIN
      sasl.jaas.config: ${KAFKA_SASL_CONFIG}
```

### File Storage Security

#### Upload Validation

Always configure file upload restrictions:

```yaml
nexora:
  file-storage:
    max-file-size: 10MB
    allowed-extensions: jpg,jpeg,png,pdf,doc,docx
    enable-virus-scan: true  # If available
```

#### Cloud Storage Credentials

- Use IAM roles where possible
- Rotate access keys regularly
- Apply principle of least privilege
- Enable bucket policies for access control

```yaml
nexora:
  file-storage:
    type: s3
    s3:
      region: us-east-1
      access-key-id: ${AWS_ACCESS_KEY_ID}     # Or use IAM role
      secret-access-key: ${AWS_SECRET_ACCESS_KEY}
```

### API Security

#### Rate Limiting

Enable rate limiting to prevent abuse:

```yaml
nexora:
  resilience:
    rate-limiter:
      enabled: true
      limit-for-period: 100
      limit-refresh-period: 1m
```

#### Circuit Breaker

Prevent cascading failures:

```yaml
nexora:
  resilience:
    circuit-breaker:
      enabled: true
      failure-rate-threshold: 50
      sliding-window-size: 10
      wait-duration-in-open-state: 10s
```

### Logging Security

#### Sensitive Data

Never log sensitive information:

```java
// Bad: Logs password
log.info("User login: {}", user.getPassword());

// Good: Logs only non-sensitive data
log.info("User login: {}", user.getUsername());
```

#### Log Sanitization

Configure log sanitization for sensitive fields:

```yaml
logging:
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
  logstash:
    enabled: true
```

## Security Headers

Enable security headers in your application:

```java
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers()
            .contentSecurityPolicy("default-src 'self'")
            .and()
            .xssProtection()
            .and()
            .httpStrictTransportSecurity();
        return http.build();
    }
}
```

## Vulnerability Scanning

### Dependency Scanning

The project uses GitHub Dependabot and GitHub Actions for security scanning:

- **Dependabot**: Automated dependency updates
- **Dependabot Alerts**: Security vulnerability notifications
- **GitHub Actions**: Security scanning on every PR

### Manual Scanning

Run security scans manually:

```bash
# OWASP Dependency Check
./gradlew dependencyCheckAnalyze

# SBOM generation
./gradlew cyclonedxBom
```

## Security Audits

### Third-Party Audits

Third-party security audits are welcomed. If you're interested in performing an audit:

1. Contact us at security@nexora.io
2. We will provide support and documentation
3. Coordinate disclosure timeline with us

## Security Updates

### Monitoring Security

Stay informed about security updates:

- **GitHub Security Advisories**: Watch for security advisories
- **Release Notes**: Check CHANGELOG.md for security fixes
- **Dependabot Alerts**: Enable for your repositories

### Updating for Security

When security updates are released:

1. Review the security advisory
2. Update to the fixed version
3. Test thoroughly in staging
4. Deploy to production

## Security Contact

For security-related questions:
- **Email**: security@nexora.io
- **PGP Key**: Available at https://nexora.io/security/pgp

## Acknowledgments

We thank all security researchers who have responsibly disclosed vulnerabilities to help improve the security of Nexora.

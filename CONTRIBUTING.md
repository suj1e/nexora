# Contributing to Nexora

Thank you for your interest in contributing to Nexora! This document provides guidelines and instructions for contributing.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)
- [Testing Guidelines](#testing-guidelines)
- [Documentation](#documentation)

## Code of Conduct

- Be respectful and inclusive
- Provide constructive feedback
- Focus on what is best for the community
- Show empathy towards other community members

## Getting Started

### Prerequisites

- **JDK 21** - [Eclipse Temurin](https://adoptium.net/) or [Amazon Corretto](https://corretto.aws/) recommended
- **Gradle 9.0+** - Project uses Gradle wrapper (no installation needed)
- **Git** - For version control
- **IDE** - IntelliJ IDEA (recommended) or VS Code

### Setup Development Environment

```bash
# 1. Fork the repository on GitHub
# 2. Clone your fork
git clone https://github.com/YOUR_USERNAME/nexora.git
cd nexora

# 3. Add upstream remote
git remote add upstream https://github.com/suj1e/nexora.git

# 4. Install dependencies and build
./gradlew build
```

### IDE Configuration

**IntelliJ IDEA:**
1. Import project as Gradle project
2. Enable "Annotation Processing" for Lombok
3. Set JDK to 21
4. Enable "Delegate IDE build/run actions to Gradle"

**VS Code:**
1. Install "Extension Pack for Java"
2. Install "Gradle for Java"
3. Set `java.jdt.ls.java.home` to JDK 21 path

## Development Workflow

### 1. Create a Branch

```bash
git checkout main
git pull upstream main
git checkout -b feature/your-feature-name
# or
git checkout -b fix/your-bug-fix
```

### Branch Naming Convention

- `feature/` - New features
- `fix/` - Bug fixes
- `docs/` - Documentation changes
- `refactor/` - Code refactoring
- `test/` - Test improvements
- `chore/` - Maintenance tasks

### 2. Make Changes

- Write code following [Coding Standards](#coding-standards)
- Add/update tests for your changes
- Update documentation as needed

### 3. Run Tests and Build

```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :nexora-spring-boot-starter-web:test

# Run full check (tests + static analysis)
./gradlew check

# Build all modules
./gradlew build
```

### 4. Commit Changes

Follow [Commit Guidelines](#commit-guidelines) and use [Conventional Commits](https://www.conventionalcommits.org/):

```bash
git add .
git commit -m "feat: add support for custom cache key generators"
```

### 5. Push and Create Pull Request

```bash
git push origin feature/your-feature-name
```

Then create a pull request on GitHub following the [Pull Request Process](#pull-request-process).

## Coding Standards

### Java Code Style

Follow these conventions for consistency:

```java
// 1. Class naming: PascalCase
public class RedisCacheManager { }

// 2. Method naming: camelCase
public void put(String key, Object value) { }

// 3. Constants: UPPER_SNAKE_CASE
public static final String DEFAULT_CACHE_PREFIX = "nexora:";

// 4. Use final for variables that won't change
private final CacheManager cacheManager;

// 5. Use @Autowired on constructor (preferred)
@Autowired
public RedisCacheService(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
}

// 6. Order of modifiers: public protected private abstract static final transient volatile synchronized native strictfp
public static final void methodName() { }

// 7. Use Optional for nullable return values
public Optional<User> findById(Long id) { }
```

### Lombok Guidelines

- Use `@Data` for simple POJOs
- Use `@Builder` for complex object construction
- Use `@Slf4j` for logging (avoid manual logger declaration)
- Prefer `@RequiredArgsConstructor` over `@AllArgsConstructor` for dependency injection

### Auto-Configuration Guidelines

Each starter MUST follow these patterns:

```java
@AutoConfiguration
@ConditionalOnClass(MyService.class)  // Only activate if class is present
@EnableConfigurationProperties(MyStarterProperties.class)
@ComponentScan(basePackageClasses = MyService.class)
public class MyStarterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean  // Allow user override
    public MyService myService(MyStarterProperties properties) {
        return new MyService(properties);
    }
}
```

### Configuration Properties

```java
@Validated
@ConfigurationProperties(prefix = "nexora.mystarter")
public class MyStarterProperties {

    @NotBlank
    private String enabled = "true";

    @Min(1)
    private int maxConnections = 10;

    // Getters and setters
}
```

### Exception Handling

- Use `BusinessException` for business logic errors
- Include error codes for i18n support
- Add trace IDs for debugging

```java
throw new BusinessException("USER_NOT_FOUND", "User not found", userId);
```

### Testing

- Write unit tests for all public methods
- Use JUnit 5 with `@ExtendWith(MockitoExtension.class)`
- Mock external dependencies
- Test both success and failure scenarios

```java
@ExtendWith(MockitoExtension.class)
class RedisCacheServiceTest {

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private RedisCacheService cacheService;

    @Test
    void shouldReturnCachedValue() {
        // given
        String key = "test-key";
        String value = "test-value";
        when(cacheManager.get(key)).thenReturn(value);

        // when
        Optional<String> result = cacheService.get(key);

        // then
        assertThat(result).isPresent().contains(value);
    }
}
```

## Commit Guidelines

Follow [Conventional Commits](https://www.conventionalcommits.org/):

### Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

| Type | Description |
|------|-------------|
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation changes |
| `style` | Code style changes (formatting, etc.) |
| `refactor` | Code refactoring |
| `test` | Test additions or modifications |
| `chore` | Maintenance tasks |
| `perf` | Performance improvements |
| `ci` | CI/CD changes |

### Examples

```
feat(web): add support for custom exception handlers

Add a new interface that allows users to provide custom exception
handlers for specific exception types.

Closes #123
```

```
fix(redis): resolve cache key collision bug

The cache key generator was not including the cache name, causing
collisions when multiple caches were used. This fix prefixes all
keys with the cache name.

Fixes #456
```

```
docs: update README with quick start guide

Add a comprehensive quick start section to help new users get
started with the project.
```

## Pull Request Process

### Before Submitting

1. **Check existing PRs** - Ensure you're not duplicating work
2. **Update documentation** - README, DEVELOPMENT.md, JavaDoc
3. **Add tests** - All new code must have tests
4. **Run full build** - `./gradlew check` must pass
5. **Format code** - Ensure consistent formatting

### PR Title Format

Use the same format as commits:

```
feat(web): add support for custom exception handlers
fix(redis): resolve cache key collision bug
docs: update README with quick start guide
```

### PR Description Template

```markdown
## Summary
Brief description of changes (2-3 sentences)

## Changes
- List key changes here
- Use bullet points

## Testing
- Describe testing performed
- Include test results

## Checklist
- [ ] Tests pass locally
- [ ] Documentation updated
- [ ] No breaking changes (or documented)
- [ ] Commit messages follow conventions
```

### Review Process

1. **Automated checks** - CI must pass
2. **Code review** - Maintainer review (1-2 days)
3. **Address feedback** - Respond to comments
4. **Approval** - At least one maintainer approval required
5. **Merge** - Squash and merge to main branch

## Testing Guidelines

### Test Coverage

- Aim for **80%+ code coverage**
- All public methods must have tests
- Test edge cases and error conditions

### Test Organization

```
src/test/java/com/nexora/
├── autoconfigure/     # Auto-configuration tests
├── handler/           # Handler tests
├── properties/        # Properties tests
└── service/           # Service tests
```

### Test Categories

1. **Unit Tests** - Test individual components in isolation
2. **Integration Tests** - Test component interactions
3. **Auto-Configuration Tests** - Verify Spring Boot auto-configuration

### Example Auto-Configuration Test

```java
@ExtendWith(MockitoExtension.class)
class MyStarterAutoConfigurationTest {

    @Test
    void shouldConfigureMyService() {
        // Given
        ApplicationContext context = ...;

        // When
        MyService service = context.getBean(MyService.class);

        // Then
        assertThat(service).isNotNull();
    }
}
```

## Documentation

### What to Document

1. **Public API** - All public classes and methods need JavaDoc
2. **Configuration** - Document all configuration properties
3. **Examples** - Provide usage examples in README and JavaDoc
4. **Changes** - Update CHANGELOG.md for user-visible changes

### JavaDoc Guidelines

```java
/**
 * Redis cache service with multi-level caching support.
 *
 * <p>This service provides a two-level caching strategy:
 * <ul>
 *   <li>L1: Caffeine (local cache)</li>
 *   <li>L2: Redis (distributed cache)</li>
 * </ul>
 *
 * @author Nexora Team
 * @since 1.0.0
 * @see CacheManager
 */
public class RedisCacheService {

    /**
     * Retrieves a value from cache.
     *
     * @param key the cache key (must not be null)
     * @return the cached value, or {@link Optional#empty()} if not found
     * @throws IllegalArgumentException if key is null
     */
    public Optional<Object> get(String key) {
        // ...
    }
}
```

### README Updates

When adding new features:
- Update module list table
- Add usage examples
- Update configuration examples
- Add troubleshooting tips (if applicable)

## Getting Help

- **Issues**: Create an issue on [GitHub](https://github.com/suj1e/nexora/issues)
- **Discussions**: Start a discussion on [GitHub Discussions](https://github.com/suj1e/nexora/discussions)
- **Documentation**: See [DEVELOPMENT.md](DEVELOPMENT.md) for technical details

## License

By contributing, you agree that your contributions will be licensed under the [Apache License 2.0](LICENSE).

package com.nexora.resilience.handler;

import com.nexora.common.api.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

/**
 * Fallback handler for circuit breaker and rate limiter scenarios.
 *
 * <p>Provides consistent fallback responses when services are unavailable
 * due to circuit breaker trips or rate limit thresholds.
 *
 * <p>Usage:
 * <pre>
 * &#64;Autowired
 * private FallbackHandler fallbackHandler;
 *
 * // In your route configuration
 * .fallbackHandler("mixFallback")
 *
 * // Reactive route example
 * RouteLocatorBuilder.Builder.routes()
 *     .route("mix-service", r -> r.path("/api/mix/**")
 *         .filters(f -> f.circuitBreaker("mixCircuitBreaker"))
 *         .uri("lb://mixsrv"))
 *         .fallbackHandler(fallbackHandler.mixFallback()))
 * </pre>
 *
 * @author sujie
 * @since 1.0.0
 */
@Slf4j
public class FallbackHandler {

    /**
     * Generic fallback handler for service unavailable scenarios.
     *
     * @param serviceName the name of the service that failed
     * @return ServerResponse with fallback data
     */
    public Mono<ServerResponse> serviceFallback(String serviceName) {
        log.warn("Service {} is unavailable, returning fallback response", serviceName);

        Result<Map<String, Object>> result = Result.fail(
                Result.CODE_SERVICE_UNAVAILABLE,
                serviceName + " 服务暂时不可用，请稍后重试",
                Map.of("timestamp", Instant.now().toString(), "fallback", true)
        );

        return ServerResponse
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(result);
    }

    /**
     * Generic fallback handler for ServerWebExchange.
     *
     * @param exchange the server exchange
     * @param serviceName the name of the service that failed
     * @return Mono<Void>
     */
    public Mono<Void> serviceFallback(ServerWebExchange exchange, String serviceName) {
        log.warn("Service {} is unavailable, returning fallback response", serviceName);

        Result<Map<String, Object>> result = Result.fail(
                Result.CODE_SERVICE_UNAVAILABLE,
                serviceName + " 服务暂时不可用，请稍后重试",
                Map.of("timestamp", Instant.now().toString(), "fallback", true)
        );

        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = toJson(result);
        byte[] bytes = body.getBytes();

        return exchange.getResponse().writeWith(
                reactor.core.publisher.Mono.just(exchange.getResponse()
                        .bufferFactory().wrap(bytes))
        );
    }

    /**
     * Fallback handler for ServerRequest functional routing.
     *
     * @param request the server request
     * @param serviceName the name of the service that failed
     * @return ServerResponse with fallback data
     */
    public Mono<ServerResponse> serviceFallback(ServerRequest request, String serviceName) {
        log.warn("Service {} is unavailable, returning fallback response", serviceName);

        Result<Map<String, Object>> result = Result.fail(
                Result.CODE_SERVICE_UNAVAILABLE,
                serviceName + " 服务暂时不可用，请稍后重试",
                Map.of("timestamp", Instant.now().toString(), "fallback", true)
        );

        return ServerResponse
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(result);
    }

    /**
     * Fallback handler with custom message.
     *
     * @param serviceName the name of the service that failed
     * @param message custom fallback message
     * @return ServerResponse with fallback data
     */
    public Mono<ServerResponse> serviceFallback(String serviceName, String message) {
        log.warn("Service {} is unavailable, returning fallback response", serviceName);

        Result<Map<String, Object>> result = Result.fail(
                Result.CODE_SERVICE_UNAVAILABLE,
                message,
                Map.of("timestamp", Instant.now().toString(), "fallback", true)
        );

        return ServerResponse
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(result);
    }

    /**
     * Fallback handler with custom data.
     *
     * @param serviceName the name of the service that failed
     * @param message custom fallback message
     * @param fallbackData custom fallback data
     * @return ServerResponse with fallback data
     */
    public <T> Mono<ServerResponse> serviceFallback(String serviceName, String message, T fallbackData) {
        log.warn("Service {} is unavailable, returning fallback response", serviceName);

        Result<T> result = Result.fail(Result.CODE_SERVICE_UNAVAILABLE, message, fallbackData);

        return ServerResponse
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(result);
    }

    /**
     * Fallback handler that returns cached/default data.
     *
     * @param serviceName the name of the service that failed
     * @param supplier the fallback data supplier
     * @return ServerResponse with fallback data
     */
    public <T> Mono<ServerResponse> serviceFallbackWithSupplier(String serviceName,
            java.util.function.Supplier<Mono<T>> supplier) {
        log.warn("Service {} is unavailable, attempting fallback response", serviceName);

        return supplier.get()
                .map(data -> (Result<Object>) Result.ok(data, serviceName + " 服务暂时不可用，返回降级数据"))
                .onErrorResume(e -> {
                    log.error("Fallback also failed for service {}", serviceName, e);
                    return Mono.just(Result.<Object>fail(Result.CODE_INTERNAL_ERROR, "降级服务也失败"));
                })
                .flatMap(result -> ServerResponse
                        .status(HttpStatus.SERVICE_UNAVAILABLE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(result));
    }

    /**
     * Generic rate limit exceeded fallback.
     *
     * @param exchange the server exchange
     * @return Mono<Void>
     */
    public Mono<Void> rateLimitFallback(ServerWebExchange exchange) {
        log.warn("Rate limit exceeded, returning rate limit response");

        Result<?> result = Result.fail(
                Result.CODE_TOO_MANY_REQUESTS,
                "请求过于频繁，请稍后重试"
        );

        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = toJson(result);
        byte[] bytes = body.getBytes();

        return exchange.getResponse().writeWith(
                reactor.core.publisher.Mono.just(exchange.getResponse()
                        .bufferFactory().wrap(bytes))
        );
    }

    /**
     * Rate limit exceeded fallback for ServerRequest.
     *
     * @param request the server request
     * @return ServerResponse with rate limit message
     */
    public Mono<ServerResponse> rateLimitFallback(ServerRequest request) {
        log.warn("Rate limit exceeded for path: {}", request.path());

        Result<?> result = Result.fail(
                Result.CODE_TOO_MANY_REQUESTS,
                "请求过于频繁，请稍后重试"
        );

        return ServerResponse
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(result);
    }

    private String toJson(Result<?> result) {
        return String.format("""
                {"success":%s,"code":%d,"message":"%s","data":%s,"timestamp":%s}""",
                result.success(),
                result.code(),
                escapeJson(result.message()),
                result.data() != null ? escapeJson(result.data().toString()) : "null",
                System.currentTimeMillis());
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

package com.nexora.webflux.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * WebFlux request logging filter.
 *
 * <p>Logs incoming requests with timing information.
 *
 * @author sujie
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(prefix = "nexora.webflux.logging", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RequestLoggingFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Instant start = Instant.now();
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String method = request.getMethod().name();
        String path = request.getPath().value();
        String query = request.getURI().getQuery();
        String fullPath = query != null ? path + "?" + query : path;
        String remoteAddr = request.getRemoteAddress() != null
                ? request.getRemoteAddress().getAddress().getHostAddress()
                : "unknown";

        return chain.filter(exchange)
                .doOnSuccess(v -> logRequest(start, method, fullPath, remoteAddr,
                        response.getStatusCode() != null ? response.getStatusCode().value() : 0));
    }

    private void logRequest(Instant start, String method, String path, String remoteAddr, int status) {
        Duration duration = Duration.between(start, Instant.now());
        log.info("{} {} from {} - Status: {} - Duration: {}ms",
                method, path, remoteAddr, status, duration.toMillis());
    }
}

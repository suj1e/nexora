package com.nexora.audit.domain;

import com.nexora.datajp.support.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractAuditable;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Audit log entity for tracking system events.
 *
 * <p>Provides comprehensive audit trail for:
 * <ul>
 *   <li>Authentication events (login, logout, token refresh)</li>
 *   <li>Data modifications (create, update, delete)</li>
     *  >Sensitive operations (permission changes, configuration updates)</li>
 * </ul>
 *
 * <p>Usage example:
 * <pre>
 * &#64;Autowired
 * private AuditLogService auditLogService;
 *
 * // Log successful login
 * auditLogService.log("LOGIN", userId, true, request);
 *
 * // Log failed login with error
 * auditLogService.logFailure("LOGIN", userId, "Invalid credentials", request);
 * </pre>
 *
 * @author sujie
 * @since 1.0.0
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_logs_user", columnList = "user_id"),
    @Index(name = "idx_audit_logs_action", columnList = "action"),
    @Index(name = "idx_audit_logs_created", columnList = "created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLog extends BaseEntity {

    /**
     * User ID who performed the action (can be null for system operations).
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * Action type (e.g., LOGIN, LOGOUT, USER_CREATE, USER_UPDATE, USER_DELETE).
     */
    @Column(nullable = false, length = 64)
    private String action;

    /**
     * IP address of the request.
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * User agent string from the request.
     */
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    /**
     * Request URI or endpoint.
     */
    @Column(name = "request_uri", length = 500)
    private String requestUri;

    /**
     * HTTP method (GET, POST, PUT, DELETE, etc.).
     */
    @Column(name = "http_method", length = 10)
    private String httpMethod;

    /**
     * Whether the operation succeeded.
     */
    @Column(nullable = false)
    private Boolean success;

    /**
     * Error message if the operation failed.
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * Additional context data as JSON (e.g., request parameters, response data).
     */
    @Column(name = "context_data", columnDefinition = "JSONB")
    private String contextData;

    /**
     * Module or service that generated this log.
     */
    @Column(name = "module_name", length = 100)
    private String moduleName;

    /**
     * Environment (dev, test, staging, prod).
     */
    @Column(name = "environment", length = 20)
    private String environment;

    /**
     * Client application identifier.
     */
    @Column(name = "client_id", length = 100)
    private String clientId;

    /**
     * Session ID if applicable.
     */
    @Column(name = "session_id", length = 100)
    private String sessionId;

    /**
     * Correlation ID for tracing across services.
     */
    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    /**
     * Create a successful audit log entry.
     *
     * @param action the action performed
     * @param userId the user who performed the action
     * @return the audit log
     */
    public static AuditLog success(String action, Long userId) {
        validateAction(action);

        AuditLog auditLog = new AuditLog();
        auditLog.action = action;
        auditLog.userId = userId;
        auditLog.success = true;
        return auditLog;
    }

    /**
     * Create a failed audit log entry.
     *
     * @param action the action that failed
     * @param userId the user who attempted the action
     * @param errorMessage error description
     * @return the audit log
     */
    public static AuditLog failure(String action, Long userId, String errorMessage) {
        validateAction(action);

        AuditLog auditLog = new AuditLog();
        auditLog.action = action;
        auditLog.userId = userId;
        auditLog.success = false;
        auditLog.errorMessage = errorMessage;
        return auditLog;
    }

    /**
     * Set IP address.
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Set user agent.
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Set request URI.
     */
    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    /**
     * Set HTTP method.
     */
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    /**
     * Set error message.
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Set context data.
     */
    public void setContextData(String contextData) {
        this.contextData = contextData;
    }

    /**
     * Set module name.
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * Set environment.
     */
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    /**
     * Set client ID.
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Set session ID.
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Set correlation ID.
     */
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * Check if this audit log represents a successful operation.
     */
    public boolean isSuccess() {
        return Boolean.TRUE.equals(success);
    }

    /**
     * Check if this audit log represents a failed operation.
     */
    public boolean isFailure() {
        return Boolean.FALSE.equals(success);
    }

    /**
     * Mark the log as failed.
     */
    public void markFailed(String errorMessage) {
        this.success = false;
        this.errorMessage = errorMessage;
    }

    /**
     * Mark the log as successful.
     */
    public void markSuccess() {
        this.success = true;
        this.errorMessage = null;
    }

    private static void validateAction(String action) {
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("Action cannot be blank");
        }
        if (action.length() > 64) {
            throw new IllegalArgumentException("Action must not exceed 64 characters");
        }
    }

    /**
     * Predefined action constants for common operations.
     */
    public static final class Actions {
        // Authentication actions
        public static final String LOGIN = "LOGIN";
        public static final String LOGIN_FAILURE = "LOGIN_FAILURE";
        public static final String LOGOUT = "LOGOUT";
        public static final String TOKEN_REFRESH = "TOKEN_REFRESH";
        public static final String TOKEN_REFRESH_FAILURE = "TOKEN_REFRESH_FAILURE";
        public static final String PASSWORD_CHANGE = "PASSWORD_CHANGE";
        public static final String PASSWORD_RESET = "PASSWORD_RESET";
        public static final String PASSWORD_RESET_REQUEST = "PASSWORD_RESET_REQUEST";

        // OAuth2 actions
        public static final String OAUTH2_LOGIN = "OAUTH2_LOGIN";
        public static final String OAUTH2_LOGIN_FAILURE = "OAUTH2_LOGIN_FAILURE";
        public static final String OAUTH2_TOKEN_REFRESH = "OAUTH2_TOKEN_REFRESH";

        // User management actions
        public static final String USER_CREATE = "USER_CREATE";
        public static final String USER_UPDATE = "USER_UPDATE";
        public static final String USER_DELETE = "USER_DELETE";
        public static final String USER_LOCK = "USER_LOCK";
        public static final String USER_UNLOCK = "USER_UNLOCK";
        public static final String USER_ENABLE = "USER_ENABLE";
        public static final String USER_DISABLE = "USER_DISABLE";

        // Role management actions
        public static final String ROLE_CREATE = "ROLE_CREATE";
        public static final String ROLE_UPDATE = "ROLE_UPDATE";
        public static final String ROLE_DELETE = "ROLE_DELETE";
        public static final String ROLE_ASSIGN = "ROLE_ASSIGN";
        public static final String ROLE_UNASSIGN = "ROLE_UNASSIGN";

        // Permission management actions
        public static final String PERMISSION_CREATE = "PERMISSION_CREATE";
        public static final String PERMISSION_UPDATE = "PERMISSION_UPDATE";
        public static final String PERMISSION_DELETE = "PERMISSION_DELETE";

        // Configuration actions
        public static final String CONFIG_UPDATE = "CONFIG_UPDATE";
        public static final String CONFIG_DELETE = "CONFIG_DELETE";

        // System actions
        public static final String SYSTEM_STARTUP = "SYSTEM_STARTUP";
        public static final String SYSTEM_SHUTDOWN = "SYSTEM_SHUTDOWN";
        public static final String SYSTEM_ERROR = "SYSTEM_ERROR";
        public static final String DATA_EXPORT = "DATA_EXPORT";
        public static final String DATA_IMPORT = "DATA_IMPORT";
        public static final String DATA_BACKUP = "DATA_BACKUP";
        public static final String DATA_RESTORE = "DATA_RESTORE";

        private Actions() {
        }
    }
}

package com.nexora.common.dto;

/**
 * Standard message response wrapper for API responses.
 *
 * <p>Provides a simple, consistent structure for API responses that contain
 * only a message without additional data.
 *
 * @param message the response message
 * @author sujie
 */
public record MessageResponse(String message) {

    /**
     * Creates a success message response.
     *
     * @param message the success message
     * @return a new MessageResponse
     */
    public static MessageResponse success(String message) {
        return new MessageResponse(message);
    }

    /**
     * Creates an info message response.
     *
     * @param message the info message
     * @return a new MessageResponse
     */
    public static MessageResponse info(String message) {
        return new MessageResponse(message);
    }

    /**
     * Creates a warning message response.
     *
     * @param message the warning message
     * @return a new MessageResponse
     */
    public static MessageResponse warning(String message) {
        return new MessageResponse(message);
    }

    /**
     * Creates an error message response.
     *
     * @param message the error message
     * @return a new MessageResponse
     */
    public static MessageResponse error(String message) {
        return new MessageResponse(message);
    }
}

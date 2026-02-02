package com.nexora.common.api;

/**
 * Pagination request parameters.
 *
 * <p>Used for list endpoints to specify page number and size.
 *
 * @author sujie
 * @since 1.0.0
 */
public record PageRequest(
    int page,
    int size
) {

    /**
     * Default page number.
     */
    public static final int DEFAULT_PAGE = 1;

    /**
     * Default page size.
     */
    public static final int DEFAULT_SIZE = 10;

    /**
     * Maximum page size.
     */
    public static final int MAX_SIZE = 100;

    /**
     * Create page request with defaults.
     */
    public static PageRequest of() {
        return new PageRequest(DEFAULT_PAGE, DEFAULT_SIZE);
    }

    /**
     * Create page request with custom page.
     */
    public static PageRequest of(int page) {
        return new PageRequest(page, DEFAULT_SIZE);
    }

    /**
     * Create page request with custom page and size.
     */
    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size);
    }

    /**
     * Create page request, ensuring values are within valid bounds.
     */
    public static PageRequest ofSafe(int page, int size) {
        int validPage = Math.max(page, 1);
        int validSize = Math.min(Math.max(size, 1), MAX_SIZE);
        return new PageRequest(validPage, validSize);
    }

    /**
     * Get the offset (for database queries).
     */
    public long getOffset() {
        return (long) (page - 1) * size;
    }

    /**
     * Get the limit (for database queries).
     */
    public int getLimit() {
        return size;
    }

    /**
     * Get the next page request.
     */
    public PageRequest next() {
        return new PageRequest(page + 1, size);
    }

    /**
     * Get the previous page request.
     */
    public PageRequest previous() {
        return new PageRequest(Math.max(page - 1, 1), size);
    }
}

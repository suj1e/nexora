package com.nexora.common.api;

import java.util.List;

/**
 * Paginated result wrapper.
 *
 * <p>Use this for list endpoints that return paginated data.
 *
 * <p>Example:
 * <pre>
 * {
 *   "records": [...],
 *   "total": 100,
 *   "page": 1,
 *   "size": 10,
 *   "totalPages": 10
 * }
 * </pre>
 *
 * @param <T> record type
 * @author sujie
 * @since 1.0.0
 */
public record PageResult<T>(
    List<T> records,
    long total,
    int page,
    int size,
    int totalPages
) {

    /**
     * Create empty page result.
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(List.of(), 0, 1, 10, 0);
    }

    /**
     * Create page result from records and total count.
     */
    public static <T> PageResult<T> of(List<T> records, long total, int page, int size) {
        int totalPages = (int) Math.ceil((double) total / size);
        return new PageResult<>(records, total, page, size, totalPages);
    }

    /**
     * Check if this page has next page.
     */
    public boolean hasNext() {
        return page < totalPages;
    }

    /**
     * Check if this page has previous page.
     */
    public boolean hasPrevious() {
        return page > 1;
    }

    /**
     * Check if this is the first page.
     */
    public boolean isFirst() {
        return page == 1;
    }

    /**
     * Check if this is the last page.
     */
    public boolean isLast() {
        return page == totalPages;
    }
}

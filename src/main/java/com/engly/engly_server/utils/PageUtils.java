package com.engly.engly_server.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PageUtils {

    public static int getTotalPages(int size, long totalElem) {
        return (int) Math.ceil((double) totalElem / size);
    }

    public static boolean hasNextPage(int page, int totalPages) {
        return (page + 1) < totalPages;
    }

    public static boolean hasPreviousPage(int page) {
        return page > 0;
    }
}

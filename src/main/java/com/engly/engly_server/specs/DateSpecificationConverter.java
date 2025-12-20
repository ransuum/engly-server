package com.engly.engly_server.specs;

import module java.base;

public interface DateSpecificationConverter {

    /**
     * Converts LocalDate to Instant at start of next day in UTC
     */
    static Instant toInstantPlusOneDay(LocalDate date) {
        return date.plusDays(1)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC);
    }

    /**
     * Converts LocalDate to Instant at start of day in UTC
     */
    static Instant toStartOfDay(LocalDate date) {
        return date.atStartOfDay()
                .toInstant(ZoneOffset.UTC);
    }

    /**
     * Converts LocalDate to Instant at end of day in UTC
     */
    static Instant toEndOfDay(LocalDate date) {
        return date.atTime(LocalTime.MAX)
                .toInstant(ZoneOffset.UTC);
    }
}

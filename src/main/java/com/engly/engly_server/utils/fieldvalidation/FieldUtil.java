package com.engly.engly_server.utils.fieldvalidation;

import com.engly.engly_server.models.enums.CategoryType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldUtil {
    public static boolean check(String val) {
        return val != null && !val.trim().isEmpty() && !val.isBlank() && !val.equals(" ");
    }

    public static boolean check(Integer val) {
        return val != null && val > 0;
    }

    public static boolean check(LocalDate val) {
        return val != null;
    }

    public static boolean check(Boolean isActive) {
        return isActive != null;
    }

    public static boolean check(CategoryType categoryType) {
        return categoryType != null;
    }

    public static boolean check(Long duration) {
        return duration != null;
    }

    public static boolean check(Object o) {
        return o != null;
    }
}

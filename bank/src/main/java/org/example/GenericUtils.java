package org.example;

import java.util.Objects;

public final class GenericUtils {

    // <T> относится к методу, а не к классу
    public static <T> T first(T[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        }
        return values[0];
    }

    public static <T> T last(T[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Array is empty");
        }
        return values[values.length - 1];
    }

    public static <T> boolean contains(T[] values, T target) {
        if (values == null) {
            return false;
        }
        for (T value : values) {
            // Objects.equals вызывает equals() и не падает, если value равен null
            if (Objects.equals(value, target)) {
                return true;
            }
        }
        return false;
    }
}

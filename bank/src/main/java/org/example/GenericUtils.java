package org.example;

import java.util.Arrays;
import java.util.List;
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
        // Objects.equals вызывает equals() и не падает, если value равен null
        return Arrays.stream(values)
                .anyMatch(value -> Objects.equals(value, target));
    }

    // source отдает нам значения (extends), target принимает их (super)
    public static <T> void copy(List<? extends T> source, List<? super T> target) {
        for (T item : source) {
            target.add(item);
        }
    }
}

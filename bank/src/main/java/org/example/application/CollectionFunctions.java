package org.example.application;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class CollectionFunctions {

    // ----------этап 3 - фильтрация по условию
    public static <T> List<T> filter(
            List<T> source,
            Predicate<? super T> condition) {
        List<T> result = new ArrayList<>();
        for (T value : source) {
            if (condition.test(value)) {
                result.add(value);
            }
        }
        return result;
    }

    // ----------этап 6 - преобразование списка
    public static <T, R> List<R> map(
            List<T> source,
            Function<? super T, ? extends R> mapper) {
        List<R> result = new ArrayList<>();
        for (T value : source) {
            result.add(mapper.apply(value));
        }
        return result;
    }

    // ----------этап 7 - действие над каждым элементом
    public static <T> void forEach(
            List<T> source,
            Consumer<? super T> action) {
        for (T value : source) {
            action.accept(value);
        }
    }
}

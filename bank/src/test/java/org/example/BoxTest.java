package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoxTest {

    @Test
    void stringCanBeReadAsString() {
        Box box = new Box();
        box.set("Java");

        String value = (String) box.get();

        assertEquals("Java", value);
    }

    @Test
    void numberReadAsStringThrowsClassCastException() {
        Box box = new Box();
        box.set(123);

        // код компилируется, но падает только во время выполнения
        assertThrows(
                ClassCastException.class,
                () -> {
                    String value = (String) box.get();
                }
        );
    }
}

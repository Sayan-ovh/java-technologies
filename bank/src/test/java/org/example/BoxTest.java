package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoxTest {

    @Test
    void stringBoxReturnsSavedString() {
        // Arrange
        Box<String> text = new Box<>();

        // Act
        text.set("Java");
        String s = text.get();

        // Assert
        assertEquals("Java", s);
    }

    @Test
    void integerBoxReturnsSavedNumber() {
        Box<Integer> number = new Box<>();

        number.set(42);
        Integer n = number.get();

        assertEquals(42, n);
    }
}

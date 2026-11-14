package org.example;

import org.example.domain.AccountNumber;
import org.example.domain.BankAccount;
import org.example.domain.DebitAccount;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GenericUtilsTest {

    private final DebitAccount first = new DebitAccount(new AccountNumber("1111111111"), "Ivan", 1000);
    private final DebitAccount second = new DebitAccount(new AccountNumber("2222222222"), "Petr", 2000);

    // ----------first, last, contains
    @Test
    void firstReturnsFirstElement() {
        String[] names = {"Ivan", "Petr", "Olga"};

        assertEquals("Ivan", GenericUtils.first(names));
    }

    @Test
    void lastReturnsLastElement() {
        Integer[] numbers = {1, 2, 3};

        assertEquals(3, GenericUtils.last(numbers));
    }

    @Test
    void firstThrowsExceptionForEmptyArray() {
        String[] empty = {};

        assertThrows(
                IllegalArgumentException.class,
                () -> GenericUtils.first(empty)
        );
    }

    @Test
    void lastThrowsExceptionForEmptyArray() {
        String[] empty = {};

        assertThrows(
                IllegalArgumentException.class,
                () -> GenericUtils.last(empty)
        );
    }

    @Test
    void containsFindsExistingElement() {
        String[] names = {"Ivan", "Petr", "Olga"};

        assertTrue(GenericUtils.contains(names, "Petr"));
    }

    @Test
    void containsReturnsFalseForMissingElement() {
        Integer[] numbers = {1, 2, 3};

        assertFalse(GenericUtils.contains(numbers, 10));
    }

    @Test
    void containsComparesWithEquals() {
        String[] values = {"Java"};

        // это другой объект, но равный по equals
        assertTrue(GenericUtils.contains(values, new String("Java")));
    }

    // ----------этап 6 - copy
    @Test
    void copyDebitAccountsToBankAccountList() {
        List<DebitAccount> source = new ArrayList<>();
        source.add(first);
        source.add(second);
        List<BankAccount> target = new ArrayList<>();

        GenericUtils.copy(source, target);

        assertEquals(2, target.size());
    }

    @Test
    void copyDebitAccountsToObjectList() {
        List<DebitAccount> source = new ArrayList<>();
        source.add(first);
        source.add(second);
        List<Object> target = new ArrayList<>();

        GenericUtils.copy(source, target);

        assertEquals(2, target.size());
    }

    @Test
    void copyBankAccountsToBankAccountList() {
        List<BankAccount> source = new ArrayList<>();
        source.add(first);
        source.add(second);
        List<BankAccount> target = new ArrayList<>();

        GenericUtils.copy(source, target);

        assertEquals(2, target.size());
    }

    @Test
    void copyKeepsOrder() {
        List<DebitAccount> source = new ArrayList<>();
        source.add(first);
        source.add(second);
        List<BankAccount> target = new ArrayList<>();

        GenericUtils.copy(source, target);

        assertEquals(first, target.get(0));
        assertEquals(second, target.get(1));
    }
}

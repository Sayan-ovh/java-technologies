package org.example.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BankAccountTest {

    @Test
    void accountsWithSameNumberAreEqual() {
        BankAccount first = new DebitAccount(new AccountNumber("1234567890"), "Ivan", 10000);
        BankAccount second = new DebitAccount(new AccountNumber("1234567890"), "Petr", 5);

        assertEquals(first, second);
    }

    @Test
    void accountsWithDifferentNumbersAreNotEqual() {
        BankAccount first = new DebitAccount(new AccountNumber("1234567890"), "Ivan", 10000);
        BankAccount second = new DebitAccount(new AccountNumber("0987654321"), "Ivan", 10000);

        assertNotEquals(first, second);
    }

    @Test
    void accountEqualsItself() {
        BankAccount account = new DebitAccount(new AccountNumber("1234567890"), "Ivan", 10000);

        assertEquals(account, account);
    }

    @Test
    void accountDoesNotEqualNull() {
        BankAccount account = new DebitAccount(new AccountNumber("1234567890"), "Ivan", 10000);

        assertFalse(account.equals(null));
    }

    @Test
    void equalAccountsHaveSameHashCode() {
        BankAccount first = new DebitAccount(new AccountNumber("1234567890"), "Ivan", 10000);
        BankAccount second = new DebitAccount(new AccountNumber("1234567890"), "Petr", 5);

        assertEquals(first.hashCode(), second.hashCode());
    }
}

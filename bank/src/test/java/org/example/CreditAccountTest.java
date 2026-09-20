package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreditAccountTest {

    @Test
    void canGoNegativeWithinLimit() {
        CreditAccount a = new CreditAccount("1", "Ivan", 1000, 5000);

        boolean result = a.withdraw(4000);

        assertTrue(result);
        assertEquals(-3000, a.getBalance());
    }

    @Test
    void cannotGoBelowLimit() {
        CreditAccount a = new CreditAccount("1", "Ivan", 1000, 5000);

        a.withdraw(4000); // баланс -3000
        boolean result = a.withdraw(3000); // хотел бы -6000, но лимит -5000

        assertFalse(result);
        assertEquals(-3000, a.getBalance());
    }

    @Test
    void canWithdrawExactlyToLimit() {
        CreditAccount a = new CreditAccount("1", "Ivan", 1000, 5000);

        boolean result = a.withdraw(6000); // 1000 - 6000 = -5000, ровно лимит

        assertTrue(result);
        assertEquals(-5000, a.getBalance());
    }

    @Test
    void depositDecreasesDebt() {
        CreditAccount a = new CreditAccount("1", "Ivan", 0, 5000);

        a.withdraw(3000); // -3000
        a.deposit(1000);  // -2000

        assertEquals(-2000, a.getBalance());
    }

    @Test
    void cannotWithdrawZero() {
        CreditAccount a = new CreditAccount("1", "Ivan", 1000, 5000);

        boolean result = a.withdraw(0);

        assertFalse(result);
        assertEquals(1000, a.getBalance());
    }
}
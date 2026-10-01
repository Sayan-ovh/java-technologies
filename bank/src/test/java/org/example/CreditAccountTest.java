package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CreditAccountTest {

    private static final AccountNumber NUMBER = new AccountNumber("1111111111");

    @Test
    void canGoNegativeWithinLimit() {
        CreditAccount a = new CreditAccount(NUMBER, "Ivan", 1000, 5000);

        a.withdraw(4000);

        assertEquals(-3000, a.getBalance());
    }

    @Test
    void cannotGoBelowLimit() {
        CreditAccount a = new CreditAccount(NUMBER, "Ivan", 1000, 5000);

        a.withdraw(4000); // баланс -3000

        // хотел бы -6000, но лимит -5000
        assertThrows(
                InsufficientFundsException.class,
                () -> a.withdraw(3000)
        );
    }

    @Test
    void balanceDoesNotChangeAfterFailedWithdraw() {
        CreditAccount a = new CreditAccount(NUMBER, "Ivan", 1000, 5000);

        a.withdraw(4000); // баланс -3000
        assertThrows(
                InsufficientFundsException.class,
                () -> a.withdraw(3000)
        );

        assertEquals(-3000, a.getBalance());
    }

    @Test
    void canWithdrawExactlyToLimit() {
        CreditAccount a = new CreditAccount(NUMBER, "Ivan", 1000, 5000);

        a.withdraw(6000); // 1000 - 6000 = -5000, ровно лимит

        assertEquals(-5000, a.getBalance());
    }

    @Test
    void depositDecreasesDebt() {
        CreditAccount a = new CreditAccount(NUMBER, "Ivan", 0, 5000);

        a.withdraw(3000); // -3000
        a.deposit(1000);  // -2000

        assertEquals(-2000, a.getBalance());
    }

    @Test
    void cannotWithdrawZero() {
        CreditAccount a = new CreditAccount(NUMBER, "Ivan", 1000, 5000);

        assertThrows(
                IllegalArgumentException.class,
                () -> a.withdraw(0)
        );
        assertEquals(1000, a.getBalance());
    }
}

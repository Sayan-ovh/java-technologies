package org.example.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SavingsAccountTest {

    private static final AccountNumber NUMBER = new AccountNumber("1111111111");

    @Test
    void canWithdrawIfMinimumBalanceIsKept() {
        // Arrange
        SavingsAccount account = new SavingsAccount(NUMBER, "Ivan", 10000, 1000);

        // Act
        account.withdraw(8500);

        // Assert
        assertEquals(1500.0, account.getBalance());
    }

    @Test
    void cannotGoBelowMinimumBalance() {
        SavingsAccount account = new SavingsAccount(NUMBER, "Ivan", 10000, 1000);

        account.withdraw(8500); // баланс 1500

        // осталось бы 500, а минимум 1000
        assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(1000)
        );
    }

    @Test
    void balanceDoesNotChangeAfterFailedWithdraw() {
        SavingsAccount account = new SavingsAccount(NUMBER, "Ivan", 10000, 1000);

        account.withdraw(8500); // баланс 1500
        assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(1000)
        );

        assertEquals(1500.0, account.getBalance());
    }
}

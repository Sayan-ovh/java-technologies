package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SavingsAccountTest {

    @Test
    void canWithdrawIfMinimumBalanceIsKept() {
        // Arrange
        SavingsAccount account = new SavingsAccount("1", "Ivan", 10000, 1000);

        // Act
        boolean result = account.withdraw(8500);

        // Assert
        assertTrue(result);
        assertEquals(1500.0, account.getBalance());
    }

    @Test
    void cannotGoBelowMinimumBalance() {
        SavingsAccount account = new SavingsAccount("1", "Ivan", 10000, 1000);

        account.withdraw(8500); // баланс 1500
        boolean result = account.withdraw(1000); // осталось бы 500, а минимум 1000

        assertFalse(result);
    }

    @Test
    void balanceDoesNotChangeAfterFailedWithdraw() {
        SavingsAccount account = new SavingsAccount("1", "Ivan", 10000, 1000);

        account.withdraw(8500); // баланс 1500
        account.withdraw(1000); // неуспешное снятие

        assertEquals(1500.0, account.getBalance());
    }

    @Test
    void canWithdrawExactlyToMinimumBalance() {
        SavingsAccount account = new SavingsAccount("1", "Ivan", 10000, 1000);

        boolean result = account.withdraw(9000); // останется ровно 1000

        assertTrue(result);
        assertEquals(1000.0, account.getBalance());
    }
}

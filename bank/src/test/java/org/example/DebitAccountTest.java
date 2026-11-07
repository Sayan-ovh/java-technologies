package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DebitAccountTest {

    @Test
    void initialBalanceIsSaved() {
        // Arrange
        DebitAccount account = new DebitAccount("1", "Ivan", 10000);

        // Act
        double balance = account.getBalance();

        // Assert
        assertEquals(10000.0, balance);
    }

    @Test
    void depositIncreasesBalance() {
        DebitAccount account = new DebitAccount("1", "Ivan", 10000);

        account.deposit(500);

        assertEquals(10500.0, account.getBalance());
    }

    @Test
    void zeroDepositDoesNotChangeBalance() {
        DebitAccount account = new DebitAccount("1", "Ivan", 10000);

        account.deposit(0);

        assertEquals(10000.0, account.getBalance());
    }

    @Test
    void negativeDepositDoesNotChangeBalance() {
        DebitAccount account = new DebitAccount("1", "Ivan", 10000);

        account.deposit(-500);

        assertEquals(10000.0, account.getBalance());
    }

    @Test
    void withdrawDecreasesBalance() {
        DebitAccount account = new DebitAccount("1", "Ivan", 10000);

        boolean result = account.withdraw(8000);

        assertTrue(result);
        assertEquals(2000.0, account.getBalance());
    }

    @Test
    void cannotWithdrawMoreThanBalance() {
        DebitAccount account = new DebitAccount("1", "Ivan", 2000);

        boolean result = account.withdraw(3000);

        assertFalse(result);
        assertEquals(2000.0, account.getBalance());
    }

    @Test
    void cannotWithdrawZero() {
        DebitAccount account = new DebitAccount("1", "Ivan", 10000);

        boolean result = account.withdraw(0);

        assertFalse(result);
        assertEquals(10000.0, account.getBalance());
    }

    @Test
    void cannotWithdrawNegativeAmount() {
        DebitAccount account = new DebitAccount("1", "Ivan", 10000);

        boolean result = account.withdraw(-100);

        assertFalse(result);
        assertEquals(10000.0, account.getBalance());
    }
}

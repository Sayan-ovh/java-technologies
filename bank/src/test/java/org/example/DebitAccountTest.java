package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DebitAccountTest {

    private static final AccountNumber NUMBER = new AccountNumber("1111111111");

    @Test
    void initialBalanceIsSaved() {
        // Arrange
        DebitAccount account = new DebitAccount(NUMBER, "Ivan", 10000);

        // Act
        double balance = account.getBalance();

        // Assert
        assertEquals(10000.0, balance);
    }

    @Test
    void depositIncreasesBalance() {
        DebitAccount account = new DebitAccount(NUMBER, "Ivan", 10000);

        account.deposit(500);

        assertEquals(10500.0, account.getBalance());
    }

    // ----------этап 8 - исключения при пополнении
    @Test
    void zeroDepositThrowsException() {
        DebitAccount account = new DebitAccount(NUMBER, "Ivan", 10000);

        assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(0)
        );
        assertEquals(10000.0, account.getBalance());
    }

    @Test
    void negativeDepositThrowsException() {
        DebitAccount account = new DebitAccount(NUMBER, "Ivan", 10000);

        assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(-100)
        );
        assertEquals(10000.0, account.getBalance());
    }

    // ----------этап 10 - исключения при снятии
    @Test
    void withdrawDecreasesBalance() {
        DebitAccount account = new DebitAccount(NUMBER, "Ivan", 10000);

        account.withdraw(8000);

        assertEquals(2000.0, account.getBalance());
    }

    @Test
    void cannotWithdrawMoreThanBalance() {
        DebitAccount account = new DebitAccount(NUMBER, "Ivan", 2000);

        assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(3000)
        );
        assertEquals(2000.0, account.getBalance());
    }

    @Test
    void cannotWithdrawZero() {
        DebitAccount account = new DebitAccount(NUMBER, "Ivan", 10000);

        assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(0)
        );
        assertEquals(10000.0, account.getBalance());
    }

    @Test
    void cannotWithdrawNegativeAmount() {
        DebitAccount account = new DebitAccount(NUMBER, "Ivan", 10000);

        assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(-100)
        );
        assertEquals(10000.0, account.getBalance());
    }
}

package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BankServiceTest {

    private static final AccountNumber NUMBER_A = new AccountNumber("1111111111");
    private static final AccountNumber NUMBER_B = new AccountNumber("2222222222");

    private final BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();

    private final BankService bank = new BankService(
            new InMemoryRepository<>(),
            new TransferService(new NoCommission(), new FakeNotificationService(),
                    blockedAccounts, new TransactionJournal())
    );

    // ----------этап 1 - банковский сервис
    @Test
    void openedAccountCanBeFound() {
        DebitAccount account = new DebitAccount(NUMBER_A, "Ivan", 10000);

        bank.openAccount(account);

        assertSame(account, bank.findAccount(NUMBER_A));
    }

    @Test
    void openingAccountWithSameNumberReplacesOldAccount() {
        DebitAccount oldAccount = new DebitAccount(NUMBER_A, "Ivan", 1000);
        DebitAccount newAccount = new DebitAccount(NUMBER_A, "Petr", 5000);

        bank.openAccount(oldAccount);
        bank.openAccount(newAccount);

        assertSame(newAccount, bank.findAccount(NUMBER_A));
    }

    @Test
    void closedAccountIsNotFoundAnymore() {
        bank.openAccount(new DebitAccount(NUMBER_A, "Ivan", 10000));

        boolean closed = bank.closeAccount(NUMBER_A);

        assertTrue(closed);
        assertNull(bank.findAccount(NUMBER_A));
    }

    // ----------этап 2 - перевод по номерам счетов
    @Test
    void transferByNumbersChangesBothBalances() {
        bank.openAccount(new DebitAccount(NUMBER_A, "Ivan", 10000));
        bank.openAccount(new DebitAccount(NUMBER_B, "Petr", 2000));

        bank.transfer(NUMBER_A, NUMBER_B, 3000);

        assertEquals(7000.0, bank.findAccount(NUMBER_A).getBalance());
        assertEquals(5000.0, bank.findAccount(NUMBER_B).getBalance());
    }

    @Test
    void transferFromMissingAccountThrowsException() {
        bank.openAccount(new DebitAccount(NUMBER_B, "Petr", 2000));

        assertThrows(
                IllegalArgumentException.class,
                () -> bank.transfer(NUMBER_A, NUMBER_B, 3000)
        );

        assertEquals(2000.0, bank.findAccount(NUMBER_B).getBalance());
    }

    @Test
    void transferToMissingAccountThrowsException() {
        bank.openAccount(new DebitAccount(NUMBER_A, "Ivan", 10000));

        assertThrows(
                IllegalArgumentException.class,
                () -> bank.transfer(NUMBER_A, NUMBER_B, 3000)
        );

        assertEquals(10000.0, bank.findAccount(NUMBER_A).getBalance());
    }

    @Test
    void transferFromBlockedAccountThrowsException() {
        bank.openAccount(new DebitAccount(NUMBER_A, "Ivan", 10000));
        bank.openAccount(new DebitAccount(NUMBER_B, "Petr", 2000));
        blockedAccounts.block(NUMBER_A);

        assertThrows(
                IllegalStateException.class,
                () -> bank.transfer(NUMBER_A, NUMBER_B, 3000)
        );

        assertEquals(10000.0, bank.findAccount(NUMBER_A).getBalance());
        assertEquals(2000.0, bank.findAccount(NUMBER_B).getBalance());
    }

    @Test
    void transferWithoutEnoughMoneyThrowsException() {
        bank.openAccount(new DebitAccount(NUMBER_A, "Ivan", 1000));
        bank.openAccount(new DebitAccount(NUMBER_B, "Petr", 2000));

        assertThrows(
                InsufficientFundsException.class,
                () -> bank.transfer(NUMBER_A, NUMBER_B, 5000)
        );

        assertEquals(1000.0, bank.findAccount(NUMBER_A).getBalance());
        assertEquals(2000.0, bank.findAccount(NUMBER_B).getBalance());
    }
}

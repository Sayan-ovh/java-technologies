package org.example.application;

import org.example.adapters.InMemoryAccountRepository;
import org.example.domain.AccountNumber;
import org.example.domain.BlockedAccountRegistry;
import org.example.domain.DebitAccount;
import org.example.domain.InsufficientFundsException;
import org.example.domain.NoCommission;
import org.example.domain.TransactionJournal;
import org.example.ports.AccountRepository;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransferApplicationServiceTest {

    private static final AccountNumber NUMBER_A = new AccountNumber("1111111111");
    private static final AccountNumber NUMBER_B = new AccountNumber("2222222222");

    private final BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
    private final AccountRepository repository = new InMemoryAccountRepository();
    private final AccountService accountService = new AccountService(repository);

    // сервису переводов не нужны отчеты: ему нужны только счета и TransferService
    private final TransferApplicationService transferApplicationService = new TransferApplicationService(
            repository,
            new TransferService(new NoCommission(), new FakeNotificationService(),
                    blockedAccounts, new TransactionJournal())
    );

    // ----------этап 2 - существующие правила перевода не изменились
    @Test
    void transferByNumbersChangesBothBalances() {
        accountService.open(new DebitAccount(NUMBER_A, "Ivan", 10000));
        accountService.open(new DebitAccount(NUMBER_B, "Petr", 2000));

        transferApplicationService.transfer(NUMBER_A, NUMBER_B, 3000);

        assertEquals(7000.0, accountService.find(NUMBER_A).get().getBalance());
        assertEquals(5000.0, accountService.find(NUMBER_B).get().getBalance());
    }

    @Test
    void transferFromMissingAccountThrowsException() {
        accountService.open(new DebitAccount(NUMBER_B, "Petr", 2000));

        assertThrows(
                IllegalArgumentException.class,
                () -> transferApplicationService.transfer(NUMBER_A, NUMBER_B, 3000)
        );

        assertEquals(2000.0, accountService.find(NUMBER_B).get().getBalance());
    }

    @Test
    void transferToMissingAccountThrowsException() {
        accountService.open(new DebitAccount(NUMBER_A, "Ivan", 10000));

        assertThrows(
                IllegalArgumentException.class,
                () -> transferApplicationService.transfer(NUMBER_A, NUMBER_B, 3000)
        );

        assertEquals(10000.0, accountService.find(NUMBER_A).get().getBalance());
    }

    @Test
    void transferFromBlockedAccountThrowsException() {
        accountService.open(new DebitAccount(NUMBER_A, "Ivan", 10000));
        accountService.open(new DebitAccount(NUMBER_B, "Petr", 2000));
        blockedAccounts.block(NUMBER_A);

        assertThrows(
                IllegalStateException.class,
                () -> transferApplicationService.transfer(NUMBER_A, NUMBER_B, 3000)
        );

        assertEquals(10000.0, accountService.find(NUMBER_A).get().getBalance());
        assertEquals(2000.0, accountService.find(NUMBER_B).get().getBalance());
    }

    @Test
    void transferWithoutEnoughMoneyThrowsException() {
        accountService.open(new DebitAccount(NUMBER_A, "Ivan", 1000));
        accountService.open(new DebitAccount(NUMBER_B, "Petr", 2000));

        assertThrows(
                InsufficientFundsException.class,
                () -> transferApplicationService.transfer(NUMBER_A, NUMBER_B, 5000)
        );

        assertEquals(1000.0, accountService.find(NUMBER_A).get().getBalance());
        assertEquals(2000.0, accountService.find(NUMBER_B).get().getBalance());
    }
}

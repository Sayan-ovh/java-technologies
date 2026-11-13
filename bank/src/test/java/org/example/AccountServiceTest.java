package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AccountServiceTest {

    private static final AccountNumber NUMBER_A = new AccountNumber("1111111111");

    // сервис счетов создается только с репозиторием, TransferService ему не нужен
    private final AccountService accountService = new AccountService(new InMemoryAccountRepository());

    // ----------этап 2 - AccountService работает без TransferService
    @Test
    void openedAccountCanBeFound() {
        DebitAccount account = new DebitAccount(NUMBER_A, "Ivan", 10000);

        accountService.open(account);

        assertSame(account, accountService.find(NUMBER_A).get());
    }

    @Test
    void openingAccountWithSameNumberReplacesOldAccount() {
        DebitAccount oldAccount = new DebitAccount(NUMBER_A, "Ivan", 1000);
        DebitAccount newAccount = new DebitAccount(NUMBER_A, "Petr", 5000);

        accountService.open(oldAccount);
        accountService.open(newAccount);

        assertSame(newAccount, accountService.find(NUMBER_A).get());
    }

    @Test
    void closedAccountIsNotFoundAnymore() {
        accountService.open(new DebitAccount(NUMBER_A, "Ivan", 10000));

        boolean closed = accountService.close(NUMBER_A);

        assertTrue(closed);
        assertTrue(accountService.find(NUMBER_A).isEmpty());
    }
}

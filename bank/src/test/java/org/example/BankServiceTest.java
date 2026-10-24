package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BankServiceTest {

    private static final AccountNumber NUMBER_A = new AccountNumber("1111111111");
    private static final AccountNumber NUMBER_B = new AccountNumber("2222222222");

    private final BankService bank = new BankService(
            new InMemoryRepository<>(),
            new TransferService(new NoCommission(), new FakeNotificationService(),
                    new BlockedAccountRegistry(), new TransactionJournal())
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
}

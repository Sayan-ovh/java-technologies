package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryRepositoryTest {

    private static final AccountNumber NUMBER_A = new AccountNumber("1111111111");
    private static final AccountNumber NUMBER_B = new AccountNumber("2222222222");

    @Test
    void saveAddsAccount() {
        Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();

        accounts.save(new DebitAccount(NUMBER_A, "Ivan", 1000));

        assertEquals(1, accounts.size());
    }

    @Test
    void saveNullThrowsException() {
        Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();

        assertThrows(
                IllegalArgumentException.class,
                () -> accounts.save(null)
        );
    }

    @Test
    void saveSameIdDoesNotCreateDuplicate() {
        Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();

        accounts.save(new DebitAccount(NUMBER_A, "Ivan", 1000));
        accounts.save(new DebitAccount(NUMBER_A, "Ivan", 5000));

        assertEquals(1, accounts.size());
    }

    @Test
    void saveSameIdReplacesOldAccount() {
        Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();

        accounts.save(new DebitAccount(NUMBER_A, "Ivan", 1000));
        accounts.save(new DebitAccount(NUMBER_A, "Ivan", 5000));

        assertEquals(5000.0, accounts.findById(NUMBER_A).getBalance());
    }

    @Test
    void findByIdReturnsSavedAccount() {
        Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();
        accounts.save(new DebitAccount(NUMBER_A, "Ivan", 1000));
        accounts.save(new SavingsAccount(NUMBER_B, "Petr", 5000, 500));

        BankAccount found = accounts.findById(NUMBER_B);

        assertEquals("Petr", found.getOwner());
    }

    @Test
    void findByIdThrowsExceptionIfNotFound() {
        Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();

        assertThrows(
                EntityNotFoundException.class,
                () -> accounts.findById(NUMBER_A)
        );
    }

    @Test
    void existsByIdReturnsTrueForSavedAccount() {
        Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();
        accounts.save(new DebitAccount(NUMBER_A, "Ivan", 1000));

        assertTrue(accounts.existsById(NUMBER_A));
    }

    @Test
    void existsByIdReturnsFalseForUnknownAccount() {
        Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();

        assertFalse(accounts.existsById(NUMBER_A));
    }

    @Test
    void repositoryWorksWithTransactionsAndLongId() {
        Repository<Long, Transaction> transactions = new InMemoryRepository<>();
        Transaction transaction = new Transaction(1L, TransactionType.DEPOSIT, NUMBER_A, 5000, TransactionStatus.SUCCESS);

        transactions.save(transaction);

        assertEquals(transaction, transactions.findById(1L));
    }
}

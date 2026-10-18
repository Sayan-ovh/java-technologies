package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;

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
    void findByIdReturnsNullIfNotFound() {
        Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();

        assertNull(accounts.findById(NUMBER_A));
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

    // ----------этап 1 - map вместо списка
    @Test
    void sizeGrowsAfterSavingNewId() {
        Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();

        accounts.save(new DebitAccount(NUMBER_A, "Ivan", 1000));
        assertEquals(1, accounts.size());

        accounts.save(new DebitAccount(NUMBER_B, "Petr", 2000));
        assertEquals(2, accounts.size());
    }

    @Test
    void savingSameIdReplacesOldValue() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        AccountNumber id = new AccountNumber("0000000001");
        BankAccount oldAccount = new DebitAccount(id, "Ivan", 1000);
        BankAccount newAccount = new DebitAccount(id, "Petr", 5000);

        repository.save(oldAccount);
        repository.save(newAccount);

        assertEquals(1, repository.size());
        assertSame(newAccount, repository.findById(id));
    }

    @Test
    void deleteByIdRemovesExistingAccount() {
        Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();
        accounts.save(new DebitAccount(NUMBER_A, "Ivan", 1000));

        boolean deleted = accounts.deleteById(NUMBER_A);

        assertTrue(deleted);
        assertFalse(accounts.existsById(NUMBER_A));
        assertEquals(0, accounts.size());
    }

    @Test
    void findAllReturnsAllSavedAccounts() {
        Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();
        BankAccount first = new DebitAccount(NUMBER_A, "Ivan", 1000);
        BankAccount second = new SavingsAccount(NUMBER_B, "Petr", 5000, 500);
        accounts.save(first);
        accounts.save(second);

        List<BankAccount> all = accounts.findAll();

        assertEquals(2, all.size());
        assertTrue(all.contains(first));
        assertTrue(all.contains(second));
    }
}

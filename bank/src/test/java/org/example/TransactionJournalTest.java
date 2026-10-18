package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionJournalTest {

    private static final AccountNumber NUMBER_A = new AccountNumber("1111111111");

    // ----------этап 3 - журнал транзакций
    @Test
    void transactionsAreStoredInOrderOfAdding() {
        TransactionJournal journal = new TransactionJournal();
        Transaction tx1 = new Transaction(1L, TransactionType.DEPOSIT, NUMBER_A, 1000, TransactionStatus.SUCCESS);
        Transaction tx2 = new Transaction(2L, TransactionType.WITHDRAWAL, NUMBER_A, 500, TransactionStatus.SUCCESS);
        Transaction tx3 = new Transaction(3L, TransactionType.TRANSFER, NUMBER_A, 300, TransactionStatus.SUCCESS);

        journal.add(tx1);
        journal.add(tx2);
        journal.add(tx3);

        List<Transaction> all = journal.findAll();
        assertSame(tx1, all.get(0));
        assertSame(tx2, all.get(1));
        assertSame(tx3, all.get(2));
    }

    @Test
    void twoSameTransactionsCanBeInJournalAtOnce() {
        TransactionJournal journal = new TransactionJournal();
        Transaction tx1 = new Transaction(1L, TransactionType.DEPOSIT, NUMBER_A, 1000, TransactionStatus.SUCCESS);

        journal.add(tx1);
        journal.add(tx1);

        assertEquals(2, journal.size());
    }

    @Test
    void sizeMatchesNumberOfRecords() {
        TransactionJournal journal = new TransactionJournal();
        Transaction tx1 = new Transaction(1L, TransactionType.DEPOSIT, NUMBER_A, 1000, TransactionStatus.SUCCESS);
        Transaction tx2 = new Transaction(2L, TransactionType.WITHDRAWAL, NUMBER_A, 500, TransactionStatus.SUCCESS);

        journal.add(tx1);
        journal.add(tx2);

        assertEquals(2, journal.size());
    }

    @Test
    void returnedListDoesNotExposeInternalState() {
        TransactionJournal journal = new TransactionJournal();
        Transaction tx1 = new Transaction(1L, TransactionType.DEPOSIT, NUMBER_A, 1000, TransactionStatus.SUCCESS);
        journal.add(tx1);

        List<Transaction> copy = journal.findAll();
        copy.clear();

        assertEquals(1, journal.size());
    }
}

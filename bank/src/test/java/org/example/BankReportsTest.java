package org.example;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BankReportsTest {

    private static final AccountNumber NUMBER_A = new AccountNumber("1111111111");
    private static final AccountNumber NUMBER_B = new AccountNumber("2222222222");

    private final BankReports reports = new BankReports();
    private final TransactionJournal journal = new TransactionJournal();

    private Transaction transaction(long id, AccountNumber number, double amount) {
        return new Transaction(id, TransactionType.TRANSFER, number, amount, TransactionStatus.SUCCESS);
    }

    // ----------этап 5 - статистика на Map
    @Test
    void emptyJournalGivesEmptyStatistics() {
        Map<AccountNumber, Integer> counts = reports.countByAccount(journal.findAll());
        Map<AccountNumber, Double> totals = reports.totalAmountByAccount(journal.findAll());

        assertTrue(counts.isEmpty());
        assertTrue(totals.isEmpty());
    }

    @Test
    void oneAccountIsCountedAndSummed() {
        journal.add(transaction(1L, NUMBER_A, 1000));

        Map<AccountNumber, Integer> counts = reports.countByAccount(journal.findAll());
        Map<AccountNumber, Double> totals = reports.totalAmountByAccount(journal.findAll());

        assertEquals(1, counts.size());
        assertEquals(1, counts.get(NUMBER_A));
        assertEquals(1000.0, totals.get(NUMBER_A));
    }

    @Test
    void severalAccountsAreCountedSeparately() {
        journal.add(transaction(1L, NUMBER_A, 1000));
        journal.add(transaction(2L, NUMBER_B, 500));

        Map<AccountNumber, Integer> counts = reports.countByAccount(journal.findAll());
        Map<AccountNumber, Double> totals = reports.totalAmountByAccount(journal.findAll());

        assertEquals(2, counts.size());
        assertEquals(1, counts.get(NUMBER_A));
        assertEquals(1, counts.get(NUMBER_B));
        assertEquals(1000.0, totals.get(NUMBER_A));
        assertEquals(500.0, totals.get(NUMBER_B));
    }

    @Test
    void severalTransactionsOfOneAccountAreAddedUp() {
        journal.add(transaction(1L, NUMBER_A, 1000));
        journal.add(transaction(2L, NUMBER_A, 500));
        journal.add(transaction(3L, NUMBER_A, 250));

        Map<AccountNumber, Integer> counts = reports.countByAccount(journal.findAll());
        Map<AccountNumber, Double> totals = reports.totalAmountByAccount(journal.findAll());

        assertEquals(1, counts.size());
        assertEquals(3, counts.get(NUMBER_A));
        assertEquals(1750.0, totals.get(NUMBER_A));
    }

    @Test
    void equalAccountNumbersAreOneEntry() {
        AccountNumber first = new AccountNumber("1111111111");
        AccountNumber second = new AccountNumber("1111111111");
        journal.add(transaction(1L, first, 1000));
        journal.add(transaction(2L, second, 500));

        Map<AccountNumber, Integer> counts = reports.countByAccount(journal.findAll());
        Map<AccountNumber, Double> totals = reports.totalAmountByAccount(journal.findAll());

        assertEquals(1, counts.size());
        assertEquals(2, counts.get(first));
        assertEquals(1500.0, totals.get(second));
    }
}

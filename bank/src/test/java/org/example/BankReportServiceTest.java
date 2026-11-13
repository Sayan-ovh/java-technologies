package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public class BankReportServiceTest {

    private static final AccountNumber NUMBER_A = new AccountNumber("1111111111");
    private static final AccountNumber NUMBER_B = new AccountNumber("2222222222");
    private static final AccountNumber NUMBER_C = new AccountNumber("3333333333");
    private static final AccountNumber NUMBER_D = new AccountNumber("4444444444");

    private final BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
    private final AccountRepository repository = new InMemoryAccountRepository();
    private final AccountService accountService = new AccountService(repository);
    private final BankReportService reports = new BankReportService(repository, blockedAccounts);
    private final TransactionJournal journal = new TransactionJournal();

    private Transaction transaction(long id, AccountNumber number, double amount) {
        return new Transaction(id, TransactionType.TRANSFER, number, amount, TransactionStatus.SUCCESS);
    }

    // ----------этап 4 - композиция Predicate
    @Test
    void findAccountsWithBalanceNotLessThanGivenAmount() {
        DebitAccount small = new DebitAccount(NUMBER_A, "Ivan", 1000);
        DebitAccount middle = new DebitAccount(NUMBER_B, "Petr", 5000);
        DebitAccount big = new DebitAccount(NUMBER_C, "Anna", 20000);
        accountService.open(small);
        accountService.open(middle);
        accountService.open(big);
        double minBalance = 5000;
        Predicate<BankAccount> enoughMoney =
                a -> a.getBalance() >= minBalance;

        List<BankAccount> result = reports.findAccounts(enoughMoney);

        assertEquals(2, result.size());
        assertTrue(result.contains(middle));
        assertTrue(result.contains(big));
    }

    @Test
    void findNotBlockedAccounts() {
        DebitAccount blocked = new DebitAccount(NUMBER_A, "Ivan", 1000);
        DebitAccount free1 = new DebitAccount(NUMBER_B, "Petr", 5000);
        DebitAccount free2 = new DebitAccount(NUMBER_C, "Anna", 20000);
        accountService.open(blocked);
        accountService.open(free1);
        accountService.open(free2);
        blockedAccounts.block(NUMBER_A);
        Predicate<BankAccount> notBlocked =
                a -> !blockedAccounts.isBlocked(a.getId());

        List<BankAccount> result = reports.findAccounts(notBlocked);

        assertEquals(2, result.size());
        assertTrue(result.contains(free1));
        assertTrue(result.contains(free2));
    }

    @Test
    void findOnlyDebitAccounts() {
        DebitAccount debit = new DebitAccount(NUMBER_A, "Ivan", 5000);
        SavingsAccount savings = new SavingsAccount(NUMBER_B, "Petr", 5000, 1000);
        CreditAccount credit = new CreditAccount(NUMBER_C, "Anna", 2000, 10000);
        accountService.open(debit);
        accountService.open(savings);
        accountService.open(credit);
        Predicate<BankAccount> debitCondition =
                DebitAccount.class::isInstance;

        List<BankAccount> result = reports.findAccounts(debitCondition);

        assertEquals(1, result.size());
        assertTrue(result.contains(debit));
    }

    @Test
    void findRichAndNotBlockedAccounts() {
        DebitAccount richBlocked = new DebitAccount(NUMBER_A, "Ivan", 150000);
        DebitAccount richFree = new DebitAccount(NUMBER_B, "Petr", 200000);
        DebitAccount poorFree = new DebitAccount(NUMBER_C, "Anna", 5000);
        accountService.open(richBlocked);
        accountService.open(richFree);
        accountService.open(poorFree);
        blockedAccounts.block(NUMBER_A);
        Predicate<BankAccount> rich =
                a -> a.getBalance() >= 100_000;
        Predicate<BankAccount> notBlocked =
                a -> !blockedAccounts.isBlocked(a.getId());

        List<BankAccount> result = reports.findAccounts(rich.and(notBlocked));

        assertEquals(1, result.size());
        assertTrue(result.contains(richFree));
    }

    @Test
    void findNotDebitAccounts() {
        DebitAccount debit = new DebitAccount(NUMBER_A, "Ivan", 5000);
        SavingsAccount savings = new SavingsAccount(NUMBER_B, "Petr", 5000, 1000);
        CreditAccount credit = new CreditAccount(NUMBER_C, "Anna", 2000, 10000);
        accountService.open(debit);
        accountService.open(savings);
        accountService.open(credit);
        Predicate<BankAccount> debitCondition =
                DebitAccount.class::isInstance;

        List<BankAccount> result = reports.findAccounts(debitCondition.negate());

        assertEquals(2, result.size());
        assertTrue(result.contains(savings));
        assertTrue(result.contains(credit));
    }

    @Test
    void findRichOrCreditAccounts() {
        DebitAccount rich = new DebitAccount(NUMBER_A, "Ivan", 150000);
        DebitAccount poorDebit = new DebitAccount(NUMBER_B, "Petr", 5000);
        CreditAccount credit = new CreditAccount(NUMBER_C, "Anna", 2000, 10000);
        accountService.open(rich);
        accountService.open(poorDebit);
        accountService.open(credit);
        Predicate<BankAccount> richCondition =
                a -> a.getBalance() >= 100_000;
        Predicate<BankAccount> creditCondition =
                CreditAccount.class::isInstance;

        List<BankAccount> result = reports.findAccounts(richCondition.or(creditCondition));

        assertEquals(2, result.size());
        assertTrue(result.contains(rich));
        assertTrue(result.contains(credit));
    }

    // ----------этап 5 - Comparator
    @Test
    void sortedByBalanceAscending() {
        DebitAccount a5000 = new DebitAccount(NUMBER_A, "Ivan", 5000);
        DebitAccount a1000 = new DebitAccount(NUMBER_B, "Petr", 1000);
        DebitAccount a3000 = new DebitAccount(NUMBER_C, "Anna", 3000);
        accountService.open(a5000);
        accountService.open(a1000);
        accountService.open(a3000);
        Comparator<BankAccount> byBalance =
                Comparator.comparingDouble(BankAccount::getBalance);

        List<BankAccount> result = reports.sortedAccounts(byBalance);

        assertSame(a1000, result.get(0));
        assertSame(a3000, result.get(1));
        assertSame(a5000, result.get(2));
    }

    @Test
    void sortedByBalanceDescending() {
        DebitAccount a5000 = new DebitAccount(NUMBER_A, "Ivan", 5000);
        DebitAccount a1000 = new DebitAccount(NUMBER_B, "Petr", 1000);
        DebitAccount a3000 = new DebitAccount(NUMBER_C, "Anna", 3000);
        accountService.open(a5000);
        accountService.open(a1000);
        accountService.open(a3000);
        Comparator<BankAccount> byBalance =
                Comparator.comparingDouble(BankAccount::getBalance);
        Comparator<BankAccount> byBalanceDesc =
                byBalance.reversed();

        List<BankAccount> result = reports.sortedAccounts(byBalanceDesc);

        assertSame(a5000, result.get(0));
        assertSame(a3000, result.get(1));
        assertSame(a1000, result.get(2));
    }

    @Test
    void sortedByAccountNumber() {
        DebitAccount first = new DebitAccount(NUMBER_A, "Ivan", 5000);
        DebitAccount second = new DebitAccount(NUMBER_B, "Petr", 1000);
        DebitAccount third = new DebitAccount(NUMBER_C, "Anna", 3000);
        accountService.open(third);
        accountService.open(first);
        accountService.open(second);
        Comparator<BankAccount> byNumber =
                Comparator.comparing(a -> a.getId().value());

        List<BankAccount> result = reports.sortedAccounts(byNumber);

        assertSame(first, result.get(0));
        assertSame(second, result.get(1));
        assertSame(third, result.get(2));
    }

    @Test
    void sortedByTypeThenBalance() {
        DebitAccount debit5000 = new DebitAccount(NUMBER_A, "Ivan", 5000);
        DebitAccount debit1000 = new DebitAccount(NUMBER_B, "Petr", 1000);
        CreditAccount credit = new CreditAccount(NUMBER_C, "Anna", 3000, 10000);
        SavingsAccount savings = new SavingsAccount(NUMBER_D, "Oleg", 2000, 500);
        accountService.open(debit5000);
        accountService.open(debit1000);
        accountService.open(credit);
        accountService.open(savings);
        Comparator<BankAccount> byTypeThenBalance =
                Comparator
                        .comparing((BankAccount a) -> a.getClass().getSimpleName())
                        .thenComparingDouble(BankAccount::getBalance);

        List<BankAccount> result = reports.sortedAccounts(byTypeThenBalance);

        assertSame(credit, result.get(0));
        assertSame(debit1000, result.get(1));
        assertSame(debit5000, result.get(2));
        assertSame(savings, result.get(3));
    }

    // ----------этап 2 - filter
    @Test
    void findRichAccountsWithNoMatches() {
        accountService.open(new DebitAccount(NUMBER_A, "Ivan", 1000));
        accountService.open(new DebitAccount(NUMBER_B, "Petr", 2000));

        List<BankAccount> result = reports.findRichAccounts(100_000);

        assertTrue(result.isEmpty());
    }

    @Test
    void findRichAccountsWithOneMatch() {
        DebitAccount rich = new DebitAccount(NUMBER_A, "Ivan", 150000);
        accountService.open(rich);
        accountService.open(new DebitAccount(NUMBER_B, "Petr", 2000));

        List<BankAccount> result = reports.findRichAccounts(100_000);

        assertEquals(1, result.size());
        assertSame(rich, result.get(0));
    }

    @Test
    void findRichAccountsWithSeveralMatches() {
        DebitAccount rich1 = new DebitAccount(NUMBER_A, "Ivan", 150000);
        DebitAccount rich2 = new DebitAccount(NUMBER_B, "Petr", 200000);
        DebitAccount poor = new DebitAccount(NUMBER_C, "Anna", 5000);
        accountService.open(rich1);
        accountService.open(rich2);
        accountService.open(poor);

        List<BankAccount> result = reports.findRichAccounts(100_000);

        assertEquals(2, result.size());
        assertTrue(result.contains(rich1));
        assertTrue(result.contains(rich2));
    }

    // ----------этап 3 - map
    @Test
    void getAccountSummariesConvertsTypeAndBalance() {
        accountService.open(new DebitAccount(NUMBER_A, "Ivan", 5000));
        accountService.open(new SavingsAccount(NUMBER_B, "Petr", 150000, 1000));

        List<AccountSummary> summaries = reports.getAccountSummaries();

        assertEquals(2, summaries.size());
        assertTrue(summaries.contains(new AccountSummary(NUMBER_A, "DebitAccount", 5000.0)));
        assertTrue(summaries.contains(new AccountSummary(NUMBER_B, "SavingsAccount", 150000.0)));
    }

    // ----------этап 4 - sorted
    @Test
    void richestFirstSortsByBalanceDescending() {
        DebitAccount a5000 = new DebitAccount(NUMBER_A, "Ivan", 5000);
        DebitAccount a1000 = new DebitAccount(NUMBER_B, "Petr", 1000);
        DebitAccount a3000 = new DebitAccount(NUMBER_C, "Anna", 3000);
        accountService.open(a5000);
        accountService.open(a1000);
        accountService.open(a3000);

        List<BankAccount> result = reports.richestFirst();

        assertSame(a5000, result.get(0));
        assertSame(a3000, result.get(1));
        assertSame(a1000, result.get(2));
    }

    @Test
    void richestFirstDoesNotChangeRepositoryOrder() {
        accountService.open(new DebitAccount(NUMBER_A, "Ivan", 5000));
        accountService.open(new DebitAccount(NUMBER_B, "Petr", 1000));
        accountService.open(new DebitAccount(NUMBER_C, "Anna", 3000));
        List<BankAccount> before = repository.findAll();

        reports.richestFirst();

        List<BankAccount> after = repository.findAll();
        assertEquals(before, after);
    }

    // ----------этап 5 - short-circuit
    @Test
    void hasBlockedAccountsReturnsTrueIfSomeAccountIsBlocked() {
        accountService.open(new DebitAccount(NUMBER_A, "Ivan", 5000));
        accountService.open(new DebitAccount(NUMBER_B, "Petr", 1000));
        blockedAccounts.block(NUMBER_B);

        assertTrue(reports.hasBlockedAccounts());
    }

    @Test
    void hasBlockedAccountsReturnsFalseIfNoAccountIsBlocked() {
        accountService.open(new DebitAccount(NUMBER_A, "Ivan", 5000));
        accountService.open(new DebitAccount(NUMBER_B, "Petr", 1000));

        assertFalse(reports.hasBlockedAccounts());
    }

    @Test
    void allAccountsPositiveReturnsTrueIfAllBalancesArePositive() {
        accountService.open(new DebitAccount(NUMBER_A, "Ivan", 5000));
        accountService.open(new DebitAccount(NUMBER_B, "Petr", 1000));

        assertTrue(reports.allAccountsPositive());
    }

    @Test
    void allAccountsPositiveReturnsFalseIfSomeBalanceIsZero() {
        accountService.open(new DebitAccount(NUMBER_A, "Ivan", 5000));
        accountService.open(new DebitAccount(NUMBER_B, "Petr", 0));

        assertFalse(reports.allAccountsPositive());
    }

    @Test
    void noCreditAccountsReturnsTrueIfThereAreNoCreditAccounts() {
        accountService.open(new DebitAccount(NUMBER_A, "Ivan", 5000));
        accountService.open(new SavingsAccount(NUMBER_B, "Petr", 5000, 1000));

        assertTrue(reports.noCreditAccounts());
    }

    @Test
    void noCreditAccountsReturnsFalseIfThereIsCreditAccount() {
        accountService.open(new DebitAccount(NUMBER_A, "Ivan", 5000));
        accountService.open(new CreditAccount(NUMBER_B, "Petr", 2000, 10000));

        assertFalse(reports.noCreditAccounts());
    }

    @Test
    void anyMatchStopsBeforeTheEndOfTheList() {
        List<BankAccount> list = new ArrayList<>();
        list.add(new DebitAccount(NUMBER_A, "Ivan", 150000));
        list.add(new DebitAccount(NUMBER_B, "Petr", 1000));
        list.add(new DebitAccount(NUMBER_C, "Anna", 2000));
        list.add(new DebitAccount(NUMBER_D, "Oleg", 3000));
        int[] calls = {0};
        Predicate<BankAccount> countingPredicate = a -> {
            calls[0]++;
            return a.getBalance() >= 100_000;
        };

        boolean found = list.stream().anyMatch(countingPredicate);

        assertTrue(found);
        // в списке 4 счета, а предикат вызвали только 1 раз
        assertEquals(1, calls[0]);
    }

    // ----------этап 6 - findFirst и Optional
    @Test
    void findFirstRichAccountReturnsAccountIfExists() {
        DebitAccount rich = new DebitAccount(NUMBER_A, "Ivan", 150000);
        accountService.open(rich);
        accountService.open(new DebitAccount(NUMBER_B, "Petr", 1000));

        Optional<BankAccount> result = reports.findFirstRichAccount(100_000);

        assertTrue(result.isPresent());
        assertSame(rich, result.get());
    }

    @Test
    void findFirstRichAccountReturnsEmptyIfNotFound() {
        accountService.open(new DebitAccount(NUMBER_A, "Ivan", 1000));

        Optional<BankAccount> result = reports.findFirstRichAccount(100_000);

        assertTrue(result.isEmpty());
    }

    // ----------этап 7 - sum, average, max
    @Test
    void totalBalanceOfEmptyBankIsZero() {
        assertEquals(0.0, reports.totalBalance());
    }

    @Test
    void totalBalanceSumsAllAccounts() {
        accountService.open(new DebitAccount(NUMBER_A, "Ivan", 1000));
        accountService.open(new DebitAccount(NUMBER_B, "Petr", 2000));
        accountService.open(new DebitAccount(NUMBER_C, "Anna", 3000));

        assertEquals(6000.0, reports.totalBalance());
    }

    @Test
    void averageBalanceOfEmptyBankIsEmpty() {
        assertEquals(OptionalDouble.empty(), reports.averageBalance());
    }

    @Test
    void richestAccountReturnsAccountWithMaxBalance() {
        DebitAccount richest = new DebitAccount(NUMBER_B, "Petr", 9000);
        accountService.open(new DebitAccount(NUMBER_A, "Ivan", 1000));
        accountService.open(richest);
        accountService.open(new DebitAccount(NUMBER_C, "Anna", 3000));

        Optional<BankAccount> result = reports.richestAccount();

        assertTrue(result.isPresent());
        assertSame(richest, result.get());
    }

    @Test
    void richestAccountOfEmptyBankIsEmpty() {
        assertTrue(reports.richestAccount().isEmpty());
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

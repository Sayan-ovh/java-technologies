package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public class BankServiceTest {

    private static final AccountNumber NUMBER_A = new AccountNumber("1111111111");
    private static final AccountNumber NUMBER_B = new AccountNumber("2222222222");
    private static final AccountNumber NUMBER_C = new AccountNumber("3333333333");
    private static final AccountNumber NUMBER_D = new AccountNumber("4444444444");

    private final BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
    private final Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();

    private final BankService bank = new BankService(
            repository,
            new TransferService(new NoCommission(), new FakeNotificationService(),
                    blockedAccounts, new TransactionJournal()),
            blockedAccounts
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

    // ----------этап 4 - композиция Predicate
    @Test
    void findAccountsWithBalanceNotLessThanGivenAmount() {
        DebitAccount small = new DebitAccount(NUMBER_A, "Ivan", 1000);
        DebitAccount middle = new DebitAccount(NUMBER_B, "Petr", 5000);
        DebitAccount big = new DebitAccount(NUMBER_C, "Anna", 20000);
        bank.openAccount(small);
        bank.openAccount(middle);
        bank.openAccount(big);
        double minBalance = 5000;
        Predicate<BankAccount> enoughMoney =
                a -> a.getBalance() >= minBalance;

        List<BankAccount> result = bank.findAccounts(enoughMoney);

        assertEquals(2, result.size());
        assertTrue(result.contains(middle));
        assertTrue(result.contains(big));
    }

    @Test
    void findNotBlockedAccounts() {
        DebitAccount blocked = new DebitAccount(NUMBER_A, "Ivan", 1000);
        DebitAccount free1 = new DebitAccount(NUMBER_B, "Petr", 5000);
        DebitAccount free2 = new DebitAccount(NUMBER_C, "Anna", 20000);
        bank.openAccount(blocked);
        bank.openAccount(free1);
        bank.openAccount(free2);
        blockedAccounts.block(NUMBER_A);
        Predicate<BankAccount> notBlocked =
                a -> !blockedAccounts.isBlocked(a.getId());

        List<BankAccount> result = bank.findAccounts(notBlocked);

        assertEquals(2, result.size());
        assertTrue(result.contains(free1));
        assertTrue(result.contains(free2));
    }

    @Test
    void findOnlyDebitAccounts() {
        DebitAccount debit = new DebitAccount(NUMBER_A, "Ivan", 5000);
        SavingsAccount savings = new SavingsAccount(NUMBER_B, "Petr", 5000, 1000);
        CreditAccount credit = new CreditAccount(NUMBER_C, "Anna", 2000, 10000);
        bank.openAccount(debit);
        bank.openAccount(savings);
        bank.openAccount(credit);
        Predicate<BankAccount> debitCondition =
                DebitAccount.class::isInstance;

        List<BankAccount> result = bank.findAccounts(debitCondition);

        assertEquals(1, result.size());
        assertTrue(result.contains(debit));
    }

    @Test
    void findRichAndNotBlockedAccounts() {
        DebitAccount richBlocked = new DebitAccount(NUMBER_A, "Ivan", 150000);
        DebitAccount richFree = new DebitAccount(NUMBER_B, "Petr", 200000);
        DebitAccount poorFree = new DebitAccount(NUMBER_C, "Anna", 5000);
        bank.openAccount(richBlocked);
        bank.openAccount(richFree);
        bank.openAccount(poorFree);
        blockedAccounts.block(NUMBER_A);
        Predicate<BankAccount> rich =
                a -> a.getBalance() >= 100_000;
        Predicate<BankAccount> notBlocked =
                a -> !blockedAccounts.isBlocked(a.getId());

        List<BankAccount> result = bank.findAccounts(rich.and(notBlocked));

        assertEquals(1, result.size());
        assertTrue(result.contains(richFree));
    }

    @Test
    void findNotDebitAccounts() {
        DebitAccount debit = new DebitAccount(NUMBER_A, "Ivan", 5000);
        SavingsAccount savings = new SavingsAccount(NUMBER_B, "Petr", 5000, 1000);
        CreditAccount credit = new CreditAccount(NUMBER_C, "Anna", 2000, 10000);
        bank.openAccount(debit);
        bank.openAccount(savings);
        bank.openAccount(credit);
        Predicate<BankAccount> debitCondition =
                DebitAccount.class::isInstance;

        List<BankAccount> result = bank.findAccounts(debitCondition.negate());

        assertEquals(2, result.size());
        assertTrue(result.contains(savings));
        assertTrue(result.contains(credit));
    }

    @Test
    void findRichOrCreditAccounts() {
        DebitAccount rich = new DebitAccount(NUMBER_A, "Ivan", 150000);
        DebitAccount poorDebit = new DebitAccount(NUMBER_B, "Petr", 5000);
        CreditAccount credit = new CreditAccount(NUMBER_C, "Anna", 2000, 10000);
        bank.openAccount(rich);
        bank.openAccount(poorDebit);
        bank.openAccount(credit);
        Predicate<BankAccount> richCondition =
                a -> a.getBalance() >= 100_000;
        Predicate<BankAccount> creditCondition =
                CreditAccount.class::isInstance;

        List<BankAccount> result = bank.findAccounts(richCondition.or(creditCondition));

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
        bank.openAccount(a5000);
        bank.openAccount(a1000);
        bank.openAccount(a3000);
        Comparator<BankAccount> byBalance =
                Comparator.comparingDouble(BankAccount::getBalance);

        List<BankAccount> result = bank.sortedAccounts(byBalance);

        assertSame(a1000, result.get(0));
        assertSame(a3000, result.get(1));
        assertSame(a5000, result.get(2));
    }

    @Test
    void sortedByBalanceDescending() {
        DebitAccount a5000 = new DebitAccount(NUMBER_A, "Ivan", 5000);
        DebitAccount a1000 = new DebitAccount(NUMBER_B, "Petr", 1000);
        DebitAccount a3000 = new DebitAccount(NUMBER_C, "Anna", 3000);
        bank.openAccount(a5000);
        bank.openAccount(a1000);
        bank.openAccount(a3000);
        Comparator<BankAccount> byBalance =
                Comparator.comparingDouble(BankAccount::getBalance);
        Comparator<BankAccount> byBalanceDesc =
                byBalance.reversed();

        List<BankAccount> result = bank.sortedAccounts(byBalanceDesc);

        assertSame(a5000, result.get(0));
        assertSame(a3000, result.get(1));
        assertSame(a1000, result.get(2));
    }

    @Test
    void sortedByAccountNumber() {
        DebitAccount first = new DebitAccount(NUMBER_A, "Ivan", 5000);
        DebitAccount second = new DebitAccount(NUMBER_B, "Petr", 1000);
        DebitAccount third = new DebitAccount(NUMBER_C, "Anna", 3000);
        bank.openAccount(third);
        bank.openAccount(first);
        bank.openAccount(second);
        Comparator<BankAccount> byNumber =
                Comparator.comparing(a -> a.getId().value());

        List<BankAccount> result = bank.sortedAccounts(byNumber);

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
        bank.openAccount(debit5000);
        bank.openAccount(debit1000);
        bank.openAccount(credit);
        bank.openAccount(savings);
        Comparator<BankAccount> byTypeThenBalance =
                Comparator
                        .comparing((BankAccount a) -> a.getClass().getSimpleName())
                        .thenComparingDouble(BankAccount::getBalance);

        List<BankAccount> result = bank.sortedAccounts(byTypeThenBalance);

        assertSame(credit, result.get(0));
        assertSame(debit1000, result.get(1));
        assertSame(debit5000, result.get(2));
        assertSame(savings, result.get(3));
    }

    // ----------этап 2 - filter
    @Test
    void findRichAccountsWithNoMatches() {
        bank.openAccount(new DebitAccount(NUMBER_A, "Ivan", 1000));
        bank.openAccount(new DebitAccount(NUMBER_B, "Petr", 2000));

        List<BankAccount> result = bank.findRichAccounts(100_000);

        assertTrue(result.isEmpty());
    }

    @Test
    void findRichAccountsWithOneMatch() {
        DebitAccount rich = new DebitAccount(NUMBER_A, "Ivan", 150000);
        bank.openAccount(rich);
        bank.openAccount(new DebitAccount(NUMBER_B, "Petr", 2000));

        List<BankAccount> result = bank.findRichAccounts(100_000);

        assertEquals(1, result.size());
        assertSame(rich, result.get(0));
    }

    @Test
    void findRichAccountsWithSeveralMatches() {
        DebitAccount rich1 = new DebitAccount(NUMBER_A, "Ivan", 150000);
        DebitAccount rich2 = new DebitAccount(NUMBER_B, "Petr", 200000);
        DebitAccount poor = new DebitAccount(NUMBER_C, "Anna", 5000);
        bank.openAccount(rich1);
        bank.openAccount(rich2);
        bank.openAccount(poor);

        List<BankAccount> result = bank.findRichAccounts(100_000);

        assertEquals(2, result.size());
        assertTrue(result.contains(rich1));
        assertTrue(result.contains(rich2));
    }

    // ----------этап 3 - map
    @Test
    void getAccountSummariesConvertsTypeAndBalance() {
        bank.openAccount(new DebitAccount(NUMBER_A, "Ivan", 5000));
        bank.openAccount(new SavingsAccount(NUMBER_B, "Petr", 150000, 1000));

        List<AccountSummary> summaries = bank.getAccountSummaries();

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
        bank.openAccount(a5000);
        bank.openAccount(a1000);
        bank.openAccount(a3000);

        List<BankAccount> result = bank.richestFirst();

        assertSame(a5000, result.get(0));
        assertSame(a3000, result.get(1));
        assertSame(a1000, result.get(2));
    }

    @Test
    void richestFirstDoesNotChangeRepositoryOrder() {
        bank.openAccount(new DebitAccount(NUMBER_A, "Ivan", 5000));
        bank.openAccount(new DebitAccount(NUMBER_B, "Petr", 1000));
        bank.openAccount(new DebitAccount(NUMBER_C, "Anna", 3000));
        List<BankAccount> before = repository.findAll();

        bank.richestFirst();

        List<BankAccount> after = repository.findAll();
        assertEquals(before, after);
    }

    // ----------этап 5 - short-circuit
    @Test
    void hasBlockedAccountsReturnsTrueIfSomeAccountIsBlocked() {
        bank.openAccount(new DebitAccount(NUMBER_A, "Ivan", 5000));
        bank.openAccount(new DebitAccount(NUMBER_B, "Petr", 1000));
        blockedAccounts.block(NUMBER_B);

        assertTrue(bank.hasBlockedAccounts());
    }

    @Test
    void hasBlockedAccountsReturnsFalseIfNoAccountIsBlocked() {
        bank.openAccount(new DebitAccount(NUMBER_A, "Ivan", 5000));
        bank.openAccount(new DebitAccount(NUMBER_B, "Petr", 1000));

        assertFalse(bank.hasBlockedAccounts());
    }

    @Test
    void allAccountsPositiveReturnsTrueIfAllBalancesArePositive() {
        bank.openAccount(new DebitAccount(NUMBER_A, "Ivan", 5000));
        bank.openAccount(new DebitAccount(NUMBER_B, "Petr", 1000));

        assertTrue(bank.allAccountsPositive());
    }

    @Test
    void allAccountsPositiveReturnsFalseIfSomeBalanceIsZero() {
        bank.openAccount(new DebitAccount(NUMBER_A, "Ivan", 5000));
        bank.openAccount(new DebitAccount(NUMBER_B, "Petr", 0));

        assertFalse(bank.allAccountsPositive());
    }

    @Test
    void noCreditAccountsReturnsTrueIfThereAreNoCreditAccounts() {
        bank.openAccount(new DebitAccount(NUMBER_A, "Ivan", 5000));
        bank.openAccount(new SavingsAccount(NUMBER_B, "Petr", 5000, 1000));

        assertTrue(bank.noCreditAccounts());
    }

    @Test
    void noCreditAccountsReturnsFalseIfThereIsCreditAccount() {
        bank.openAccount(new DebitAccount(NUMBER_A, "Ivan", 5000));
        bank.openAccount(new CreditAccount(NUMBER_B, "Petr", 2000, 10000));

        assertFalse(bank.noCreditAccounts());
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
        bank.openAccount(rich);
        bank.openAccount(new DebitAccount(NUMBER_B, "Petr", 1000));

        Optional<BankAccount> result = bank.findFirstRichAccount(100_000);

        assertTrue(result.isPresent());
        assertSame(rich, result.get());
    }

    @Test
    void findFirstRichAccountReturnsEmptyIfNotFound() {
        bank.openAccount(new DebitAccount(NUMBER_A, "Ivan", 1000));

        Optional<BankAccount> result = bank.findFirstRichAccount(100_000);

        assertTrue(result.isEmpty());
    }

    // ----------этап 7 - sum, average, max
    @Test
    void totalBalanceOfEmptyBankIsZero() {
        assertEquals(0.0, bank.totalBalance());
    }

    @Test
    void totalBalanceSumsAllAccounts() {
        bank.openAccount(new DebitAccount(NUMBER_A, "Ivan", 1000));
        bank.openAccount(new DebitAccount(NUMBER_B, "Petr", 2000));
        bank.openAccount(new DebitAccount(NUMBER_C, "Anna", 3000));

        assertEquals(6000.0, bank.totalBalance());
    }

    @Test
    void averageBalanceOfEmptyBankIsEmpty() {
        assertEquals(OptionalDouble.empty(), bank.averageBalance());
    }

    @Test
    void richestAccountReturnsAccountWithMaxBalance() {
        DebitAccount richest = new DebitAccount(NUMBER_B, "Petr", 9000);
        bank.openAccount(new DebitAccount(NUMBER_A, "Ivan", 1000));
        bank.openAccount(richest);
        bank.openAccount(new DebitAccount(NUMBER_C, "Anna", 3000));

        Optional<BankAccount> result = bank.richestAccount();

        assertTrue(result.isPresent());
        assertSame(richest, result.get());
    }

    @Test
    void richestAccountOfEmptyBankIsEmpty() {
        assertTrue(bank.richestAccount().isEmpty());
    }
}

package org.example.application;

import org.example.domain.AccountNumber;
import org.example.domain.BankAccount;
import org.example.domain.BlockedAccountRegistry;
import org.example.domain.CreditAccount;
import org.example.domain.DebitAccount;
import org.example.domain.Transaction;
import org.example.ports.AccountRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Predicate;

public class BankReportService {
    private final AccountRepository accounts;
    private final BlockedAccountRegistry blockedAccounts;

    public BankReportService(
            AccountRepository accounts,
            BlockedAccountRegistry blockedAccounts) {
        this.accounts = accounts;
        this.blockedAccounts = blockedAccounts;
    }

    // ----------этап 3 - поиск счетов по условию
    public List<BankAccount> findAccounts(
            Predicate<? super BankAccount> condition) {
        return CollectionFunctions.filter(accounts.findAll(), condition);
    }

    // ----------этап 5 - сортировка счетов
    public List<BankAccount> sortedAccounts(
            Comparator<? super BankAccount> comparator) {
        List<BankAccount> result =
                new ArrayList<>(accounts.findAll());
        result.sort(comparator);
        return result;
    }

    // ----------этап 2 - filter
    public List<BankAccount> findRichAccounts(double minBalance) {
        return accounts.findAll().stream()
                .filter(a -> a.getBalance() >= minBalance)
                .toList();
    }

    public List<BankAccount> findDebitAccounts() {
        return accounts.findAll().stream()
                .filter(DebitAccount.class::isInstance)
                .toList();
    }

    public List<BankAccount> findAccountsByOwner(String owner) {
        return accounts.findAll().stream()
                .filter(a -> a.getOwner().equals(owner))
                .toList();
    }

    // ----------этап 3 - map
    public List<AccountSummary> getAccountSummaries() {
        return accounts.findAll().stream()
                .map(this::toSummary)
                .toList();
    }

    private AccountSummary toSummary(BankAccount account) {
        return new AccountSummary(
                account.getId(),
                account.getClass().getSimpleName(),
                account.getBalance());
    }

    // ----------этап 4 - sorted
    public List<BankAccount> richestFirst() {
        return accounts.findAll().stream()
                .sorted(Comparator
                        .comparingDouble(BankAccount::getBalance)
                        .reversed())
                .toList();
    }

    public List<BankAccount> sortedByNumber() {
        return accounts.findAll().stream()
                .sorted(Comparator.comparing(a -> a.getId().value()))
                .toList();
    }

    public List<BankAccount> sortedByTypeThenBalance() {
        return accounts.findAll().stream()
                .sorted(Comparator
                        .comparing((BankAccount a) -> a.getClass().getSimpleName())
                        .thenComparingDouble(BankAccount::getBalance))
                .toList();
    }

    public List<BankAccount> sortedByOwnerThenNumber() {
        return accounts.findAll().stream()
                .sorted(Comparator
                        .comparing(BankAccount::getOwner)
                        .thenComparing(a -> a.getId().value()))
                .toList();
    }

    // ----------этап 5 - short-circuit
    public boolean hasBlockedAccounts() {
        return accounts.findAll().stream()
                .anyMatch(a -> blockedAccounts.isBlocked(a.getId()));
    }

    public boolean allAccountsPositive() {
        return accounts.findAll().stream()
                .allMatch(a -> a.getBalance() > 0);
    }

    public boolean noCreditAccounts() {
        return accounts.findAll().stream()
                .noneMatch(CreditAccount.class::isInstance);
    }

    // ----------этап 6 - findFirst и Optional
    public Optional<BankAccount> findFirstRichAccount(double minBalance) {
        return accounts.findAll().stream()
                .filter(a -> a.getBalance() >= minBalance)
                .findFirst();
    }

    // ----------этап 7 - sum, average, max
    public double totalBalance() {
        return accounts.findAll().stream()
                .mapToDouble(BankAccount::getBalance)
                .sum();
    }

    public OptionalDouble averageBalance() {
        return accounts.findAll().stream()
                .mapToDouble(BankAccount::getBalance)
                .average();
    }

    public Optional<BankAccount> richestAccount() {
        return accounts.findAll().stream()
                .max(Comparator.comparingDouble(BankAccount::getBalance));
    }


    // ----------этап 5 - количество исходящих операций по счетам
    public Map<AccountNumber, Integer> countByAccount(
            List<Transaction> transactions) {
        Map<AccountNumber, Integer> result = new HashMap<>();
        for (Transaction tx : transactions) {
            AccountNumber number = tx.account();
            int old = result.getOrDefault(number, 0);
            result.put(number, old + 1);
        }
        return result;
    }

    // общая сумма операций по счетам
    public Map<AccountNumber, Double> totalAmountByAccount(
            List<Transaction> transactions) {
        Map<AccountNumber, Double> result = new HashMap<>();
        for (Transaction tx : transactions) {
            AccountNumber number = tx.account();
            double old = result.getOrDefault(number, 0.0);
            result.put(number, old + tx.amount());
        }
        return result;
    }
}

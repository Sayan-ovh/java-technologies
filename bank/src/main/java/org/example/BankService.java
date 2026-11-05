package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class BankService {
    private final Repository<AccountNumber, BankAccount> accounts;
    private final TransferService transferService;
    private final BlockedAccountRegistry blockedAccounts;

    public BankService(
            Repository<AccountNumber, BankAccount> accounts,
            TransferService transferService,
            BlockedAccountRegistry blockedAccounts) {
        this.accounts = accounts;
        this.transferService = transferService;
        this.blockedAccounts = blockedAccounts;
    }

    public void openAccount(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Account must not be null");
        }
        accounts.save(account);
    }

    // если счета нет, вернется null, так же как в Repository
    public BankAccount findAccount(AccountNumber number) {
        return accounts.findById(number);
    }

    public boolean closeAccount(AccountNumber number) {
        return accounts.deleteById(number);
    }

    // ----------этап 2 - перевод по номерам счетов
    public void transfer(
            AccountNumber fromNumber,
            AccountNumber toNumber,
            double amount) {
        BankAccount from = accounts.findById(fromNumber);
        BankAccount to = accounts.findById(toNumber);

        // findById возвращает null, если счета нет, поэтому проверяем оба
        if (from == null) {
            throw new IllegalArgumentException("Source account not found");
        }
        if (to == null) {
            throw new IllegalArgumentException("Target account not found");
        }

        transferService.transfer(from, to, amount);
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
}

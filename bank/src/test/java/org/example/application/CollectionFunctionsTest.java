package org.example.application;

import org.example.domain.AccountNumber;
import org.example.domain.BankAccount;
import org.example.domain.CreditAccount;
import org.example.domain.DebitAccount;
import org.example.domain.SavingsAccount;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionFunctionsTest {

    private static final AccountNumber NUMBER_A = new AccountNumber("1111111111");
    private static final AccountNumber NUMBER_B = new AccountNumber("2222222222");
    private static final AccountNumber NUMBER_C = new AccountNumber("3333333333");

    // ----------этап 3 - фильтр по условию
    @Test
    void filterByPositiveBalance() {
        DebitAccount withMoney = new DebitAccount(NUMBER_A, "Ivan", 5000);
        DebitAccount empty = new DebitAccount(NUMBER_B, "Petr", 0);
        CreditAccount inDebt = new CreditAccount(NUMBER_C, "Anna", 1000, 5000);
        inDebt.withdraw(3000);
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(withMoney);
        accounts.add(empty);
        accounts.add(inDebt);
        Predicate<BankAccount> positiveBalance =
                account -> account.getBalance() > 0;

        List<BankAccount> result = CollectionFunctions.filter(accounts, positiveBalance);

        assertEquals(1, result.size());
        assertSame(withMoney, result.get(0));
    }

    @Test
    void filterRichAccounts() {
        DebitAccount rich = new DebitAccount(NUMBER_A, "Ivan", 150000);
        DebitAccount exactlyLimit = new DebitAccount(NUMBER_B, "Petr", 100_000);
        DebitAccount almostRich = new DebitAccount(NUMBER_C, "Anna", 99_999);
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(rich);
        accounts.add(exactlyLimit);
        accounts.add(almostRich);
        Predicate<BankAccount> richCondition =
                account -> account.getBalance() >= 100_000;

        List<BankAccount> result = CollectionFunctions.filter(accounts, richCondition);

        assertEquals(2, result.size());
        assertSame(rich, result.get(0));
        assertSame(exactlyLimit, result.get(1));
    }

    @Test
    void filterDebitAccountsOnly() {
        DebitAccount debit = new DebitAccount(NUMBER_A, "Ivan", 5000);
        SavingsAccount savings = new SavingsAccount(NUMBER_B, "Petr", 5000, 1000);
        CreditAccount credit = new CreditAccount(NUMBER_C, "Anna", 2000, 10000);
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(debit);
        accounts.add(savings);
        accounts.add(credit);
        Predicate<BankAccount> debitOnly =
                DebitAccount.class::isInstance;

        List<BankAccount> result = CollectionFunctions.filter(accounts, debitOnly);

        assertEquals(1, result.size());
        assertSame(debit, result.get(0));
    }
}

package org.example;

import org.example.domain.AccountNumber;
import org.example.domain.BankAccount;
import org.example.domain.DebitAccount;

import java.util.Comparator;
import java.util.List;

public final class AccountUtils {

    // список только читаем, значит extends (Producer Extends)
    public static double totalBalance(List<? extends BankAccount> accounts) {
        return accounts.stream()
                .mapToDouble(BankAccount::getBalance)
                .sum();
    }

    // в список только кладем, значит super (Consumer Super)
    public static void addDemoDebitAccounts(List<? super DebitAccount> target) {
        target.add(new DebitAccount(new AccountNumber("1000000001"), "Demo1", 1000));
        target.add(new DebitAccount(new AccountNumber("1000000002"), "Demo2", 2000));
    }

    // T связывает тип списка и тип результата: из List<DebitAccount> вернется DebitAccount
    public static <T extends BankAccount> T richest(List<T> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            throw new IllegalArgumentException("Account list is empty");
        }
        return accounts.stream()
                .max(Comparator.comparingDouble(BankAccount::getBalance))
                .get();
    }
}

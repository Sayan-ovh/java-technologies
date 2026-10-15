package org.example;

import java.util.List;

public final class AccountUtils {

    // список только читаем, значит extends (Producer Extends)
    public static double totalBalance(List<? extends BankAccount> accounts) {
        double total = 0;
        for (BankAccount account : accounts) {
            total += account.getBalance();
        }
        return total;
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
        T best = accounts.get(0);
        for (T account : accounts) {
            if (account.getBalance() > best.getBalance()) {
                best = account;
            }
        }
        return best;
    }
}

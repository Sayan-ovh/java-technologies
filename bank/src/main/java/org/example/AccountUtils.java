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
}

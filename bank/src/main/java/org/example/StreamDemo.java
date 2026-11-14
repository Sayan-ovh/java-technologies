package org.example;

import org.example.adapters.InMemoryAccountRepository;
import org.example.application.AccountService;
import org.example.application.BankReportService;
import org.example.domain.AccountNumber;
import org.example.domain.BankAccount;
import org.example.domain.BlockedAccountRegistry;
import org.example.domain.CreditAccount;
import org.example.domain.DebitAccount;
import org.example.domain.SavingsAccount;
import org.example.ports.AccountRepository;

import java.util.ArrayList;
import java.util.List;

public class StreamDemo {
    public static void main(String[] args) {
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Ivan", 5000));
        accounts.add(new SavingsAccount(new AccountNumber("2222222222"), "Petr", 150000, 1000));
        accounts.add(new CreditAccount(new AccountNumber("3333333333"), "Anna", 2000, 10000));

        // ----------этап 1 - один и тот же запрос: цикл и Stream API
        // исходный код, цикл
        List<BankAccount> resultByLoop = new ArrayList<>();
        for (BankAccount account : accounts) {
            if (account.getBalance() >= 100_000) {
                resultByLoop.add(account);
            }
        }
        // то же самое через Stream API
        List<BankAccount> resultByStream =
                accounts.stream()
                        .filter(a -> a.getBalance() >= 100_000)
                        .toList();
        System.out.println(resultByLoop);
        System.out.println(resultByStream);

        // ----------этап 6 - findFirst и Optional
        BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
        AccountRepository repository = new InMemoryAccountRepository();
        AccountService accountService = new AccountService(repository);
        BankReportService reports = new BankReportService(repository, blockedAccounts);
        for (BankAccount opened : accounts) {
            accountService.open(opened);
        }

        BankAccount account = reports.findFirstRichAccount(100_000)
                .orElseThrow(() ->
                        new IllegalStateException("No rich account"));
        String label = reports.findFirstRichAccount(100_000)
                .map(a -> a.getId().value())
                .orElse("not found");
        System.out.println(account);
        System.out.println(label);

        // ----------этап 8 - reduce
        // сначала вручную
        double totalByLoop = 0;
        for (BankAccount a : accounts) {
            totalByLoop += a.getBalance();
        }
        // затем через reduce
        double totalByReduce = accounts.stream()
                .map(BankAccount::getBalance)
                .reduce(0.0, Double::sum);
        System.out.println(totalByLoop);
        System.out.println(totalByReduce);
    }
}

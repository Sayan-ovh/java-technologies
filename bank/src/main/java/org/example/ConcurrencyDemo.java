package org.example;

import org.example.application.BankClient;
import org.example.application.TransferService;
import org.example.domain.AccountNumber;
import org.example.domain.BankAccount;
import org.example.domain.BlockedAccountRegistry;
import org.example.domain.DebitAccount;
import org.example.domain.NoCommission;
import org.example.domain.TransactionJournal;
import org.example.ports.NotificationService;

import java.util.ArrayList;
import java.util.List;

public class ConcurrencyDemo {
    public static void main(String[] args) throws InterruptedException {
        // ----------этап 2 - несколько запусков без синхронизации для проверки инварианта
        int runsCount = 5;
        for (int run = 1; run <= runsCount; run++) {
            runSimulation(run);
        }
    }

    private static void runSimulation(int runNumber) throws InterruptedException {
        List<BankAccount> accounts = List.of(
                new DebitAccount(new AccountNumber("1111111111"), "Ivan", 100_000),
                new DebitAccount(new AccountNumber("2222222222"), "Petr", 100_000),
                new DebitAccount(new AccountNumber("3333333333"), "Olga", 100_000),
                new DebitAccount(new AccountNumber("4444444444"), "Anna", 100_000),
                new DebitAccount(new AccountNumber("5555555555"), "Oleg", 100_000)
        );

        double initialTotal = totalBalance(accounts);

        NotificationService silentNotification = message -> {};
        TransferService transferService = new TransferService(
                new NoCommission(),
                silentNotification,
                new BlockedAccountRegistry(),
                new TransactionJournal()
        );

        int clientsCount = 5;
        int operationsPerClient = 5_000;

        List<BankClient> clients = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        for (int i = 1; i <= clientsCount; i++) {
            BankClient client = new BankClient(
                    "Client-" + i,
                    transferService,
                    accounts,
                    operationsPerClient
            );
            Thread thread = new Thread(client, "client-" + i);
            clients.add(client);
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        double finalTotal = totalBalance(accounts);
        boolean invariantKept = initialTotal == finalTotal;

        int totalSuccess = 0;
        int totalFailed = 0;
        for (BankClient client : clients) {
            totalSuccess += client.getSuccessCount();
            totalFailed += client.getFailedCount();
        }

        System.out.println("---------------------------------------------------");
        System.out.println("Run " + runNumber);
        System.out.println("Initial total: " + initialTotal);
        System.out.println("Final total: " + finalTotal);
        System.out.println("Difference: " + (finalTotal - initialTotal));
        System.out.println("Invariant kept: " + invariantKept);
        System.out.println("Success: " + totalSuccess + ", failed: " + totalFailed);
    }

    private static double totalBalance(List<BankAccount> accounts) {
        double sum = 0;
        for (BankAccount account : accounts) {
            sum += account.getBalance();
        }
        return sum;
    }
}
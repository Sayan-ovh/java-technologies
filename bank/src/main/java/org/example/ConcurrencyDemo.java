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
        // ----------этап 1 - 5 счетов по 100 000, общая сумма 500 000
        List<BankAccount> accounts = List.of(
                new DebitAccount(new AccountNumber("1111111111"), "Ivan", 100_000),
                new DebitAccount(new AccountNumber("2222222222"), "Petr", 100_000),
                new DebitAccount(new AccountNumber("3333333333"), "Olga", 100_000),
                new DebitAccount(new AccountNumber("4444444444"), "Anna", 100_000),
                new DebitAccount(new AccountNumber("5555555555"), "Oleg", 100_000)
        );

        double initialTotal = totalBalance(accounts);
        System.out.println("Initial total: " + initialTotal);

        // уведомления пустые
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

        // ждем завершения всех клиентских потоков
        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("---------------------------------------------------");
        for (BankAccount account : accounts) {
            System.out.println(account);
        }

        double finalTotal = totalBalance(accounts);
        System.out.println("Final total: " + finalTotal);
        System.out.println("Difference: " + (finalTotal - initialTotal));

        System.out.println("---------------------------------------------------");
        for (BankClient client : clients) {
            System.out.println(
                    client.getName()
                            + " -> success: " + client.getSuccessCount()
                            + ", failed: " + client.getFailedCount()
            );
        }
    }

    private static double totalBalance(List<BankAccount> accounts) {
        double sum = 0;
        for (BankAccount account : accounts) {
            sum += account.getBalance();
        }
        return sum;
    }
}
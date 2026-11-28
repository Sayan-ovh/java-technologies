package org.example.application;

import org.example.domain.BankAccount;

import java.util.List;
import java.util.Random;

// ----------этап 1 - клиент банка в отдельном потоке
public class BankClient implements Runnable {
    private final String name;
    private final TransferService transferService;
    private final List<BankAccount> accounts;
    private final int operationsCount;
    private final Random random = new Random();

    private int successCount;
    private int failedCount;

    public BankClient(
            String name,
            TransferService transferService,
            List<BankAccount> accounts,
            int operationsCount) {
        this.name = name;
        this.transferService = transferService;
        this.accounts = accounts;
        this.operationsCount = operationsCount;
    }

    @Override
    public void run() {
        for (int i = 0; i < operationsCount; i++) {
            int fromIndex = random.nextInt(accounts.size());
            int toIndex = random.nextInt(accounts.size());

            // если выпал тот же счет, берем следующий по кругу
            if (fromIndex == toIndex) {
                toIndex = (toIndex + 1) % accounts.size();
            }

            BankAccount from = accounts.get(fromIndex);
            BankAccount to = accounts.get(toIndex);
            double amount = 100 + random.nextInt(901);

            try {
                transferService.transfer(from, to, amount);
                successCount++;
            } catch (RuntimeException e) {
                // ошибка одной операции не останавливает клиента
                failedCount++;
            }
        }
    }

    public String getName() {
        return name;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailedCount() {
        return failedCount;
    }
}
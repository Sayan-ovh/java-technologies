package org.example;

public class TransferApplicationService {
    private final Repository<AccountNumber, BankAccount> accounts;
    private final TransferService transferService;

    public TransferApplicationService(
            Repository<AccountNumber, BankAccount> accounts,
            TransferService transferService) {
        this.accounts = accounts;
        this.transferService = transferService;
    }

    public void transfer(
            AccountNumber from,
            AccountNumber to,
            double amount) {
        BankAccount fromAccount = accounts.findById(from);
        BankAccount toAccount = accounts.findById(to);

        // findById возвращает null, если счета нет, поэтому проверяем оба
        if (fromAccount == null) {
            throw new IllegalArgumentException("Source account not found");
        }
        if (toAccount == null) {
            throw new IllegalArgumentException("Target account not found");
        }

        transferService.transfer(fromAccount, toAccount, amount);
    }
}

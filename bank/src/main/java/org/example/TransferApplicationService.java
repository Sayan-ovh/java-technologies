package org.example;

public class TransferApplicationService {
    private final AccountRepository accounts;
    private final TransferService transferService;

    public TransferApplicationService(
            AccountRepository accounts,
            TransferService transferService) {
        this.accounts = accounts;
        this.transferService = transferService;
    }

    public void transfer(
            AccountNumber from,
            AccountNumber to,
            double amount) {
        BankAccount fromAccount = accounts.findById(from)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));
        BankAccount toAccount = accounts.findById(to)
                .orElseThrow(() -> new IllegalArgumentException("Target account not found"));

        transferService.transfer(fromAccount, toAccount, amount);
    }
}

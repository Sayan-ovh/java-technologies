package org.example;

public class TransferService {
    private static final double TRANSFER_LIMIT = 50_000;

    private final CommissionPolicy commissionPolicy;
    private final NotificationService notificationService;

    public TransferService(CommissionPolicy commissionPolicy,
                           NotificationService notificationService) {
        this.commissionPolicy = commissionPolicy;
        this.notificationService = notificationService;
    }

    public void transfer(BankAccount from, BankAccount to, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (from.equals(to)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        if (amount > TRANSFER_LIMIT) {
            throw new TransferLimitExceededException("Transfer limit exceeded");
        }

        double commission = commissionPolicy.calculate(amount);
        double total = amount + commission;

        // если денег не хватит, withdraw бросит исключение, и до deposit дело не дойдет
        from.withdraw(total);
        to.deposit(amount);

        notificationService.notify("Transfer " + amount + " completed");
    }
}

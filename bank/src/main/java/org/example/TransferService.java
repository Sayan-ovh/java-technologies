package org.example;

public class TransferService {
    private static final double TRANSFER_LIMIT = 50_000;

    private final CommissionPolicy commissionPolicy;
    private final NotificationService notificationService;
    private final BlockedAccountRegistry blockedAccounts;
    private final TransactionJournal journal;

    public TransferService(CommissionPolicy commissionPolicy,
                           NotificationService notificationService,
                           BlockedAccountRegistry blockedAccounts,
                           TransactionJournal journal) {
        this.commissionPolicy = commissionPolicy;
        this.notificationService = notificationService;
        this.blockedAccounts = blockedAccounts;
        this.journal = journal;
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
        if (blockedAccounts.isBlocked(from.getNumber())) {
            throw new IllegalStateException("Source account is blocked");
        }

        double commission = commissionPolicy.calculate(amount);
        double total = amount + commission;

        // если денег не хватит, withdraw бросит исключение, и до deposit дело не дойдет
        from.withdraw(total);
        to.deposit(amount);

        // после успешного перевода пишем запись в журнал
        long id = journal.size() + 1;
        journal.add(new Transaction(
                id,
                TransactionType.TRANSFER,
                from.getNumber(),
                amount,
                TransactionStatus.SUCCESS
        ));

        notificationService.notify("Transfer " + amount + " completed");
    }
}

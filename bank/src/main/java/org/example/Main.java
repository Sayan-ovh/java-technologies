package org.example;

public class Main {
    public static void main(String[] args) {
        CreditAccount c = new CreditAccount(new AccountNumber("0000000002"), "Vanya", 1000, 5000);
        c.withdraw(4000);
        System.out.println(c.getBalance());
        try {
            c.withdraw(3000);
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(c.getBalance());

        System.out.println("---------------------------------------------------");

        DebitAccount a = new DebitAccount(new AccountNumber("1111111111"), "Vanya", 10000);
        DebitAccount b = new DebitAccount(new AccountNumber("2222222222"), "Petya", 2000);

        TransferService ts = new TransferService(new NoCommission(), new ConsoleNotificationService());
        tryTransfer(ts, a, b, 3000);
        System.out.println(a.getBalance());
        System.out.println(b.getBalance());
        tryTransfer(ts, a, b, 7001);
        System.out.println(a.getBalance());
        System.out.println(b.getBalance());

        System.out.println("---------------------------------------------------");

        DebitAccount a2 = new DebitAccount(new AccountNumber("1111111112"), "Vanya", 10100);
        DebitAccount b2 = new DebitAccount(new AccountNumber("2222222223"), "Petya", 2000);
        TransferService ts2 = new TransferService(new PercentCommission(1), new ConsoleNotificationService());
        tryTransfer(ts2, a2, b2, 10000);
        System.out.println(a2.getBalance());
        System.out.println(b2.getBalance());

        DebitAccount a3 = new DebitAccount(new AccountNumber("1111111113"), "Vanya", 9999);
        DebitAccount b3 = new DebitAccount(new AccountNumber("2222222224"), "Petya", 2000);
        tryTransfer(ts2, a3, b3, 10000);
        System.out.println(a3.getBalance());
        System.out.println(b3.getBalance());

        System.out.println("---------------------------------------------------");

        // ----------этап 7 - транзакции
        Transaction tx = new Transaction(
                TransactionType.DEPOSIT,
                new AccountNumber("1234567890"),
                5000,
                TransactionStatus.SUCCESS
        );
        System.out.println(tx);
        System.out.println(new Transaction(
                TransactionType.WITHDRAWAL,
                new AccountNumber("1234567890"),
                99999,
                TransactionStatus.REJECTED
        ));
        System.out.println(new Transaction(
                TransactionType.TRANSFER,
                new AccountNumber("1111111111"),
                3000,
                TransactionStatus.SUCCESS
        ));
    }

    private static void tryTransfer(TransferService service, BankAccount from, BankAccount to, double amount) {
        try {
            service.transfer(from, to, amount);
            System.out.println(true);
        } catch (RuntimeException e) {
            System.out.println(false);
        }
    }
}

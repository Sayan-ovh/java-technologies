package org.example;

public class Main {
    public static void main(String[] args) {
        // ----------этап 1 - toString
        DebitAccount a = new DebitAccount(new AccountNumber("1111111111"), "Ivan", 10000);
        DebitAccount b = new DebitAccount(new AccountNumber("2222222222"), "Petr", 1000);
        SavingsAccount s = new SavingsAccount(new AccountNumber("3333333333"), "Olga", 20000, 1000);
        CreditAccount c = new CreditAccount(new AccountNumber("4444444444"), "Anna", 500, 5000);
        System.out.println(a);
        System.out.println(b);
        System.out.println(s);
        System.out.println(c);

        System.out.println("---------------------------------------------------");

        // ----------этап 2 - equals по номеру счета
        CreditAccount sameNumber = new CreditAccount(new AccountNumber("1111111111"), "Other", 0, 100);
        System.out.println("a equals sameNumber = " + a.equals(sameNumber));
        System.out.println("a equals b = " + a.equals(b));

        System.out.println("---------------------------------------------------");

        // ----------этап 7 - транзакции
        Transaction tx1 = new Transaction(
                1L,
                TransactionType.DEPOSIT,
                new AccountNumber("1234567890"),
                5000,
                TransactionStatus.SUCCESS);
        Transaction tx2 = new Transaction(
                2L,
                TransactionType.WITHDRAWAL,
                new AccountNumber("1234567890"),
                99999,
                TransactionStatus.REJECTED);
        Transaction tx3 = new Transaction(
                3L,
                TransactionType.TRANSFER,
                new AccountNumber("1111111111"),
                3000,
                TransactionStatus.SUCCESS);
        System.out.println(tx1);
        System.out.println(tx2);
        System.out.println(tx3);

        System.out.println("---------------------------------------------------");

        // ----------этап 14 - переводы и исключения
        NotificationService notification =
                new LoggingNotificationDecorator(
                        new ConsoleNotificationAdapter()
                );
        TransferService transferService = new TransferService(new PercentCommission(1), notification, new BlockedAccountRegistry(), new TransactionJournal());

        // успешный перевод
        try {
            transferService.transfer(a, b, 3000);
            System.out.println("Transfer completed");
        } catch (InsufficientFundsException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }

        // у Petr не хватает денег
        try {
            transferService.transfer(b, a, 50000);
            System.out.println("Transfer completed");
        } catch (InsufficientFundsException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }

        // сумма выше лимита
        try {
            transferService.transfer(a, b, 60000);
            System.out.println("Transfer completed");
        } catch (TransferLimitExceededException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }

        // отрицательная сумма
        try {
            transferService.transfer(a, b, -100);
            System.out.println("Transfer completed");
        } catch (IllegalArgumentException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }

        System.out.println(a);
        System.out.println(b);
    }
}

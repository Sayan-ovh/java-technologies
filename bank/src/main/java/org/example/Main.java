package org.example;

public class Main {
    public static void main(String[] args) {
        CreditAccount c = new CreditAccount("2", "Vanya", 1000, 5000);
        System.out.println(c.withdraw(4000));
        System.out.println(c.getBalance());
        System.out.println(c.withdraw(3000));
        System.out.println(c.getBalance());

        System.out.println("---------------------------------------------------");

        DebitAccount a = new DebitAccount("A", "Vanya", 10000);
        DebitAccount b = new DebitAccount("B", "Petya", 2000);

        TransferService ts = new TransferService(new NoCommission(), new ConsoleNotificationService());
        System.out.println(ts.transfer(a, b, 3000));
        System.out.println(a.getBalance());
        System.out.println(b.getBalance());
        System.out.println(ts.transfer(a, b, 7001));
        System.out.println(a.getBalance());
        System.out.println(b.getBalance());

        System.out.println("---------------------------------------------------");

        DebitAccount a2 = new DebitAccount("A2", "Vanya", 10100);
        DebitAccount b2 = new DebitAccount("B2", "Petya", 2000);
        TransferService ts2 = new TransferService(new PercentCommission(1), new ConsoleNotificationService());
        System.out.println(ts2.transfer(a2, b2, 10000));
        System.out.println(a2.getBalance());
        System.out.println(b2.getBalance());

        DebitAccount a3 = new DebitAccount("A3", "Vanya", 9999);
        DebitAccount b3 = new DebitAccount("B3", "Petya", 2000);
        System.out.println(ts2.transfer(a3, b3, 10000));
        System.out.println(a3.getBalance());
        System.out.println(b3.getBalance());

        System.out.println("---------------------------------------------------");


    }
}
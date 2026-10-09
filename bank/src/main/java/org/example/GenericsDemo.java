package org.example;

import java.util.ArrayList;
import java.util.List;

public class GenericsDemo {
    public static void main(String[] args) {
        // ----------Box
        Box<String> text = new Box<>();
        text.set("Java");
        String s = text.get();
        System.out.println(s);
        // text.set(123); // ошибка компиляции: в Box<String> можно положить только String

        Box<Integer> number = new Box<>();
        number.set(42);
        Integer n = number.get();
        System.out.println(n);

        System.out.println("---------------------------------------------------");

        // ----------Pair
        AccountNumber accountNumber = new AccountNumber("1234567890");
        Pair<String, Integer> age = new Pair<>("Ivan", 20);
        Pair<AccountNumber, String> owner = new Pair<>(accountNumber, "Ivan");
        System.out.println(age);
        System.out.println(owner);

        System.out.println("---------------------------------------------------");

        // ----------методы first, last, contains
        String[] names = {"Ivan", "Petr", "Olga"};
        Integer[] numbers = {1, 2, 3};
        System.out.println(GenericUtils.first(names));
        System.out.println(GenericUtils.last(numbers));
        System.out.println(GenericUtils.contains(names, "Petr"));
        System.out.println(GenericUtils.contains(numbers, 10));

        System.out.println("---------------------------------------------------");

        // ----------этап 1-3 - Identifiable, Repository, InMemoryRepository
        Repository<AccountNumber, BankAccount> accounts = new InMemoryRepository<>();
        accounts.save(new DebitAccount(accountNumber, "Ivan", 10000));
        accounts.save(new SavingsAccount(new AccountNumber("2222222222"), "Petr", 5000, 1000));
        System.out.println(accounts.size());
        System.out.println(accounts.findById(accountNumber));

        // счет с таким же номером заменяет старый, дубликата нет
        accounts.save(new DebitAccount(accountNumber, "Ivan", 99999));
        System.out.println(accounts.size());
        System.out.println(accounts.findById(accountNumber));

        AccountNumber unknown = new AccountNumber("9999999999");
        System.out.println(accounts.existsById(unknown));
        try {
            accounts.findById(unknown);
        } catch (EntityNotFoundException e) {
            System.out.println("Not found: " + e.getMessage());
        }

        Repository<Long, Transaction> transactions = new InMemoryRepository<>();
        transactions.save(new Transaction(1L, TransactionType.DEPOSIT, accountNumber, 5000, TransactionStatus.SUCCESS));
        System.out.println(transactions.size());
        System.out.println(transactions.findById(1L));

        System.out.println("---------------------------------------------------");

        // ----------инвариантность
        List<DebitAccount> debitAccounts = new ArrayList<>();
        // List<BankAccount> bankAccounts = debitAccounts; // ошибка компиляции

        debitAccounts.add(new DebitAccount(new AccountNumber("3333333333"), "Anna", 1000));
        debitAccounts.add(new DebitAccount(new AccountNumber("4444444444"), "Oleg", 3000));
        debitAccounts.add(new DebitAccount(new AccountNumber("5555555555"), "Igor", 3000));

        System.out.println("---------------------------------------------------");
    }
}

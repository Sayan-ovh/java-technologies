package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class LambdaDemo {
    public static void main(String[] args) {
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Ivan", 5000));
        accounts.add(new SavingsAccount(new AccountNumber("2222222222"), "Petr", 150000, 1000));
        accounts.add(new CreditAccount(new AccountNumber("3333333333"), "Anna", 2000, 10000));

        // ----------этап 1 - от анонимного класса к lambda
        // сначала анонимный класс
        Comparator<BankAccount> byBalanceAnonymous =
                new Comparator<>() {
                    @Override
                    public int compare(BankAccount a, BankAccount b) {
                        return Double.compare(a.getBalance(), b.getBalance());
                    }
                };
        // затем эквивалентная lambda
        Comparator<BankAccount> byBalanceLambda =
                (a, b) -> Double.compare(a.getBalance(), b.getBalance());
        // и фабричный метод Comparator
        Comparator<BankAccount> byBalance =
                Comparator.comparingDouble(BankAccount::getBalance);

        List<BankAccount> sortedAnonymous = new ArrayList<>(accounts);
        sortedAnonymous.sort(byBalanceAnonymous);
        System.out.println(sortedAnonymous);

        List<BankAccount> sortedLambda = new ArrayList<>(accounts);
        sortedLambda.sort(byBalanceLambda);
        System.out.println(sortedLambda);

        List<BankAccount> sortedFactory = new ArrayList<>(accounts);
        sortedFactory.sort(byBalance);
        System.out.println(sortedFactory);

        // ----------этап 6 - Function: преобразование объектов
        Function<BankAccount, AccountSummary> toSummary =
                account -> new AccountSummary(
                        account.getId(),
                        account.getClass().getSimpleName(),
                        account.getBalance()
                );
        List<AccountSummary> summaries =
                CollectionFunctions.map(accounts, toSummary);

        // строковое представление
        Function<BankAccount, String> label =
                account -> account.getId() + " : " + account.getBalance();
        List<String> labels = CollectionFunctions.map(accounts, label);
        System.out.println(labels);

        // ----------этап 7 - Consumer: действие над каждым элементом
        Consumer<AccountSummary> printer =
                summary -> System.out.println(summary);
        CollectionFunctions.forEach(
                summaries,
                printer
        );

        // то же самое через method reference
        CollectionFunctions.forEach(
                summaries,
                System.out::println
        );
    }
}

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
    }
}

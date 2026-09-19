// 19	FizzBuzz	for, порядок условий	1..n: Fizz/Buzz/FizzBuzz
//
// Нужно вывести числа от 1 до n, но:
//
// если число делится на 3 — вывести Fizz;
// если делится на 5 — вывести Buzz;
// если делится и на 3, и на 5 — вывести FizzBuzz;
// иначе вывести само число.

import java.util.Scanner;

public class HelloWorld {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите n");
        int n = scanner.nextInt();

        for (int i = 1; i <= n; i++) {
            if (i % 3 == 0 && i % 5 == 0) {
                System.out.println("FizzBuzz");
            } else if (i % 3 == 0) {
                System.out.println("Fizz");
            } else if (i % 5 == 0) {
                System.out.println("Buzz");
            } else {
                System.out.println(i);
            }
        }

        scanner.close();
    }
}

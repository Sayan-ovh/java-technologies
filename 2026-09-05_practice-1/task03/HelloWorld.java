// 3	Сумма двух чисел	Scanner, int, арифметика	2 3 → 5

import java.util.Scanner;

public class HelloWorld {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите первое число");
        int first = scanner.nextInt();

        System.out.println("Введите второе число");
        int second = scanner.nextInt();

        int sum = first + second;
        System.out.println("Сумма: " + sum);

        scanner.close();
    }
}

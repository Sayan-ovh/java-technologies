// 15	Разворот числа	while, %, /	1234 → 4321

import java.util.Scanner;

public class HelloWorld {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите число");
        int number = scanner.nextInt();

        int reversed = 0;

        while (number != 0) {
            int digit = number % 10;
            reversed = reversed * 10 + digit;
            number = number / 10;
        }

        System.out.println("Перевёрнутое число: " + reversed);

        scanner.close();
    }
}

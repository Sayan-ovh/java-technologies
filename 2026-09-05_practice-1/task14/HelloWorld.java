// 14	Количество цифр в числе	while, /, %	12345 → 5

import java.util.Scanner;

public class HelloWorld {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите число");
        int number = scanner.nextInt();

        int count = 0;

        if (number == 0) {
            count = 1;
        }

        while (number != 0) {
            number = number / 10;
            count++;
        }

        System.out.println("Количество цифр: " + count);

        scanner.close();
    }
}

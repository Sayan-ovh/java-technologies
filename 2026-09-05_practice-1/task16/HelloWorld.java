// 16	Простое число	for, boolean flag	17 → true; 21 → false

import java.util.Scanner;

public class HelloWorld {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите число");
        int number = scanner.nextInt();

        boolean isPrime = true;

        if (number < 2) {
            isPrime = false;
        }

        for (int i = 2; i < number; i++) {
            if (number % i == 0) {
                isPrime = false;
            }
        }

        System.out.println(isPrime);

        scanner.close();
    }
}

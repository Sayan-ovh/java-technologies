// 11	Сумма чисел от 1 до n	for	n=5 → 15

import java.util.Scanner;

public class HelloWorld {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите n");
        int n = scanner.nextInt();

        int sum = 0;

        for (int i = 1; i <= n; i++) {
            sum = sum + i;
        }

        System.out.println("Сумма: " + sum);

        scanner.close();
    }
}

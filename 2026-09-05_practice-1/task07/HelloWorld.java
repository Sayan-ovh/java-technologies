// 7	Максимум из двух чисел	if/else	10 4 → 10

import java.util.Scanner;

public class HelloWorld {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите первое число");
        int first = scanner.nextInt();

        System.out.println("Введите второе число");
        int second = scanner.nextInt();

        if (first > second) {
            System.out.println("Максимум: " + first);
        } else {
            System.out.println("Максимум: " + second);
        }

        scanner.close();
    }
}

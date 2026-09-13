// 8	Максимум из трёх чисел	if/else, вложенные условия	5 9 3 → 9

import java.util.Scanner;

public class HelloWorld {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите первое число");
        int first = scanner.nextInt();

        System.out.println("Введите второе число");
        int second = scanner.nextInt();

        System.out.println("Введите третье число");
        int third = scanner.nextInt();

        if (first > second) {
            if (first > third) {
                System.out.println("Максимум: " + first);
            } else {
                System.out.println("Максимум: " + third);
            }
        } else {
            if (second > third) {
                System.out.println("Максимум: " + second);
            } else {
                System.out.println("Максимум: " + third);
            }
        }

        scanner.close();
    }
}

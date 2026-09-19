// 6	Чётное или нечётное	if, %, boolean	7 → odd; 8 → even

import java.util.Scanner;

public class HelloWorld {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите число");
        int number = scanner.nextInt();

        boolean isEven = number % 2 == 0;

        if (isEven) {
            System.out.println("even");
        } else {
            System.out.println("odd");
        }

        scanner.close();
    }
}

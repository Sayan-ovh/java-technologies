// 17	Калькулятор	switch, char/String	10 + 5 → 15; 10 / 0 → error

import java.util.Scanner;

public class HelloWorld {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите выражение через пробел, например: 10 + 5");
        int first = scanner.nextInt();
        String operation = scanner.next();
        int second = scanner.nextInt();

        switch (operation) {
            case "+":
                System.out.println(first + second);
                break;
            case "-":
                System.out.println(first - second);
                break;
            case "*":
                System.out.println(first * second);
                break;
            case "/":
                if (second == 0) {
                    System.out.println("error");
                } else {
                    System.out.println(first / second);
                }
                break;
            default:
                System.out.println("error");
        }

        scanner.close();
    }
}

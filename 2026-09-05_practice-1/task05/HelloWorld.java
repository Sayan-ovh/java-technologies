// 5	Celsius → Fahrenheit	double, выражение	0 → 32; 100 → 212

import java.util.Scanner;

public class HelloWorld {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите температуру в Цельсиях");
        double celsius = scanner.nextDouble();

        double fahrenheit = celsius * 9 / 5 + 32;

        System.out.println("В Фаренгейтах: " + fahrenheit);

        scanner.close();
    }
}

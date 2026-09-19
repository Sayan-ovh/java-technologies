// 4	Площадь и периметр прямоугольника	double, формулы	a=3, b=4 → S=12, P=14

import java.util.Scanner;

public class HelloWorld {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите сторону a");
        double a = scanner.nextDouble();

        System.out.println("Введите сторону b");
        double b = scanner.nextDouble();

        double area = a * b;
        double perimeter = 2 * (a + b);

        System.out.println("S = " + area);
        System.out.println("P = " + perimeter);

        scanner.close();
    }
}

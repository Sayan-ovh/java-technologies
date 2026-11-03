// 18	Треугольник существует?	boolean expression	3 4 5 → true; 1 2 3 → false

import java.util.Scanner;

public class HelloWorld {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите сторону a");
        int a = scanner.nextInt();

        System.out.println("Введите сторону b");
        int b = scanner.nextInt();

        System.out.println("Введите сторону c");
        int c = scanner.nextInt();

        boolean exists = a + b > c && a + c > b && b + c > a;

        System.out.println(exists);

        scanner.close();
    }
}

// 20	Первые n чисел Fibonacci	цикл, две переменные	n=7 → 0 1 1 2 3 5 8

import java.util.Scanner;

public class HelloWorld {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите n");
        int n = scanner.nextInt();

        int first = 0;
        int second = 1;

        for (int i = 0; i < n; i++) {
            System.out.print(first + " ");
            int next = first + second;
            first = second;
            second = next;
        }

        System.out.println();

        scanner.close();
    }
}

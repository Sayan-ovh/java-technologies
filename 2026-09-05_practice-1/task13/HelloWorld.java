// 13	Таблица умножения	for, форматированный вывод	n=7 → 7*1...7*10

import java.util.Scanner;

public class HelloWorld {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите n");
        int n = scanner.nextInt();

        for (int i = 1; i <= 10; i++) {
            System.out.printf("%d * %d = %d%n", n, i, n * i);
        }

        scanner.close();
    }
}

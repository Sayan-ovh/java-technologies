// 9	Оценка по баллам	if/else-if	90–100 A, 75–89 B, 60–74 C, иначе D

import java.util.Scanner;

public class HelloWorld {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите количество баллов");
        int score = scanner.nextInt();

        if (score >= 90 && score <= 100) {
            System.out.println("A");
        } else if (score >= 75 && score <= 89) {
            System.out.println("B");
        } else if (score >= 60 && score <= 74) {
            System.out.println("C");
        } else {
            System.out.println("D");
        }

        scanner.close();
    }
}

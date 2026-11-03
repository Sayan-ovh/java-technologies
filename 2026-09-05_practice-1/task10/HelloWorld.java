// 10	День недели	switch	1 → Monday, 7 → Sunday, иначе Error

import java.util.Scanner;

public class HelloWorld {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите номер дня недели (1-7)");
        int day = scanner.nextInt();

        switch (day) {
            case 1:
                System.out.println("Monday");
                break;
            case 2:
                System.out.println("Tuesday");
                break;
            case 3:
                System.out.println("Wednesday");
                break;
            case 4:
                System.out.println("Thursday");
                break;
            case 5:
                System.out.println("Friday");
                break;
            case 6:
                System.out.println("Saturday");
                break;
            case 7:
                System.out.println("Sunday");
                break;
            default:
                System.out.println("Error");
        }

        scanner.close();
    }
}

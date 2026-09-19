package org.example;

public class Math2 {

    public static long factorial(int n) {
        long result = 1;

        for (int i = 1; i <= n; i++) {
            result = result * i;
        }

        return result;
    }

    public static int nod(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }

        return Math.abs(a);
    }

    public static int nok(int a, int b) {
        if (a == 0 || b == 0) {
            return 0;
        }

        return Math.abs(a * b) / nod(a, b);
    }

    public static boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }

        for (int i = 2; i < n; i++) {
            if (n % i == 0) {
                return false;
            }
        }

        return true;
    }
}
package org.example.domain;

public class NoCommission implements CommissionPolicy {
    @Override
    public double calculate(double amount) {
        return 0;
    }
}
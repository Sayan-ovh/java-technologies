package org.example.domain;

public final class FixedCommissionPolicy implements CommissionPolicy {
    private final double fee;

    public FixedCommissionPolicy(double fee) {
        this.fee = fee;
    }

    @Override
    public double calculate(double amount) {
        return fee;
    }
}

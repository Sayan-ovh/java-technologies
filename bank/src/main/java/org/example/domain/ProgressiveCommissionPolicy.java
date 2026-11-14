package org.example.domain;

// чем больше сумма, тем выше процент: до 100 000 комиссии нет, выше 100 000 это 1.5%
public final class ProgressiveCommissionPolicy implements CommissionPolicy {
    @Override
    public double calculate(double amount) {
        if (amount > 100_000) {
            return amount * 0.015;
        }
        return 0;
    }
}

package org.example.domain;

@FunctionalInterface
public interface CommissionPolicy {
    double calculate(double amount);
}

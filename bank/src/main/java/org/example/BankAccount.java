package org.example;

public abstract class BankAccount {
    private final String number;
    private final String owner;
    private double balance;

    protected BankAccount(String number, String owner, double initialBalance) {
        this.number = number;
        this.owner = owner;
        if (initialBalance < 0) {
            this.balance = 0;
        } else {
            this.balance = initialBalance;
        }
    }

    public String getNumber() {
        return number;
    }

    public String getOwner() {
        return owner;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance = balance + amount;
        }
    }

    public abstract boolean withdraw(double amount);

    protected void setBalance(double newBalance) {
        this.balance = newBalance;
    }
}
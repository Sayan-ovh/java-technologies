package org.example;

public abstract class BankAccount {
    private final AccountNumber number;
    private final String owner;
    private double balance;

    protected BankAccount(AccountNumber number, String owner, double initialBalance) {
        this.number = number;
        this.owner = owner;
        if (initialBalance < 0) {
            this.balance = 0;
        } else {
            this.balance = initialBalance;
        }
    }

    public AccountNumber getNumber() {
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

    // счета равны, если у них одинаковый номер (баланс и тип счета не важны)
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BankAccount)) {
            return false;
        }
        BankAccount other = (BankAccount) o;
        return number.equals(other.number);
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "{number='" + number.value()
                + "', owner='" + owner
                + "', balance=" + balance + "}";
    }
}

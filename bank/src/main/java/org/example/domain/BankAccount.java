package org.example.domain;

public abstract class BankAccount implements Identifiable<AccountNumber> {
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

    @Override
    public AccountNumber getId() {
        return number;
    }

    public String getOwner() {
        return owner;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        increaseBalance(amount);
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (amount > getAvailableAmount()) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        decreaseBalance(amount);
    }

    // сколько денег можно снять со счета, у каждого типа счета свое правило
    protected abstract double getAvailableAmount();

    protected void decreaseBalance(double amount) {
        balance = balance - amount;
    }

    private void increaseBalance(double amount) {
        balance = balance + amount;
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

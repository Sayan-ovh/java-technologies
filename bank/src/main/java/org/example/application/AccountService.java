package org.example.application;

import org.example.domain.AccountNumber;
import org.example.domain.BankAccount;
import org.example.domain.RequiresRole;
import org.example.ports.AccountRepository;

import java.util.Optional;

public class AccountService {
    private final AccountRepository accounts;

    public AccountService(AccountRepository accounts) {
        this.accounts = accounts;
    }

    public void open(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Account must not be null");
        }
        accounts.save(account);
    }

    public Optional<BankAccount> find(AccountNumber number) {
        return accounts.findById(number);
    }

    @RequiresRole("ADMIN")
    public boolean close(AccountNumber number) {
        return accounts.deleteById(number);
    }
}

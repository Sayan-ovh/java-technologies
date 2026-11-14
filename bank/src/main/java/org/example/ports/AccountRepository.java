package org.example.ports;

import org.example.domain.AccountNumber;
import org.example.domain.BankAccount;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Optional<BankAccount> findById(AccountNumber number);

    void save(BankAccount account);

    boolean deleteById(AccountNumber number);

    List<BankAccount> findAll();
}

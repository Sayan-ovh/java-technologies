package org.example.adapters;

import org.example.domain.AccountNumber;
import org.example.domain.CreditAccount;
import org.example.domain.DebitAccount;
import org.example.domain.SavingsAccount;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonStorageTest {

    private final JsonStorage storage = new JsonStorage();

    private Path newPath() throws IOException {
        return Files.createTempDirectory("bank-test").resolve("accounts.json");
    }

    // ----------этап 7 - round-trip test
    @Test
    void savedAccountsAreRestoredEqual() throws IOException {
        Path path = newPath();
        List<AccountDto> source = List.of(
                new AccountDto("1111111111", "Ivan", 5000.0, "DebitAccount")
        );

        storage.saveAccounts(path, source);
        List<AccountDto> restored =
                storage.loadAccounts(path);

        assertEquals(source, restored);
    }

    @Test
    void emptyListIsRestoredAsEmptyList() throws IOException {
        Path path = newPath();
        List<AccountDto> source = new ArrayList<>();

        storage.saveAccounts(path, source);
        List<AccountDto> restored =
                storage.loadAccounts(path);

        assertEquals(source, restored);
    }

    @Test
    void severalAccountsOfDifferentTypesAreRestored() throws IOException {
        Path path = newPath();
        List<AccountDto> source = List.of(
                AccountDto.fromDomain(new DebitAccount(new AccountNumber("1111111111"), "Ivan", 5000)),
                AccountDto.fromDomain(new SavingsAccount(new AccountNumber("2222222222"), "Petr", 150000, 1000)),
                AccountDto.fromDomain(new CreditAccount(new AccountNumber("3333333333"), "Anna", 2000, 10000))
        );

        storage.saveAccounts(path, source);
        List<AccountDto> restored =
                storage.loadAccounts(path);

        assertEquals(source, restored);
    }

    @Test
    void invalidJsonThrowsException() throws IOException {
        Path path = newPath();
        Files.writeString(path, "this is not json");

        assertThrows(
                IOException.class,
                () -> storage.loadAccounts(path)
        );
    }

    @Test
    void missingFileThrowsException() throws IOException {
        Path path = newPath();

        assertThrows(
                IOException.class,
                () -> storage.loadAccounts(path)
        );
    }
}

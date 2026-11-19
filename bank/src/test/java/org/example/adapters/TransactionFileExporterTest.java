package org.example.adapters;

import org.example.domain.AccountNumber;
import org.example.domain.Transaction;
import org.example.domain.TransactionStatus;
import org.example.domain.TransactionType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionFileExporterTest {

    private static final AccountNumber NUMBER_A = new AccountNumber("1111111111");

    // ----------этап 2 - экспорт в текстовый файл
    @Test
    void exportCreatesFileAndDirectory() throws IOException {
        Path dir = Files.createTempDirectory("bank-test");
        Path path = dir.resolve("output").resolve("transactions.txt");
        List<Transaction> transactions = List.of(
                new Transaction(1L, TransactionType.TRANSFER, NUMBER_A, 1000, TransactionStatus.SUCCESS)
        );

        new TransactionFileExporter().export(path, transactions);

        assertTrue(Files.exists(path));
    }
}

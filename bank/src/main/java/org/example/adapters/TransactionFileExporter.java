package org.example.adapters;

import org.example.domain.Transaction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TransactionFileExporter {

    public void export(
            Path path,
            List<Transaction> transactions)
            throws IOException {
        Files.createDirectories(path.getParent());
        List<String> lines = new ArrayList<>();
        // первая строка это заголовок
        lines.add("id;from;to;amount;status");
        lines.addAll(transactions.stream()
                .map(tx -> toLine(TransactionDto.fromDomain(tx)))
                .toList());
        Files.write(path, lines);
    }

    private String toLine(TransactionDto dto) {
        return dto.id() + ";"
                + dto.from() + ";"
                + dto.to() + ";"
                + dto.amount() + ";"
                + dto.status();
    }
}

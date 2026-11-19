package org.example.adapters;

import org.example.domain.TransactionStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TransactionFileImporter {

    public List<TransactionDto> load(Path path)
            throws IOException {
        List<String> lines = Files.readAllLines(path);
        List<TransactionDto> result = new ArrayList<>();
        // первую строку пропускаем, это заголовок
        for (int i = 1; i < lines.size(); i++) {
            result.add(parseLine(lines.get(i), i + 1));
        }
        return result;
    }

    // для некорректной строки бросаем исключение с номером строки
    private TransactionDto parseLine(String line, int lineNumber) {
        String[] parts = line.split(";", -1);
        if (parts.length != 5) {
            throw new IllegalArgumentException("Bad line " + lineNumber + ": " + line);
        }
        try {
            long id = Long.parseLong(parts[0]);
            double amount = Double.parseDouble(parts[3]);
            TransactionStatus status = TransactionStatus.valueOf(parts[4]);
            return new TransactionDto(id, parts[1], parts[2], amount, status.name());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Bad line " + lineNumber + ": " + line);
        }
    }
}

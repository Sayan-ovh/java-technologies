package org.example.adapters;

import org.example.domain.Transaction;

public record TransactionDto(
        long id,
        String from,
        String to,
        double amount,
        String status
) {
    // в Transaction хранится один счет (отправитель), получателя нет, поэтому to пустой
    public static TransactionDto fromDomain(Transaction tx) {
        return new TransactionDto(
                tx.id(),
                tx.account().value(),
                "",
                tx.amount(),
                tx.status().name()
        );
    }
}

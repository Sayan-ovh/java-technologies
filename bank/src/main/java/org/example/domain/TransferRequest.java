package org.example.domain;

public record TransferRequest(
        AccountNumber from,
        AccountNumber to,
        double amount) {
}

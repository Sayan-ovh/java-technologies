package org.example.application;

import org.example.domain.AccountNumber;

public record AccountSummary(
        AccountNumber number,
        String type,
        double balance) {
}

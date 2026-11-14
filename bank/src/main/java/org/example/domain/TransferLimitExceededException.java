package org.example.domain;

public class TransferLimitExceededException extends RuntimeException {

    public TransferLimitExceededException(String message) {
        super(message);
    }
}

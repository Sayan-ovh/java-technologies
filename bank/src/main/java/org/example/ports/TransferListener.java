package org.example.ports;

import org.example.domain.Transaction;

public interface TransferListener {
    void onTransferCompleted(Transaction transaction);
}

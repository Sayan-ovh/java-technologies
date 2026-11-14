package org.example.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionIndexTest {

    private final TransactionIndex index = new TransactionIndex();

    private Transaction transactionFor(AccountNumber number) {
        return new Transaction(1L, TransactionType.TRANSFER, number, 1000, TransactionStatus.SUCCESS);
    }

    // ----------этап 4 - индекс на HashMap
    @Test
    void transactionCanBeFoundByEqualAccountNumber() {
        AccountNumber stored = new AccountNumber("1111111111");
        AccountNumber lookup = new AccountNumber("1111111111");

        // это разные объекты, но равные value objects
        index.add(transactionFor(stored));

        assertEquals(1, index.findByAccount(lookup).size());
    }
}

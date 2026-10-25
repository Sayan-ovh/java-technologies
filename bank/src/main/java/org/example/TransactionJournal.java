package org.example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TransactionJournal {
    private final List<Transaction> transactions = new ArrayList<>();
    private TransactionIndex index = new TransactionIndex();

    public void add(Transaction transaction) {
        // пишем и в список, и в индекс, чтобы они не расходились
        transactions.add(transaction);
        index.add(transaction);
    }

    public List<Transaction> findAll() {
        return new ArrayList<>(transactions);
    }

    // ----------этап 4 - операции счета берем из индекса
    public List<Transaction> findByAccount(AccountNumber number) {
        return index.findByAccount(number);
    }

    public int size() {
        return transactions.size();
    }

    // ----------этап 5 - удаляем отклоненные операции через iterator
    public void removeRejected() {
        Iterator<Transaction> iterator = transactions.iterator();
        while (iterator.hasNext()) {
            Transaction tx = iterator.next();
            if (tx.status() == TransactionStatus.REJECTED) {
                iterator.remove();
            }
        }

        // из списка удалили, значит индекс тоже надо обновить
        index = new TransactionIndex();
        for (Transaction tx : transactions) {
            index.add(tx);
        }
    }
}

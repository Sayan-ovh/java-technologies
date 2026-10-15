package org.example;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GenericUtilsTest {

    private final DebitAccount first = new DebitAccount(new AccountNumber("1111111111"), "Ivan", 1000);
    private final DebitAccount second = new DebitAccount(new AccountNumber("2222222222"), "Petr", 2000);

    // ----------этап 6 - copy
    @Test
    void copyDebitAccountsToBankAccountList() {
        List<DebitAccount> source = new ArrayList<>();
        source.add(first);
        source.add(second);
        List<BankAccount> target = new ArrayList<>();

        GenericUtils.copy(source, target);

        assertEquals(2, target.size());
    }

    @Test
    void copyDebitAccountsToObjectList() {
        List<DebitAccount> source = new ArrayList<>();
        source.add(first);
        source.add(second);
        List<Object> target = new ArrayList<>();

        GenericUtils.copy(source, target);

        assertEquals(2, target.size());
    }

    @Test
    void copyBankAccountsToBankAccountList() {
        List<BankAccount> source = new ArrayList<>();
        source.add(first);
        source.add(second);
        List<BankAccount> target = new ArrayList<>();

        GenericUtils.copy(source, target);

        assertEquals(2, target.size());
    }

    @Test
    void copyKeepsOrder() {
        List<DebitAccount> source = new ArrayList<>();
        source.add(first);
        source.add(second);
        List<BankAccount> target = new ArrayList<>();

        GenericUtils.copy(source, target);

        assertEquals(first, target.get(0));
        assertEquals(second, target.get(1));
    }
}

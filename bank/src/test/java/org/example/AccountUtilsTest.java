package org.example;

import org.example.domain.AccountNumber;
import org.example.domain.DebitAccount;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AccountUtilsTest {

    // ----------этап 7 - richest
    @Test
    void richestReturnsAccountWithMaxBalance() {
        DebitAccount poor = new DebitAccount(new AccountNumber("1111111111"), "Ivan", 1000);
        DebitAccount rich = new DebitAccount(new AccountNumber("2222222222"), "Petr", 5000);
        List<DebitAccount> values = new ArrayList<>();
        values.add(poor);
        values.add(rich);

        DebitAccount result = AccountUtils.richest(values);

        assertEquals(rich, result);
    }

    @Test
    void richestWorksWithOneAccount() {
        DebitAccount only = new DebitAccount(new AccountNumber("1111111111"), "Ivan", 1000);
        List<DebitAccount> values = new ArrayList<>();
        values.add(only);

        DebitAccount result = AccountUtils.richest(values);

        assertEquals(only, result);
    }

    @Test
    void richestThrowsExceptionForEmptyList() {
        List<DebitAccount> values = new ArrayList<>();

        assertThrows(
                IllegalArgumentException.class,
                () -> AccountUtils.richest(values)
        );
    }

    @Test
    void richestReturnsFirstAccountIfBalancesAreEqual() {
        DebitAccount first = new DebitAccount(new AccountNumber("1111111111"), "Ivan", 5000);
        DebitAccount second = new DebitAccount(new AccountNumber("2222222222"), "Petr", 5000);
        List<DebitAccount> values = new ArrayList<>();
        values.add(first);
        values.add(second);

        DebitAccount result = AccountUtils.richest(values);

        assertEquals(first, result);
    }
}

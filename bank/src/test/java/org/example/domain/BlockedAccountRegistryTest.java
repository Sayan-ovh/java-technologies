package org.example.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BlockedAccountRegistryTest {

    // ----------этап 2 - реестр заблокированных счетов
    @Test
    void blockedAccountIsBlocked() {
        BlockedAccountRegistry registry = new BlockedAccountRegistry();
        AccountNumber number = new AccountNumber("0000000001");

        registry.block(number);

        assertTrue(registry.isBlocked(number));
    }

    @Test
    void blockingSameAccountTwiceDoesNotCreateDuplicate() {
        BlockedAccountRegistry registry = new BlockedAccountRegistry();
        AccountNumber number = new AccountNumber("0000000001");

        registry.block(number);
        registry.block(number);

        assertEquals(1, registry.size());
    }

    @Test
    void unblockRemovesAccountNumber() {
        BlockedAccountRegistry registry = new BlockedAccountRegistry();
        AccountNumber number = new AccountNumber("0000000001");
        registry.block(number);

        registry.unblock(number);

        assertFalse(registry.isBlocked(number));
    }

    @Test
    void unknownAccountIsNotBlocked() {
        BlockedAccountRegistry registry = new BlockedAccountRegistry();

        assertFalse(registry.isBlocked(new AccountNumber("0000000002")));
    }
}

package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransferQueueTest {

    private static final AccountNumber NUMBER_A = new AccountNumber("1111111111");
    private static final AccountNumber NUMBER_B = new AccountNumber("2222222222");

    // ----------этап 4 - очередь заявок на перевод
    @Test
    void requestsComeOutInOrderOfAdding() {
        TransferQueue queue = new TransferQueue();
        TransferRequest requestA = new TransferRequest(NUMBER_A, NUMBER_B, 100);
        TransferRequest requestB = new TransferRequest(NUMBER_A, NUMBER_B, 200);
        TransferRequest requestC = new TransferRequest(NUMBER_A, NUMBER_B, 300);
        queue.add(requestA);
        queue.add(requestB);
        queue.add(requestC);

        assertSame(requestA, queue.next());
        assertSame(requestB, queue.next());
        assertSame(requestC, queue.next());
    }

    @Test
    void peekDoesNotRemoveFirstRequest() {
        TransferQueue queue = new TransferQueue();
        TransferRequest requestA = new TransferRequest(NUMBER_A, NUMBER_B, 100);
        queue.add(requestA);

        assertSame(requestA, queue.peek());

        assertEquals(1, queue.size());
    }

    @Test
    void nextOnEmptyQueueReturnsNull() {
        TransferQueue queue = new TransferQueue();

        assertNull(queue.next());
    }

    @Test
    void sizeDecreasesAfterNext() {
        TransferQueue queue = new TransferQueue();
        queue.add(new TransferRequest(NUMBER_A, NUMBER_B, 100));
        queue.add(new TransferRequest(NUMBER_A, NUMBER_B, 200));

        queue.next();

        assertEquals(1, queue.size());
    }
}

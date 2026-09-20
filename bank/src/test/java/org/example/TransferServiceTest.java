package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransferServiceTest {

    // ----------этап 8 - основные правила перевода

    @Test
    void successfulTransferChangesBothBalances() {
        // Arrange
        DebitAccount from = new DebitAccount("A", "Ivan", 10000);
        DebitAccount to = new DebitAccount("B", "Petr", 2000);
        TransferService service = new TransferService(new NoCommission(), new FakeNotificationService());

        // Act
        boolean result = service.transfer(from, to, 3000);

        // Assert
        assertTrue(result);
        assertEquals(7000.0, from.getBalance());
        assertEquals(5000.0, to.getBalance());
    }

    @Test
    void failedTransferDoesNotChangeBalances() {
        DebitAccount from = new DebitAccount("A", "Ivan", 1000);
        DebitAccount to = new DebitAccount("B", "Petr", 2000);
        TransferService service = new TransferService(new NoCommission(), new FakeNotificationService());

        boolean result = service.transfer(from, to, 3000);

        assertFalse(result);
        assertEquals(1000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }

    @Test
    void cannotTransferNegativeAmount() {
        DebitAccount from = new DebitAccount("A", "Ivan", 10000);
        DebitAccount to = new DebitAccount("B", "Petr", 2000);
        TransferService service = new TransferService(new NoCommission(), new FakeNotificationService());

        boolean result = service.transfer(from, to, -500);

        assertFalse(result);
        assertEquals(10000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }

    @Test
    void cannotTransferZeroAmount() {
        DebitAccount from = new DebitAccount("A", "Ivan", 10000);
        DebitAccount to = new DebitAccount("B", "Petr", 2000);
        TransferService service = new TransferService(new NoCommission(), new FakeNotificationService());

        boolean result = service.transfer(from, to, 0);

        assertFalse(result);
        assertEquals(10000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }

    @Test
    void cannotTransferToTheSameAccount() {
        DebitAccount account = new DebitAccount("A", "Ivan", 10000);
        TransferService service = new TransferService(new NoCommission(), new FakeNotificationService());

        boolean result = service.transfer(account, account, 3000);

        assertFalse(result);
        assertEquals(10000.0, account.getBalance());
    }

    // ----------этап 8 - комиссия

    @Test
    void commissionIsChargedFromSender() {
        DebitAccount from = new DebitAccount("A", "Ivan", 20000);
        DebitAccount to = new DebitAccount("B", "Petr", 0);
        TransferService service = new TransferService(new PercentCommission(1), new FakeNotificationService());

        service.transfer(from, to, 10000);

        // списалось 10000 + 100 комиссии
        assertEquals(9900.0, from.getBalance());
    }

    @Test
    void receiverGetsExactlyTransferAmount() {
        DebitAccount from = new DebitAccount("A", "Ivan", 20000);
        DebitAccount to = new DebitAccount("B", "Petr", 2000);
        TransferService service = new TransferService(new PercentCommission(1), new FakeNotificationService());

        service.transfer(from, to, 10000);

        // комиссия получателю не идёт
        assertEquals(12000.0, to.getBalance());
    }

    @Test
    void transferFailsIfMoneyIsNotEnoughWithCommission() {
        // денег хватает на 10000, но не хватает на 10000 + 100
        DebitAccount from = new DebitAccount("A", "Ivan", 10000);
        DebitAccount to = new DebitAccount("B", "Petr", 2000);
        TransferService service = new TransferService(new PercentCommission(1), new FakeNotificationService());

        boolean result = service.transfer(from, to, 10000);

        assertFalse(result);
        assertEquals(10000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }

    @Test
    void transferWorksIfMoneyIsExactlyEnoughWithCommission() {
        DebitAccount from = new DebitAccount("A", "Ivan", 10100);
        DebitAccount to = new DebitAccount("B", "Petr", 2000);
        TransferService service = new TransferService(new PercentCommission(1), new FakeNotificationService());

        boolean result = service.transfer(from, to, 10000);

        assertTrue(result);
        assertEquals(0.0, from.getBalance());
        assertEquals(12000.0, to.getBalance());
    }

    // ----------этап 8 - переводы между разными типами счетов

    @Test
    void transferFromDebitToDebit() {
        DebitAccount from = new DebitAccount("A", "Ivan", 5000);
        DebitAccount to = new DebitAccount("B", "Petr", 1000);
        TransferService service = new TransferService(new NoCommission(), new FakeNotificationService());

        boolean result = service.transfer(from, to, 2000);

        assertTrue(result);
        assertEquals(3000.0, from.getBalance());
        assertEquals(3000.0, to.getBalance());
    }

    @Test
    void transferFromDebitToSavings() {
        DebitAccount from = new DebitAccount("A", "Ivan", 5000);
        SavingsAccount to = new SavingsAccount("B", "Petr", 2000, 500);
        TransferService service = new TransferService(new NoCommission(), new FakeNotificationService());

        boolean result = service.transfer(from, to, 1000);

        assertTrue(result);
        assertEquals(4000.0, from.getBalance());
        assertEquals(3000.0, to.getBalance());
    }

    @Test
    void transferFromCreditToDebitCanGoNegative() {
        CreditAccount from = new CreditAccount("A", "Ivan", 1000, 5000);
        DebitAccount to = new DebitAccount("B", "Petr", 2000);
        TransferService service = new TransferService(new NoCommission(), new FakeNotificationService());

        boolean result = service.transfer(from, to, 3000);

        assertTrue(result);
        assertEquals(-2000.0, from.getBalance());
        assertEquals(5000.0, to.getBalance());
    }

    @Test
    void transferFromCreditFailsIfLimitIsExceeded() {
        CreditAccount from = new CreditAccount("A", "Ivan", 1000, 5000);
        DebitAccount to = new DebitAccount("B", "Petr", 2000);
        TransferService service = new TransferService(new NoCommission(), new FakeNotificationService());

        boolean result = service.transfer(from, to, 7000); // ушли бы в -6000

        assertFalse(result);
        assertEquals(1000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }

    @Test
    void transferFromSavingsToDebit() {
        SavingsAccount from = new SavingsAccount("A", "Ivan", 5000, 1000);
        DebitAccount to = new DebitAccount("B", "Petr", 2000);
        TransferService service = new TransferService(new NoCommission(), new FakeNotificationService());

        boolean result = service.transfer(from, to, 3000);

        assertTrue(result);
        assertEquals(2000.0, from.getBalance());
        assertEquals(5000.0, to.getBalance());
    }

    @Test
    void transferFromSavingsFailsIfMinimumBalanceIsBroken() {
        SavingsAccount from = new SavingsAccount("A", "Ivan", 5000, 1000);
        DebitAccount to = new DebitAccount("B", "Petr", 2000);
        TransferService service = new TransferService(new NoCommission(), new FakeNotificationService());

        boolean result = service.transfer(from, to, 4500); // осталось бы 500

        assertFalse(result);
        assertEquals(5000.0, from.getBalance());
        assertEquals(2000.0, to.getBalance());
    }

    // ----------этап 9 - уведомления

    @Test
    void successfulTransferSendsExactlyOneNotification() {
        DebitAccount from = new DebitAccount("A", "Ivan", 10000);
        DebitAccount to = new DebitAccount("B", "Petr", 2000);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        service.transfer(from, to, 3000);

        assertEquals(1, notificationService.getNotificationCount());
    }

    @Test
    void failedTransferDoesNotSendNotification() {
        DebitAccount from = new DebitAccount("A", "Ivan", 1000);
        DebitAccount to = new DebitAccount("B", "Petr", 2000);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        service.transfer(from, to, 3000);

        assertEquals(0, notificationService.getNotificationCount());
    }

    @Test
    void notificationHasExpectedText() {
        DebitAccount from = new DebitAccount("A", "Ivan", 10000);
        DebitAccount to = new DebitAccount("B", "Petr", 2000);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        service.transfer(from, to, 3000);

        assertEquals("Transfer 3000.0 completed", notificationService.getLastMessage());
    }
}

package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransferServiceTest {

    private static final AccountNumber NUMBER_A = new AccountNumber("1111111111");
    private static final AccountNumber NUMBER_B = new AccountNumber("2222222222");

    // ----------этап 8 - основные правила перевода
    @Test
    void successfulTransferChangesBothBalances() {
        // Arrange
        DebitAccount from = new DebitAccount(NUMBER_A, "Ivan", 10000);
        DebitAccount to = new DebitAccount(NUMBER_B, "Petr", 2000);
        TransferService service = new TransferService(new NoCommission(), new FakeNotificationService());

        // Act
        service.transfer(from, to, 3000);

        // Assert
        assertEquals(7000.0, from.getBalance());
        assertEquals(5000.0, to.getBalance());
    }

    // ----------этап 8 - комиссия
    @Test
    void commissionIsChargedFromSender() {
        DebitAccount from = new DebitAccount(NUMBER_A, "Ivan", 20000);
        DebitAccount to = new DebitAccount(NUMBER_B, "Petr", 0);
        TransferService service = new TransferService(new PercentCommission(1), new FakeNotificationService());

        service.transfer(from, to, 10000);

        // списалось 10000 + 100 комиссии
        assertEquals(9900.0, from.getBalance());
    }

    @Test
    void receiverGetsExactlyTransferAmount() {
        DebitAccount from = new DebitAccount(NUMBER_A, "Ivan", 20000);
        DebitAccount to = new DebitAccount(NUMBER_B, "Petr", 2000);
        TransferService service = new TransferService(new PercentCommission(1), new FakeNotificationService());

        service.transfer(from, to, 10000);

        // комиссия получателю не идет
        assertEquals(12000.0, to.getBalance());
    }

    // ----------этап 8 - переводы между разными типами счетов
    @Test
    void transferFromDebitToDebit() {
        DebitAccount from = new DebitAccount(NUMBER_A, "Ivan", 5000);
        DebitAccount to = new DebitAccount(NUMBER_B, "Petr", 1000);
        TransferService service = new TransferService(new NoCommission(), new FakeNotificationService());

        service.transfer(from, to, 2000);

        assertEquals(3000.0, from.getBalance());
        assertEquals(3000.0, to.getBalance());
    }

    @Test
    void transferFromDebitToSavings() {
        DebitAccount from = new DebitAccount(NUMBER_A, "Ivan", 5000);
        SavingsAccount to = new SavingsAccount(NUMBER_B, "Petr", 2000, 500);
        TransferService service = new TransferService(new NoCommission(), new FakeNotificationService());

        service.transfer(from, to, 1000);

        assertEquals(4000.0, from.getBalance());
        assertEquals(3000.0, to.getBalance());
    }

    @Test
    void transferFromCreditToDebit() {
        CreditAccount from = new CreditAccount(NUMBER_A, "Ivan", 1000, 5000);
        DebitAccount to = new DebitAccount(NUMBER_B, "Petr", 2000);
        TransferService service = new TransferService(new NoCommission(), new FakeNotificationService());

        service.transfer(from, to, 3000);

        assertEquals(-2000.0, from.getBalance());
        assertEquals(5000.0, to.getBalance());
    }

    @Test
    void transferFromSavingsToDebit() {
        SavingsAccount from = new SavingsAccount(NUMBER_A, "Ivan", 5000, 1000);
        DebitAccount to = new DebitAccount(NUMBER_B, "Petr", 2000);
        TransferService service = new TransferService(new NoCommission(), new FakeNotificationService());

        service.transfer(from, to, 3000);

        assertEquals(2000.0, from.getBalance());
        assertEquals(5000.0, to.getBalance());
    }

    // ----------этап 12 - тесты на исключения
    @Test
    void negativeTransferThrowsException() {
        DebitAccount from = new DebitAccount(NUMBER_A, "Ivan", 10000);
        DebitAccount to = new DebitAccount(NUMBER_B, "Petr", 2000);
        TransferService service = new TransferService(new NoCommission(), new FakeNotificationService());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.transfer(from, to, -500)
        );
    }

    @Test
    void zeroTransferThrowsException() {
        DebitAccount from = new DebitAccount(NUMBER_A, "Ivan", 10000);
        DebitAccount to = new DebitAccount(NUMBER_B, "Petr", 2000);
        TransferService service = new TransferService(new NoCommission(), new FakeNotificationService());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.transfer(from, to, 0)
        );
    }

    @Test
    void transferToSameAccountThrowsException() {
        DebitAccount account = new DebitAccount(NUMBER_A, "Ivan", 10000);
        TransferService service = new TransferService(new NoCommission(), new FakeNotificationService());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.transfer(account, account, 3000)
        );
    }

    @Test
    void transferWithoutEnoughMoneyThrowsException() {
        DebitAccount from = new DebitAccount(NUMBER_A, "Ivan", 1000);
        DebitAccount to = new DebitAccount(NUMBER_B, "Petr", 2000);
        TransferService service = new TransferService(new NoCommission(), new FakeNotificationService());

        InsufficientFundsException ex = assertThrows(
                InsufficientFundsException.class,
                () -> service.transfer(from, to, 5000)
        );

        assertEquals("Insufficient funds", ex.getMessage());
    }

    @Test
    void transferWithoutMoneyForCommissionThrowsException() {
        // денег хватает на 10000, но не хватает на 10000 + 100
        DebitAccount from = new DebitAccount(NUMBER_A, "Ivan", 10000);
        DebitAccount to = new DebitAccount(NUMBER_B, "Petr", 2000);
        TransferService service = new TransferService(new PercentCommission(1), new FakeNotificationService());

        assertThrows(
                InsufficientFundsException.class,
                () -> service.transfer(from, to, 10000)
        );
    }

    @Test
    void transferOverLimitThrowsException() {
        DebitAccount from = new DebitAccount(NUMBER_A, "Ivan", 100000);
        DebitAccount to = new DebitAccount(NUMBER_B, "Petr", 2000);
        TransferService service = new TransferService(new NoCommission(), new FakeNotificationService());

        assertThrows(
                TransferLimitExceededException.class,
                () -> service.transfer(from, to, 50001)
        );
    }

    // ----------этап 9 - уведомления
    @Test
    void successfulTransferSendsExactlyOneNotification() {
        DebitAccount from = new DebitAccount(NUMBER_A, "Ivan", 10000);
        DebitAccount to = new DebitAccount(NUMBER_B, "Petr", 2000);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        service.transfer(from, to, 3000);

        assertEquals(1, notificationService.getNotificationCount());
    }

    @Test
    void failedTransferDoesNotSendNotification() {
        DebitAccount from = new DebitAccount(NUMBER_A, "Ivan", 1000);
        DebitAccount to = new DebitAccount(NUMBER_B, "Petr", 2000);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        assertThrows(
                InsufficientFundsException.class,
                () -> service.transfer(from, to, 3000)
        );

        assertEquals(0, notificationService.getNotificationCount());
    }

    @Test
    void notificationHasExpectedText() {
        DebitAccount from = new DebitAccount(NUMBER_A, "Ivan", 10000);
        DebitAccount to = new DebitAccount(NUMBER_B, "Petr", 2000);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        service.transfer(from, to, 3000);

        assertEquals("Transfer 3000.0 completed", notificationService.getLastMessage());
    }
}

package org.example.adapters;

import org.example.ports.NotificationService;

public class ConsoleNotificationAdapter
        implements NotificationService {

    @Override
    public void notify(String message) {
        System.out.println(message);
    }
}

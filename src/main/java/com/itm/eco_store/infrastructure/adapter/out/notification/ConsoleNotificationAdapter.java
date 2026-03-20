package com.itm.eco_store.infrastructure.adapter.out.notification;

import com.itm.eco_store.application.port.out.NotificationPort;
import org.springframework.stereotype.Component;

@Component
public class ConsoleNotificationAdapter implements NotificationPort {

    @Override
    public void send(String message) {
        System.out.println("Enviando notificación: " + message);
    }
}

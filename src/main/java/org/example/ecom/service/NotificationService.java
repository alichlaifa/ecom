package org.example.ecom.service;

import lombok.AllArgsConstructor;
import org.example.ecom.model.Notification;
import org.example.ecom.repository.NotificationRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {

    private final NotificationRepo notificationRepo;

    public Notification saveNotification(String type, String message) {
        Notification notif = new Notification();
        notif.setType(type);
        notif.setMessage(message);
        return notificationRepo.save(notif);
    }

    public List<Notification> getLastNotifications() {
        return notificationRepo.findTop20ByOrderByCreatedAtDesc();
    }
}

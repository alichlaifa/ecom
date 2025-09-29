package org.example.ecom.controller;

import lombok.AllArgsConstructor;
import org.example.ecom.model.Notification;
import org.example.ecom.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getLastNotifications() {
        List<Notification> notifications = notificationService.getLastNotifications();
        return ResponseEntity.ok(notifications);
    }
}

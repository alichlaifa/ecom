package org.example.ecom.service;

import lombok.RequiredArgsConstructor;
import org.example.ecom.config.RabbitMQConfig;
import org.example.ecom.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consume(Map<String, Object> message) {
        try {
            Long orderId = Long.valueOf(message.get("orderId").toString());
            String status = message.get("status").toString();
            String email = message.get("email").toString();

            // Crée une notification en DB
            notificationService.saveNotification(
                    "ORDER_" + status,
                    "Commande #" + orderId + " est maintenant " + status + " pour " + email
            );

            // Ici, tu pourrais aussi appeler EmailService pour envoyer un vrai email
            System.out.println("📩 Notification envoyée pour Order #" + orderId);

        } catch (Exception e) {
            // Si erreur → lever une exception pour que RabbitMQ envoie en DLQ
            throw new RuntimeException("Erreur lors du traitement du message: " + message, e);
        }
    }
}

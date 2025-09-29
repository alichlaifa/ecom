package org.example.ecom.service;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.example.ecom.model.PaymentSession;
import org.example.ecom.model._Order;
import org.example.ecom.model.OrderStatus;
import org.example.ecom.repository.OrderRepo;
import org.example.ecom.repository.PaymentSessionRepo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebhookService {
    private final PaymentSessionRepo paymentSessionRepo;
    private final OrderRepo orderRepo;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    // set this value from Stripe Dashboard (webhook signing secret)
    private final String endpointSecret = System.getenv("STRIPE_WEBHOOK_SECRET"); // or @Value

    public void handleWebhook(String payload, String sigHeader) throws Exception {
        Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null) {
                PaymentSession ps = paymentSessionRepo.findByStripeSessionId(session.getId())
                        .orElseThrow(() -> new RuntimeException("PaymentSession not found"));

                _Order order = ps.getOrder();
                order.setStatus(OrderStatus.ACCEPTED);
                orderRepo.save(order);

                ps.setCompleted(true);
                paymentSessionRepo.save(ps);

                String message = "✅ Payment successful! Order ID " + order.getId();
                notificationService.saveNotification("ORDER_PAID", message);
                messagingTemplate.convertAndSend("/topic/orders", message);
            }
        }
    }
}

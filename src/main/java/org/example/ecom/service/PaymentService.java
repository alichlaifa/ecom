package org.example.ecom.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.ecom.config.RabbitMQConfig;
import org.example.ecom.dto.OrderRequest;
import org.example.ecom.model.OrderStatus;
import org.example.ecom.model._Order;
import org.example.ecom.repository.OrderRepo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderService orderService;
    private final OrderRepo orderRepo;
    private final RabbitTemplate rabbitTemplate;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public Map<String, Object> createCheckoutSession(OrderRequest orderRequest, String idempotencyKey) throws StripeException {
        _Order order;

        if (idempotencyKey != null) {
            Optional<_Order> existingOrder = orderRepo.findByTransactionId(idempotencyKey);
            if (existingOrder.isPresent()) {
                order = existingOrder.get();
            } else {
                order = orderService.saveOrder(orderRequest, idempotencyKey);
            }
        } else {
            order = orderService.saveOrder(orderRequest, null);
        }

        var lineItems = order.getOrderItems().stream().map(item ->
                SessionCreateParams.LineItem.builder()
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("usd")
                                        .setUnitAmount((long) (item.getProduct().getPrice() * 100))
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName(item.getProduct().getName())
                                                        .build()
                                        )
                                        .build()
                        )
                        .setQuantity((long) item.getQuantity())
                        .build()
        ).toList();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:4200/order?success=true&orderId=" + order.getId())
                .setCancelUrl("http://localhost:4200/order?canceled=true")
                .addAllLineItem(lineItems)
                .build();

        Session session = Session.create(params);

        Map<String, Object> response = new HashMap<>();
        response.put("id", session.getId());
        response.put("orderId", order.getId());
        return response;
    }

    public String confirmPayment(Long orderId) {
        _Order order = orderService.updateStatus(orderId, OrderStatus.ACCEPTED);
        Map<String, Object> message = new HashMap<>();
        message.put("orderId", order.getId());
        message.put("userId", order.getUser().getId());
        message.put("status", order.getStatus().name());
        message.put("email", order.getUser().getEmail());

        // Publier dans RabbitMQ
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                message
        );

        return "Payment confirmed for order #" + orderId;
    }
}

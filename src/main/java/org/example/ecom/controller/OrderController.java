package org.example.ecom.controller;

import lombok.AllArgsConstructor;
import org.example.ecom.dto.OrderRequest;
import org.example.ecom.model.OrderStatus;
import org.example.ecom.model._Order;
import org.example.ecom.repository.OrderRepo;
import org.example.ecom.service.NotificationService;
import org.example.ecom.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;
    private final OrderRepo orderRepo;
    private SimpMessagingTemplate messagingTemplate;
    private NotificationService notificationService;


    @GetMapping
    public List<_Order> getOrders() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<_Order> getOrder(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @PostMapping
    public ResponseEntity<_Order> saveOrder(
            @RequestBody OrderRequest orderRequest,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {

        // If client retries with same Idempotency-Key, return same order
        if (idempotencyKey != null) {
            Optional<_Order> existingOrder = orderRepo.findByTransactionId(idempotencyKey);
            if (existingOrder.isPresent()) {
                return ResponseEntity.ok(existingOrder.get());
            }
        }

        _Order order = orderService.saveOrder(orderRequest, idempotencyKey);

        String message = "New order placed: ID " + order.getId();
        notificationService.saveNotification("ORDER_PLACED", message);
        messagingTemplate.convertAndSend("/topic/orders", message);

        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrderById(id);
    }

    @PutMapping("/{id}")
    public _Order updateOrder(@PathVariable Long id, @RequestBody _Order order) {
        return orderService.updateOrder(id, order);
    }

    @GetMapping("/vendor/{vendorId}")
    public List<_Order> getOrdersByVendor(@PathVariable Long vendorId) {
        return orderService.getOrdersByVendor(vendorId);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<_Order> updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus status) {
        _Order order = orderService.updateStatus(orderId, status);

        String message = "Order updated: ID " + order.getId() + ", Status: " + status;
        notificationService.saveNotification("ORDER_UPDATED", message);
        messagingTemplate.convertAndSend("/topic/orders", message);

        return ResponseEntity.ok(order);
    }

    @GetMapping("/user/{userId}")
    public List<_Order> getOrdersByUser(@PathVariable Long userId) {
        return orderRepo.findByUserId(userId);
    }

}

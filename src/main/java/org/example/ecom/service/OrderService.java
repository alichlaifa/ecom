package org.example.ecom.service;

import lombok.AllArgsConstructor;
import org.example.ecom.dto.OrderRequest;
import org.example.ecom.model.*;
import org.example.ecom.repository.OrderRepo;
import org.example.ecom.repository.ProductRepo;
import org.example.ecom.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepo orderRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepository;

    public List<_Order> findAll() {
        return orderRepo.findAll();
    }

    public Optional<_Order> findById(Long id) {
        return orderRepo.findById(id);
    }

    public _Order saveOrder(OrderRequest orderRequest, String transactionId) {
        _User user = userRepo.findById(orderRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        _Order order = new _Order();

        if (transactionId != null) {
            order.setTransactionId(transactionId);
        } else {
            order.setTransactionId(UUID.randomUUID().toString());
        }

        order.setCity(orderRequest.getCity());
        order.setState(orderRequest.getState());
        order.setCountry(orderRequest.getCountry());
        order.setOrderDate(new Date());
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = orderRequest.getOrderItems().stream().map(itemReq -> {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + itemReq.getProductId()));
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setOrder(order);
            return orderItem;
        }).toList();

        order.setOrderItems(orderItems);

        return orderRepo.save(order);
    }

    public void deleteOrderById(Long id) {
        orderRepo.deleteById(id);
    }

    public _Order updateOrder(Long id, _Order order) {
        _Order existingOrder = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        existingOrder.setOrderDate(order.getOrderDate());
        existingOrder.setCity(order.getCity());
        existingOrder.setCountry(order.getCountry());
        existingOrder.setState(order.getState());
        existingOrder.setUser(order.getUser());

        existingOrder.getOrderItems().clear();

        List<OrderItem> newOrderItems = order.getOrderItems().stream()
                .map(orderItem -> {
                    Product fullProduct = productRepository.findById(orderItem.getProduct().getId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Product not found with ID: " + orderItem.getProduct().getId()));

                    OrderItem processedItem = new OrderItem();
                    processedItem.setProduct(fullProduct);
                    processedItem.setQuantity(orderItem.getQuantity());
                    processedItem.setOrder(existingOrder);

                    return processedItem;
                })
                .toList();

        existingOrder.getOrderItems().addAll(newOrderItems);
        return orderRepo.save(existingOrder);
    }

    public List<_Order> getOrdersByVendor(Long vendorId) {
        return orderRepo.findDistinctByOrderItems_Product_Vendor_Id(vendorId);
    }

    public _Order updateStatus(Long orderId, OrderStatus status) {
        _Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepo.save(order);
    }
}

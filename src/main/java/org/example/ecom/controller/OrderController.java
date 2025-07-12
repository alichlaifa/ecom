package org.example.ecom.controller;

import lombok.AllArgsConstructor;
import org.example.ecom.model._Order;
import org.example.ecom.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public List<_Order> getOrders() {
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<_Order> getOrder(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @PostMapping
    public _Order saveOrder(@RequestBody _Order order) {
        return orderService.saveOrder(order);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrderById(id);
    }

    @PutMapping("/{id}")
    public _Order updateOrder(@PathVariable Long id, @RequestBody _Order order) {
        return orderService.updateOrder(id, order);
    }
}

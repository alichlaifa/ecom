package org.example.ecom.service;

import lombok.AllArgsConstructor;
import org.example.ecom.model.Client;
import org.example.ecom.model.Product;
import org.example.ecom.model._Order;
import org.example.ecom.repository.ClientRepo;
import org.example.ecom.repository.OrderRepo;
import org.example.ecom.repository.ProductRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepo orderRepo;
    private final ClientRepo clientRepository;
    private final ProductRepo productRepository;

    public List<_Order> findAll() {
        return orderRepo.findAll();
    }

    public Optional<_Order> findById(Long id) {
        return orderRepo.findById(id);
    }

    public _Order saveOrder(_Order order) {
        Client client = clientRepository.findById(order.getClient().getId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        List<Product> fullProducts = order.getProducts().stream()
                .map(p -> productRepository.findById(p.getId())
                        .orElseThrow(() -> new RuntimeException("Product not found with ID: " + p.getId())))
                .toList();

        order.setClient(client);
        order.setProducts(fullProducts);

        return orderRepo.save(order);
    }

    public void deleteOrderById(Long id) {
        orderRepo.deleteById(id);
    }

    public _Order updateOrder(Long id, _Order order) {
        _Order existingOrder = orderRepo.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        existingOrder.setOrderDate(order.getOrderDate());
        existingOrder.setCity(order.getCity());
        existingOrder.setProducts(order.getProducts());
        existingOrder.setClient(order.getClient());
        existingOrder.setCountry(order.getCountry());
        existingOrder.setState(order.getState());
        return orderRepo.save(existingOrder);
    }
}

package org.example.ecom.repository;

import org.example.ecom.model._Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<_Order, Long> {
}

package org.example.ecom.repository;

import org.example.ecom.model._Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepo extends JpaRepository<_Order, Long> {
    List<_Order> findByUserId(Long userId);
    List<_Order> findDistinctByOrderItems_Product_Vendor_Id(Long vendorId);
    boolean existsByUserId(Long userId);
    Optional<_Order> findByTransactionId(String transactionId);

}

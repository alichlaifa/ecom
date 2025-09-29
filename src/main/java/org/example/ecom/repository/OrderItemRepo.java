package org.example.ecom.repository;

import org.example.ecom.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.product.id = :productId")
    long countByProductId(@Param("productId") Long productId);

    @Query("SELECT DISTINCT oi2.product.id " +
            "FROM OrderItem oi1 " +
            "JOIN oi1.order o " +
            "JOIN o.orderItems oi2 " +
            "WHERE oi1.product.id = :productId " +
            "AND oi2.product.id <> :productId " +
            "AND o.user.id <> :excludeUserId")
    List<Long> findCoPurchasedProducts(@Param("productId") Long productId,
                                       @Param("excludeUserId") Long excludeUserId);
}


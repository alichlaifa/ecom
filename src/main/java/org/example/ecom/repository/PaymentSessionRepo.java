package org.example.ecom.repository;

import org.example.ecom.model.PaymentSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentSessionRepo extends JpaRepository<PaymentSession, Long> {
    Optional<PaymentSession> findByStripeSessionId(String sessionId);
}


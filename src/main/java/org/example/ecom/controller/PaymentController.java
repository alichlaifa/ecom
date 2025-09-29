package org.example.ecom.controller;

import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.example.ecom.dto.OrderRequest;
import org.example.ecom.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, Object>> createCheckoutSession(
            @RequestBody OrderRequest orderRequest,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey
    ) throws StripeException {
        return ResponseEntity.ok(paymentService.createCheckoutSession(orderRequest, idempotencyKey));
    }

    @PostMapping("/confirm/{orderId}")
    public ResponseEntity<String> confirmPayment(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.confirmPayment(orderId));
    }
}

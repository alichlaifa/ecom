package org.example.ecom.controller;

import lombok.AllArgsConstructor;
import org.example.ecom.dto.ReviewRequest;
import org.example.ecom.model.Review;
import org.example.ecom.service.NotificationService;
import org.example.ecom.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody ReviewRequest request) {
        try {
            Review review = reviewService.addReview(request);

            String message = String.format(
                    "User %s added a review for product '%s': %d⭐ - \"%s\"",
                    review.getUser().getUsername(),
                    review.getProduct().getName(),
                    review.getRating(),
                    review.getComment()
            );

            notificationService.saveNotification("REVIEW_ADDED", message);
            messagingTemplate.convertAndSend("/topic/reviews", message);

            return ResponseEntity.ok("Review added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/product/{productId}")
    public List<Review> getReviewsByProduct(@PathVariable Long productId) {
        return reviewService.getReviewsByProduct(productId);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        Review review = reviewService.getReviewById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        String message = String.format(
                "User %s deleted a review for product '%s': %d⭐ - \"%s\"",
                review.getUser().getUsername(),
                review.getProduct().getName(),
                review.getRating(),
                review.getComment()
        );

        notificationService.saveNotification("REVIEW_DELETED", message);
        messagingTemplate.convertAndSend("/topic/reviews", message);

        reviewService.deleteReview(reviewId);

        return ResponseEntity.ok("Review deleted successfully");
    }
}

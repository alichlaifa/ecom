package org.example.ecom.service;

import lombok.AllArgsConstructor;
import org.example.ecom.dto.ReviewRequest;
import org.example.ecom.model.Product;
import org.example.ecom.model.Review;
import org.example.ecom.model._User;
import org.example.ecom.repository.ProductRepo;
import org.example.ecom.repository.ReviewRepo;
import org.example.ecom.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepo reviewRepo;
    private final ProductRepo productRepo;
    private final UserRepo userRepo;

    public List<Review> getAllReviews() {
        return reviewRepo.findAll();
    }

    public Optional<Review> getReviewById(Long reviewId) {
        return reviewRepo.findById(reviewId);
    }


    public Review addReview(ReviewRequest request) {
        Review review = new Review();
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setProduct(productRepo.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found")));
        review.setUser(userRepo.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found")));

        return reviewRepo.save(review);
    }


    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepo.findByProductId(productId);
    }

    public void deleteReview(Long reviewId) {
        if (!reviewRepo.existsById(reviewId)) {
            throw new RuntimeException("Review not found");
        }
        reviewRepo.deleteById(reviewId);
    }
}

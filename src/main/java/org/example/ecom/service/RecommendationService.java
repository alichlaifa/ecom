package org.example.ecom.service;

import lombok.AllArgsConstructor;
import org.example.ecom.model.Product;
import org.example.ecom.repository.FavoriteRepo;
import org.example.ecom.repository.OrderItemRepo;
import org.example.ecom.repository.OrderRepo;
import org.example.ecom.repository.ProductRepo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class RecommendationService {

    private final FavoriteRepo favoriteRepo;
    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final ProductRepo productRepo;
    private final RedisTemplate<String, Object> redisTemplate;

    // -------------------------------
    // Popularity-based Recommendations
    // -------------------------------
    public void updatePopularityScores() {
        List<Product> products = productRepo.findAll();

        for (Product product : products) {
            long favorites = favoriteRepo.countByProductId(product.getId());
            long orders = orderItemRepo.countByProductId(product.getId());

            double score = favorites * 2 + orders; // Weighted formula
            redisTemplate.opsForZSet().add("product:popularity", product.getId(), score);
        }
    }

    public List<Product> getPopularProducts(int limit) {
        Set<Object> ids = redisTemplate.opsForZSet()
                .reverseRange("product:popularity", 0, limit - 1);

        if (ids == null || ids.isEmpty()) {
            return productRepo.findAll(PageRequest.of(0, limit)).toList();
        }

        return ids.stream()
                .map(id -> productRepo.findById(Long.valueOf(id.toString())).orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }


    // -------------------------------
    // Collaborative Filtering (simple version)
    // -------------------------------
    public List<Product> getCollaborativeRecommendations(Long userId, int limit) {
        // Get products this user already ordered
        List<Long> userProductIds = orderRepo.findByUserId(userId).stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(item -> item.getProduct().getId())
                .distinct()
                .toList();

        if (userProductIds.isEmpty()) {
            // Fall back to popularity
            return getPopularProducts(limit);
        }

        // For each product user bought → find other products bought together
        Map<Long, Integer> recommendationScores = new HashMap<>();

        for (Long productId : userProductIds) {
            List<Long> coPurchased = orderItemRepo.findCoPurchasedProducts(productId, userId);
            for (Long coId : coPurchased) {
                recommendationScores.put(coId, recommendationScores.getOrDefault(coId, 0) + 1);
            }
        }

        // Sort by score and return top N
        return recommendationScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> productRepo.findById(entry.getKey()).orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    // -------------------------------
    // Hybrid System
    // -------------------------------
    public List<Product> getRecommendations(Long userId, int limit) {
        boolean hasOrders = orderRepo.existsByUserId(userId);
        boolean hasFavorites = favoriteRepo.existsByUserId(userId);

        if (!hasOrders && !hasFavorites) {
            return getPopularProducts(limit); // new user
        }

        return getCollaborativeRecommendations(userId, limit); // old user
    }
}


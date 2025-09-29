package org.example.ecom.controller;

import org.example.ecom.model.Product;
import org.example.ecom.service.RecommendationService;
import org.springframework.web.bind.annotation.PostMapping;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/recommended")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/popular")
    public List<Product> getPopularProducts(@RequestParam(defaultValue = "10") int limit) {
        return recommendationService.getPopularProducts(limit);
    }

    @GetMapping("/user/{userId}")
    public List<Product> getUserRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        return recommendationService.getRecommendations(userId, limit);
    }

    @PostMapping("/update-popularity")
    public String updatePopularityScores() {
        recommendationService.updatePopularityScores();
        return "Popularity scores updated successfully";
    }
}


package org.example.ecom.controller;

import lombok.AllArgsConstructor;
import org.example.ecom.model.Favorite;
import org.example.ecom.model.Product;
import org.example.ecom.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/favorite")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping
    public List<Favorite> getFavorite() {
        return favoriteService.getFavorite();
    }

    @DeleteMapping("/{id}")
    public void deleteFavoriteById(@PathVariable Long id) {
        favoriteService.deleteFavoriteById(id);
    }

    @PostMapping
    public Favorite addFavorite(@ModelAttribute Favorite Favorite, @RequestParam Long productId, @RequestParam Long userId) {
        return favoriteService.addFavorite(Favorite, productId, userId);
    }

    @GetMapping("/{userId}")
    public List<Product> getProductsFavoriteByUserId(@PathVariable Long userId) {
        return favoriteService.getProductsFavoriteByUserId(userId);
    }

    @PostMapping("/toggle")
    public ResponseEntity<?> toggleFavorite(@RequestParam Long productId, @RequestParam Long userId) {
        Favorite result = favoriteService.toggleFavorite(productId, userId);
        if (result == null) {
            return ResponseEntity.ok("Favorite removed");
        } else {
            return ResponseEntity.ok(result);
        }
    }

    @GetMapping("/count/{userId}")
    public long getFavoritesCountByUserId(@PathVariable Long userId) {
        return favoriteService.getFavoritesCountByUserId(userId);
    }
}

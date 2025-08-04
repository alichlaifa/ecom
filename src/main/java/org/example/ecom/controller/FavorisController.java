package org.example.ecom.controller;

import lombok.AllArgsConstructor;
import org.example.ecom.model.Favoris;
import org.example.ecom.model.Product;
import org.example.ecom.service.FavorisService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/favoris")
public class FavorisController {
    private final FavorisService favorisService;

    @GetMapping
    public List<Favoris> getFavoris() {
        return favorisService.getFavoris();
    }

    @DeleteMapping("/{id}")
    public void deleteFavorisById(@PathVariable Long id) {
        favorisService.deleteFavorisById(id);
    }

    @PostMapping
    public Favoris addFavoris(@ModelAttribute Favoris favoris, @RequestParam Long productId, @RequestParam Long clientId) {
        return favorisService.addFavoris(favoris, productId, clientId);
    }

    @GetMapping("/{clientId}")
    public List<Product> getProductsFavorisByClientId(@PathVariable Long clientId) {
        return favorisService.getProductsFavorisByClientId(clientId);
    }
}

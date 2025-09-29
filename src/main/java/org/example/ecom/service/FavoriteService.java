package org.example.ecom.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.ecom.model.Favorite;
import org.example.ecom.model.Product;
import org.example.ecom.model._User;
import org.example.ecom.repository.FavoriteRepo;
import org.example.ecom.repository.ProductRepo;
import org.example.ecom.repository.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FavoriteService {
    private final UserRepo userRepo;
    private final ProductRepo productRepo;
    private final FavoriteRepo favoriteRepo;

    public List<Favorite> getFavorite(){
         return favoriteRepo.findAll();
     }

     public void deleteFavoriteById(Long id){
         favoriteRepo.deleteById(id);
     }

     public Favorite addFavorite(Favorite favorite, Long productId, Long userId){
         Product product = productRepo.findById(productId).orElseThrow(()->new EntityNotFoundException("product not found"));
         _User user = userRepo.findById(userId).orElseThrow(()->new EntityNotFoundException("user not found"));
         favorite.setUser(user);
         favorite.setProduct(product);
         return favoriteRepo.save(favorite);
     }

     public List<Product> getProductsFavoriteByUserId(Long userId) {
         return favoriteRepo.findFavoriteByUserId(userId)
                 .stream()
                 .map(Favorite::getProduct)
                 .collect(Collectors.toList());
     }

    public Favorite toggleFavorite(Long productId, Long userId) {
        Favorite existingFavorite = favoriteRepo.findByUserIdAndProductId(userId, productId);

        if (existingFavorite != null) {
            favoriteRepo.delete(existingFavorite);
            return null;
        } else {
            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));
            _User user = userRepo.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setProduct(product);
            return favoriteRepo.save(favorite);
        }
    }

    public long getFavoritesCountByUserId(Long userId) {
        return favoriteRepo.countByUserId(userId);
    }


}

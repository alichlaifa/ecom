package org.example.ecom.repository;

import org.example.ecom.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavoriteRepo extends JpaRepository<Favorite,Long> {
    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId")
    List<Favorite> findFavoriteByUserId(@Param("userId") Long userId);
    Favorite findByUserIdAndProductId(Long userId, Long productId);

    long countByUserId(Long id);

    long countByProductId(Long productId);
    boolean existsByUserId(Long userId);
}

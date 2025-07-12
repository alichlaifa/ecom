package org.example.ecom.repository;

import org.example.ecom.model.Favoris;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavorisRepo extends JpaRepository<Favoris,Long> {
    @Query("SELECT f FROM Favoris f WHERE f.client.id = :clientId")
    List<Favoris> findFavorisByClientId(@Param("clientId") Long clientId);
}

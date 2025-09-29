package org.example.ecom.repository;

import org.example.ecom.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VendorRepo extends JpaRepository<Vendor, Long> {
    @Query("SELECT v FROM Vendor v WHERE v.username = :username")
    Optional<Vendor> findByUsername(@Param("username") String username);
}

package org.example.ecom.repository;

import org.example.ecom.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepo extends JpaRepository<Client, Long> {

}

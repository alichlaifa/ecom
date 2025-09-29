package org.example.ecom.repository;

import org.example.ecom.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {
    List<Notification> findTop20ByOrderByCreatedAtDesc();
}

package org.example.iwa_ms_notification.repositories;

import org.example.iwa_ms_notification.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Ajoutez des méthodes personnalisées si nécessaire
}
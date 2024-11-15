package org.example.iwa_ms_notification.services;

import org.example.iwa_ms_notification.models.Notification;
import org.example.iwa_ms_notification.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RestTemplate restTemplate;

    // Méthode pour créer une notification
    public Notification createNotification(Notification notification) {
        // Vérifier si l'utilisateur existe via ms_user
        String userServiceUrl = "http://host.docker.internal:8080/users/" + notification.getUserId();
        ResponseEntity<Object> response = restTemplate.getForEntity(userServiceUrl, Object.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            // L'utilisateur existe, on peut sauvegarder la notification
            return notificationRepository.save(notification);
        } else {
            throw new RuntimeException("Utilisateur non trouvé pour l'ID : " + notification.getUserId());
        }
    }

    // Autres méthodes du service (par exemple, pour marquer une notification comme lue)
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée pour l'ID : " + notificationId));

        notification.setRead(true);
        return notificationRepository.save(notification);
    }
}

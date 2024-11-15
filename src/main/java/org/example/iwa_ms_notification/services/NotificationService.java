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

    public Notification createFavoriteNotification(Long userId, Long lieuId) {
        try {
            // Vérifier l'existence de l'utilisateur dans ms_user
            String userServiceUrl = "http://host.docker.internal:8080/users/" + userId;
            ResponseEntity<Object> userResponse = restTemplate.getForEntity(userServiceUrl, Object.class);

            if (!userResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Utilisateur non trouvé pour l'ID : " + userId);
            }

            // Vérifier l'existence du lieu dans ms_lieu
            String lieuServiceUrl = "http://host.docker.internal:8083/locations/" + lieuId;
            ResponseEntity<Object> lieuResponse = restTemplate.getForEntity(lieuServiceUrl, Object.class);

            if (!lieuResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Lieu non trouvé pour l'ID : " + lieuId);
            }

            // Créer et enregistrer la notification
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setType("ajout aux favoris");
            notification.setMessage("Un utilisateur a ajouté votre lieu à ses favoris");
            notification.setIsRead(false);
            notification.setRelatedEntityId(lieuId);
            notification.setEntityType("lieu");

            return notificationRepository.save(notification);
        } catch (Exception e) {
            // Log l'exception et retournez une réponse appropriée ou relancez l'exception
            System.err.println("Erreur lors de la création de la notification : " + e.getMessage());
            throw new RuntimeException("Erreur lors de la création de la notification", e);
        }
    }

    public Iterable<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
    public Notification createSupportResponseNotification(Long userId, Long questionId) {
        // Vérifier l'existence de l'utilisateur dans ms_user
        String userServiceUrl = "http://host.docker.internal:8080/users/" + userId;
        ResponseEntity<Object> userResponse = restTemplate.getForEntity(userServiceUrl, Object.class);

        if (!userResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Utilisateur non trouvé pour l'ID : " + userId);
        }

        // Vérifier l'existence de la question de support dans ms_support
        String supportServiceUrl = "http://host.docker.internal:8087/support/questions/" + questionId;
        ResponseEntity<Object> questionResponse = restTemplate.getForEntity(supportServiceUrl, Object.class);

        if (!questionResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Question de support non trouvée pour l'ID : " + questionId);
        }

        // Créer et enregistrer la notification
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType("question répondue");
        notification.setMessage("Une réponse a été donnée à votre question dans le support");
        notification.setIsRead(false);
        notification.setRelatedEntityId(questionId);
        notification.setEntityType("support");

        return notificationRepository.save(notification);
    }

}

package org.example.iwa_ms_notification.services;

import org.example.iwa_ms_notification.models.Notification;
import org.example.iwa_ms_notification.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RestTemplate restTemplate;


    public Notification createFavoriteNotification(Long locationId) {
        if (locationId == null) {
            throw new IllegalArgumentException("Lieu ID ne peut pas être nul");
        }

        try {
            String lieuServiceUrl = "http://host.docker.internal:8083/locations/" + locationId;
            ResponseEntity<Map> lieuResponse = restTemplate.getForEntity(lieuServiceUrl, Map.class);

            if (!lieuResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Lieu non trouvé pour l'ID : " + locationId);
            }

            // Récupérer userId depuis la réponse en utilisant Map
            Map<String, Object> responseBody = lieuResponse.getBody();
            System.out.println(responseBody);
            Long userId = ((Number) responseBody.get("userId")).longValue();

            // Créer et enregistrer la notification
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setType("ajout aux favoris");
            notification.setMessage("Un utilisateur a ajouté votre lieu à ses favoris");
            notification.setIsRead(false);
            notification.setRelatedEntityId(locationId);
            notification.setEntityType("lieu");

            return notificationRepository.save(notification);
        } catch (Exception e) {
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

    public Notification createCommentNotification(Long locationId) {
        if (locationId == null) {
            throw new IllegalArgumentException("Lieu ID ne peut pas être nul");
        }

        try {
            String lieuServiceUrl = "http://host.docker.internal:8083/locations/" + locationId;
            ResponseEntity<Map> lieuResponse = restTemplate.getForEntity(lieuServiceUrl, Map.class);

            if (!lieuResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Lieu non trouvé pour l'ID : " + locationId);
            }

            // Récupérer userId depuis la réponse en utilisant Map
            Map<String, Object> responseBody = lieuResponse.getBody();
            System.out.println(responseBody);
            Long userId = ((Number) responseBody.get("userId")).longValue();


            // Créer et enregistrer la notification
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setType("Nouveau commentaire");
            notification.setMessage("Un utilisateur a commenté votre lieu.");
            notification.setIsRead(false);
            notification.setRelatedEntityId(locationId);
            notification.setEntityType("commentaire");

            return notificationRepository.save(notification);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création de la notification pour un commentaire", e);
        }
    }

    public Notification createFlagNotification(Long locationId, Long commentId, String reason) {
        if (locationId == null && commentId == null) {
            throw new IllegalArgumentException("Location ID et Comment ID ne peuvent pas être tous les deux nuls");
        }

        try {
            Long userId = null;

            // Récupérer l'utilisateur propriétaire
            if (locationId != null) {
                String lieuServiceUrl = "http://host.docker.internal:8083/locations/" + locationId;
                ResponseEntity<Map> lieuResponse = restTemplate.getForEntity(lieuServiceUrl, Map.class);
                if (!lieuResponse.getStatusCode().is2xxSuccessful()) {
                    throw new RuntimeException("Lieu non trouvé pour l'ID : " + locationId);
                }
                Map<String, Object> responseBody = lieuResponse.getBody();
                userId = ((Number) responseBody.get("userId")).longValue();
            } else if (commentId != null) {
                String commentServiceUrl = "http://host.docker.internal:8081/comments/" + commentId;
                ResponseEntity<Map> commentResponse = restTemplate.getForEntity(commentServiceUrl, Map.class);
                if (!commentResponse.getStatusCode().is2xxSuccessful()) {
                    throw new RuntimeException("Commentaire non trouvé pour l'ID : " + commentId);
                }
                Map<String, Object> responseBody = commentResponse.getBody();
                userId = ((Number) responseBody.get("userId")).longValue();
            }

            if (userId == null) {
                throw new RuntimeException("Impossible de déterminer l'utilisateur propriétaire");
            }

            // Créer et enregistrer la notification
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setType("Signalement");
            notification.setMessage("Votre " + (locationId != null ? "lieu" : "commentaire") + " a été signalé : " + reason);
            notification.setIsRead(false);
            notification.setRelatedEntityId(locationId != null ? locationId : commentId);
            notification.setEntityType(locationId != null ? "lieu" : "commentaire");

            return notificationRepository.save(notification);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création de la notification pour un signalement", e);
        }
    }

    public Notification createDeletedNotification(Long locationId, Long commentId) {
        if (locationId == null && commentId == null) {
            throw new IllegalArgumentException("Location ID et Comment ID ne peuvent pas être tous les deux nuls");
        }

        try {
            Long userId = null;

            // Récupérer l'utilisateur propriétaire
            if (locationId != null) {
                String lieuServiceUrl = "http://host.docker.internal:8083/locations/" + locationId;
                ResponseEntity<Map> lieuResponse = restTemplate.getForEntity(lieuServiceUrl, Map.class);
                if (!lieuResponse.getStatusCode().is2xxSuccessful()) {
                    throw new RuntimeException("Lieu non trouvé pour l'ID : " + locationId);
                }
                Map<String, Object> responseBody = lieuResponse.getBody();
                userId = ((Number) responseBody.get("userId")).longValue();
            } else if (commentId != null) {
                String commentServiceUrl = "http://host.docker.internal:8081/comments/" + commentId;
                ResponseEntity<Map> commentResponse = restTemplate.getForEntity(commentServiceUrl, Map.class);
                if (!commentResponse.getStatusCode().is2xxSuccessful()) {
                    throw new RuntimeException("Commentaire non trouvé pour l'ID : " + commentId);
                }
                Map<String, Object> responseBody = commentResponse.getBody();
                userId = ((Number) responseBody.get("userId")).longValue();
            }

            if (userId == null) {
                throw new RuntimeException("Impossible de déterminer l'utilisateur propriétaire");
            }

            // Créer et enregistrer la notification
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setType("Suppression");
            notification.setMessage("Votre " + (locationId != null ? "lieu" : "commentaire") + " a été supprimé par un administrateur.");
            notification.setIsRead(false);
            notification.setRelatedEntityId(locationId != null ? locationId : commentId);
            notification.setEntityType(locationId != null ? "lieu" : "commentaire");

            return notificationRepository.save(notification);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création de la notification pour une suppression", e);
        }
    }

}

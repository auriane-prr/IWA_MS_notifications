package org.example.iwa_ms_notification.controllers;

import org.example.iwa_ms_notification.models.Notification;
import org.example.iwa_ms_notification.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @GetMapping("/")
    public ResponseEntity<String> healthCheck() {
        logger.info("Health check endpoint hit");
        return ResponseEntity.ok("Notification service is up and running!");
    }

    @PostMapping("/create/favorite")
    public ResponseEntity<Notification> createFavoriteNotification(@RequestBody Map<String, Long> requestBody) {
        logger.info("POST /notifications/create/favorite hit with data: {}", requestBody);
        Long userId = requestBody.get("userId");
        Long lieuId = requestBody.get("lieuId");

        Notification notification = notificationService.createFavoriteNotification(userId, lieuId);
        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }

    @PostMapping("/create/support-response")
    public ResponseEntity<Notification> createSupportResponseNotification(
            @RequestBody Map<String, Long> requestBody) {

        Long userId = requestBody.get("userId");
        Long questionId = requestBody.get("questionId");

        Notification notification = notificationService.createSupportResponseNotification(userId, questionId);
        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }

}

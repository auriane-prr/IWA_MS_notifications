package org.example.iwa_ms_notification;


import org.example.iwa_ms_notification.controllers.NotificationController;
import org.example.iwa_ms_notification.models.Notification;
import org.example.iwa_ms_notification.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testNotification = new Notification();
        testNotification.setNotificationId(1L);
        testNotification.setUserId(101L);
        testNotification.setType("ajout aux favoris");
        testNotification.setMessage("Un utilisateur a ajouté votre lieu à ses favoris");
        testNotification.setIsRead(false);
        testNotification.setRelatedEntityId(202L);
        testNotification.setEntityType("lieu");
    }

    @Test
    void testGetAllNotifications() throws Exception {
        when(notificationService.getAllNotifications()).thenReturn(Arrays.asList(testNotification));

        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].notificationId").value(1))
                .andExpect(jsonPath("$[0].userId").value(101))
                .andExpect(jsonPath("$[0].type").value("ajout aux favoris"));
    }

    @Test
    void testCreateFavoriteNotification() throws Exception {
        when(notificationService.createFavoriteNotification(anyLong())).thenReturn(testNotification);

        String requestBody = """
            {
                "locationId": 202
            }
            """;

        mockMvc.perform(post("/notifications/create/favorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notificationId").value(1))
                .andExpect(jsonPath("$.type").value("ajout aux favoris"))
                .andExpect(jsonPath("$.userId").value(101));
    }

    @Test
    void testMarkNotificationAsRead() throws Exception {
        testNotification.setIsRead(true);
        when(notificationService.markNotificationAsRead(anyLong())).thenReturn(testNotification);

        mockMvc.perform(put("/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isRead").value(true));
    }

    @Test
    void testGetUnreadNotifications() throws Exception {
        List<Notification> unreadNotifications = List.of(testNotification);
        when(notificationService.getUnreadNotifications()).thenReturn(unreadNotifications);

        mockMvc.perform(get("/notifications/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].notificationId").value(1));
    }
}

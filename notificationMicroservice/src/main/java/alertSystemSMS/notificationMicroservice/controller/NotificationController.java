package alertSystemSMS.notificationMicroservice.controller;

import alertSystemSMS.notificationMicroservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/notifications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createNotification(@RequestBody Map<String, Object> payload) {
        Long alertId = Long.valueOf(payload.get("alertId").toString());
        String message = (String) payload.get("message");
        notificationService.createAndSendNotification(alertId, message);
        return ResponseEntity.ok("Notification sent successfully.");
    }

    /**
     * GET endpoint to retrieve all alert types and the user's subscription status for each.
     */
    @GetMapping("/users/{userId}/preferences")
    public ResponseEntity<List<Map<String, Object>>> getAllUserPreferences(@PathVariable Long userId) {
        List<Map<String, Object>> preferences = notificationService.getAllUserPreferences(userId);
        return ResponseEntity.ok(preferences);
    }

    /**
     * GET endpoint to check a user's subscription status for a single, specific alert.
     */
    @GetMapping("/users/{userId}/preferences/{alertId}")
    public ResponseEntity<Map<String, Boolean>> getUserPreference(
            @PathVariable Long userId,
            @PathVariable Long alertId) {

        boolean isEnabled = notificationService.getUserPreference(userId, alertId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isEnabled", isEnabled);

        return ResponseEntity.ok(response);
    }

    /**
     * POST endpoint to update a user's subscription preference for a single alert.
     */
    @PostMapping("/users/{userId}/preferences")
    public ResponseEntity<String> setUserPreference(
        @PathVariable Long userId,
        @RequestBody Map<String, Object> payload) {
        Long alertId = Long.valueOf(payload.get("alertId").toString());
        Boolean isEnabled = (Boolean) payload.get("isEnabled");
        notificationService.updateUserPreference(userId, alertId, isEnabled);
        return ResponseEntity.ok("User preferences updated.");
    }

    @PostMapping("/notifications/status")
    public ResponseEntity<Void> handleSmsStatusUpdate(
            @RequestParam("MessageSid") String messageSid,
            @RequestParam("MessageStatus") String status) {
        notificationService.logSmsDeliveryStatus(messageSid, status);
        return ResponseEntity.ok().build();
    }

    /**
     * API for other microservices to send notification to a specific user
     * Example: Payment microservice sending receipt notification
     */
    @PostMapping("/send/user/{userId}")
    public ResponseEntity<String> sendNotificationToUser(
            @PathVariable Long userId,
            @RequestBody Map<String, String> payload) {
        String content = payload.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Content is required");
        }
        
        boolean success = notificationService.sendNotificationToUser(userId, content);
        if (success) {
            return ResponseEntity.ok("Notification sent successfully to user " + userId);
        } else {
            return ResponseEntity.badRequest().body("Failed to send notification - user not found or no phone number");
        }
    }

    /**
     * API for other microservices to send notification to all users
     * Example: System maintenance announcement
     */
    @PostMapping("/send/all")
    public ResponseEntity<String> sendNotificationToAllUsers(@RequestBody Map<String, String> payload) {
        String content = payload.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Content is required");
        }
        
        int sentCount = notificationService.sendNotificationToAllUsers(content);
        return ResponseEntity.ok("Notification sent to " + sentCount + " users");
    }
}


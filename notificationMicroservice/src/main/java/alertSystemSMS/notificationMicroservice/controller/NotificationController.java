package alertSystemSMS.notificationMicroservice.controller;

import alertSystemSMS.notificationMicroservice.model.Notification;
import alertSystemSMS.notificationMicroservice.model.AlertType;
import alertSystemSMS.notificationMicroservice.repository.AlertTypeRepository;
import alertSystemSMS.notificationMicroservice.repository.NotificationRepository;
import alertSystemSMS.notificationMicroservice.service.NotificationService;
import alertSystemSMS.notificationMicroservice.service.SmsRoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/v1")
public class NotificationController {

    @Autowired
    private SmsRoutingService smsRoutingService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AlertTypeRepository alertTypeRepository;

    @PostMapping("/notifications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createNotification(@RequestBody Map<String, Object> payload, Principal principal) {
        Long alertId = Long.valueOf(payload.get("alertId").toString());
        String message = (String) payload.get("message");
        notificationService.createAndSendNotification(alertId, message, principal);
        return ResponseEntity.ok("Notification sent successfully.");
    }

    @GetMapping("/users/{userId}/preferences")
    public ResponseEntity<List<Map<String, Object>>> getAllUserPreferences(@PathVariable Long userId) {
        List<Map<String, Object>> preferences = notificationService.getAllUserPreferences(userId);
        return ResponseEntity.ok(preferences);
    }

    @GetMapping("/users/{userId}/preferences/{alertId}")
    public ResponseEntity<Map<String, Boolean>> getUserPreference(
            @PathVariable Long userId,
            @PathVariable Long alertId) {

        boolean isEnabled = notificationService.getUserPreference(userId, alertId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isEnabled", isEnabled);

        return ResponseEntity.ok(response);
    }

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

    @PostMapping("/send/all")
    public ResponseEntity<String> sendNotificationToAllUsers(@RequestBody Map<String, String> payload) {
        String content = payload.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Content is required");
        }
        
        int sentCount = notificationService.sendNotificationToAllUsers(content);
        return ResponseEntity.ok("Notification sent to " + sentCount + " users");
    }

    /**
     * API for sending a direct message to a phone number.
     */
    @PostMapping("/send/direct")
    public ResponseEntity<String> sendDirectNotification(@RequestBody Map<String, String> payload, Principal principal) {
        String phoneNumber = payload.get("phoneNumber");
        String message = payload.get("message");
        if (phoneNumber == null || phoneNumber.trim().isEmpty() || message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Phone number and message are required");
        }
        String sentBy = principal != null ? principal.getName() : "API";

        // Find the specific AlertType for 'Direct Message'
        AlertType directMessageAlertType = alertTypeRepository.findByAlertName("Direct Message")
                .orElseThrow(() -> new RuntimeException("AlertType 'Direct Message' not found in database."));

        // Create and save a notification record first for logging purposes
        Notification notification = new Notification();
        notification.setAlertType(directMessageAlertType);
        notification.setMessageContent(message);
        notification.setSentBy(sentBy);
        Notification savedNotification = notificationRepository.save(notification);

        smsRoutingService.sendSms(phoneNumber, message, savedNotification);
        return ResponseEntity.ok("Notification sent successfully to " + phoneNumber);
    }
}

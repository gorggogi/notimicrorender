package alertSystemSMS.notificationMicroservice.controller;

import alertSystemSMS.notificationMicroservice.model.Notification;
import alertSystemSMS.notificationMicroservice.service.NotificationService;
import alertSystemSMS.notificationMicroservice.service.SmsRoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/v1")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private SmsRoutingService smsRoutingService;

    @Autowired
    private NotificationService notificationService;




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

        logger.info("[CONTROLLER] Attempting to save notification in new transaction for message: {}", message);
        // First, create the notification in a new, separate transaction to get a committed ID.
        Notification savedNotification = notificationService.createAndLogDirectMessage(message, sentBy);

        if (savedNotification != null && savedNotification.getNotificationId() != null) {
            logger.info("[CONTROLLER] Returned from service. Notification has been committed with ID: {}", savedNotification.getNotificationId());
        } else {
            logger.error("[CONTROLLER] ERROR: Returned from service, but notification or its ID is null.");
            return ResponseEntity.internalServerError().body("Failed to save notification before sending.");
        }

        // Now, with a committed notification, proceed to send the SMS.
        logger.info("[CONTROLLER] Proceeding to send SMS for notification ID: {}", savedNotification.getNotificationId());
        smsRoutingService.sendSms(phoneNumber, message, savedNotification);
        return ResponseEntity.ok("Notification sent successfully to " + phoneNumber);
    }
}

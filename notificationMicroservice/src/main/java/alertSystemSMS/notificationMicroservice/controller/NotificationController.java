package alertSystemSMS.notificationMicroservice.controller;

import alertSystemSMS.notificationMicroservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
}


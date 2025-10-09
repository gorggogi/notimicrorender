package alertSystemSMS.notificationMicroservice.controller;

import alertSystemSMS.notificationMicroservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Test endpoint to send notification to specific user
     * Direct service call (simulates what the API endpoints do)
     */
    @GetMapping("/send-to-user/{userId}")
    public ResponseEntity<String> testSendToUser(@PathVariable Long userId) {
        try {
            String content = "TEST: Payment receipt for user " + userId + " - Amount: $99.99";
            boolean success = notificationService.sendNotificationToUser(userId, content);
            
            if (success) {
                return ResponseEntity.ok("SUCCESS: Notification sent to user " + userId);
            } else {
                return ResponseEntity.badRequest().body("FAILED: User not found or no phone number");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("ERROR: " + e.getMessage());
        }
    }

    /**
     * Test endpoint to send notification to all users
     */
    @GetMapping("/send-to-all")
    public ResponseEntity<String> testSendToAll() {
        try {
            String content = "TEST: System announcement - New features available!";
            int sentCount = notificationService.sendNotificationToAllUsers(content);
            
            return ResponseEntity.ok("SUCCESS: Notification sent to " + sentCount + " users");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("ERROR: " + e.getMessage());
        }
    }

    /**
     * Run multiple test scenarios
     */
    @GetMapping("/run-all-tests")
    public ResponseEntity<String> runAllTests() {
        StringBuilder results = new StringBuilder();
        results.append("RUNNING ALL TESTS\n");
        results.append("===================\n\n");
        
        try {
            // Test 1: Valid user
            results.append("Test 1 - Send to Valid User (ID: 1):\n");
            boolean test1 = notificationService.sendNotificationToUser(1L, "Test message for user 1");
            results.append(test1 ? "SUCCESS\n\n" : "FAILED\n\n");

            // Test 2: Invalid user
            results.append("Test 2 - Send to Invalid User (ID: 999):\n");
            boolean test2 = notificationService.sendNotificationToUser(999L, "Test message for invalid user");
            results.append(test2 ? "UNEXPECTED SUCCESS\n\n" : "FAILED AS EXPECTED\n\n");

            // Test 3: Send to all users
            results.append("Test 3 - Send to All Users:\n");
            int sentCount = notificationService.sendNotificationToAllUsers("Test broadcast message");
            results.append("Sent to " + sentCount + " users\n\n");

            results.append("ALL TESTS COMPLETED!");
            return ResponseEntity.ok(results.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Tests failed: " + e.getMessage());
        }
    }

    /**
     * Simple health check
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Test Controller is working!");
    }
}

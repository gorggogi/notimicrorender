package alertSystemSMS.notificationMicroservice.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void createAndSendNotification(Long alertId, String messageContent) {
        System.out.println("-----");
        System.out.println("Creating notification for alert ID: " + alertId);
        System.out.println("Message: '" + messageContent + "'");
        // Business logic to save notification and send SMS would go here
        System.out.println("Notification processed and sent to subscribed users.");
        System.out.println("-----");
    }

    public void updateUserPreference(Long userId, Long alertId, boolean isEnabled) {
        System.out.println("-----");
        System.out.println("Updating preference for User ID: " + userId + ", Alert ID: " + alertId + " to " + isEnabled);
        // Logic to find and update the UserPreferenceAlertType entity would go here
        System.out.println("Preference updated successfully.");
        System.out.println("-----");
    }

    public void logSmsDeliveryStatus(String messageSid, String status) {
        System.out.println("-----");
        System.out.println("Received delivery status update for Message SID: " + messageSid);
        System.out.println("New Status: " + status);
        // Logic to update NotificationLog entity would go here
        System.out.println("Status logged.");
        System.out.println("-----");
    }
}


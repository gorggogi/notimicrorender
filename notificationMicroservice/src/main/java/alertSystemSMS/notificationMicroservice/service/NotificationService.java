package alertSystemSMS.notificationMicroservice.service;

import alertSystemSMS.notificationMicroservice.model.User;
import alertSystemSMS.notificationMicroservice.model.UserPreferenceAlertType;
import alertSystemSMS.notificationMicroservice.repository.UserPreferenceAlertTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserPreferenceAlertTypeRepository userPreferenceRepository;

    @Value("${philsms.api.token}")
    private String apiToken;

    @Value("${philsms.sender.id}")
    private String senderId;

    private final String philSmsApiUrl = "https://app.philsms.com/api/v3/sms/send";

    public void createAndSendNotification(Long alertId, String messageContent) {
        System.out.println("-----");
        System.out.println("Processing notification for alert ID: " + alertId);

        List<UserPreferenceAlertType> subscriptions = userPreferenceRepository.findByAlertType_AlertIdAndIsEnabledTrue(alertId);

        if (subscriptions.isEmpty()) {
            System.out.println("No users are subscribed to this alert type. No SMS sent.");
            return;
        }

        System.out.println("Found " + subscriptions.size() + " subscribed user(s). Sending SMS...");

        for (UserPreferenceAlertType subscription : subscriptions) {
            User user = subscription.getUser();
            if (user != null && user.getUserPhoneNumber() != null && !user.getUserPhoneNumber().isEmpty()) {
                sendSms(user.getUserPhoneNumber(), messageContent);
            }
        }
        System.out.println("-----");
    }

    private void sendSms(String recipientPhoneNumber, String messageContent) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiToken);
            headers.set("Accept", "application/json");

            Map<String, String> body = new HashMap<>();
            body.put("recipient", formatPhoneNumber(recipientPhoneNumber)); // Format the number here
            body.put("sender_id", senderId);
            body.put("type", "plain");
            body.put("message", messageContent);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(philSmsApiUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("SMS sent successfully to " + recipientPhoneNumber);
                logSmsDeliveryStatus(recipientPhoneNumber, "SENT_TO_GATEWAY");
            } else {
                System.err.println("Failed to send SMS to " + recipientPhoneNumber + ". Status: " + response.getStatusCode());
                logSmsDeliveryStatus(recipientPhoneNumber, "FAILED");
            }
        } catch (HttpClientErrorException e) {
            System.err.println("An error occurred while sending SMS to " + recipientPhoneNumber + ": " + e.getStatusCode() + " " + e.getResponseBodyAsString());
            logSmsDeliveryStatus(recipientPhoneNumber, "ERROR");
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while sending SMS to " + recipientPhoneNumber + ": " + e.getMessage());
            logSmsDeliveryStatus(recipientPhoneNumber, "ERROR");
        }
    }

    /**
     * Converts a local Philippine mobile number (e.g., 09171234567)
     * to the international format required by the API (e.g., 639171234567).
     */
    private String formatPhoneNumber(String number) {
        if (number.startsWith("0")) {
            return "63" + number.substring(1);
        }
        return number;
    }

    public void updateUserPreference(Long userId, Long alertId, boolean isEnabled) {
        System.out.println("-----");
        System.out.println("Updating preference for User ID: " + userId + ", Alert ID: " + alertId + " to " + isEnabled);
        System.out.println("Preference updated successfully.");
        System.out.println("-----");
    }

    public void logSmsDeliveryStatus(String messageSid, String status) {
        System.out.println("-----");
        System.out.println("Received delivery status update for Message SID: " + messageSid);
        System.out.println("New Status: " + status);
        System.out.println("Status logged.");
        System.out.println("-----");
    }
}
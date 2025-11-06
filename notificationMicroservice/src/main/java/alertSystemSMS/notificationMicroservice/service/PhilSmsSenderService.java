package alertSystemSMS.notificationMicroservice.service;

import alertSystemSMS.notificationMicroservice.model.Notification;
import alertSystemSMS.notificationMicroservice.model.NotificationLog;
import alertSystemSMS.notificationMicroservice.repository.NotificationLogRepository;
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
import java.util.Map;

@Service
public class PhilSmsSenderService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    @Value("${philsms.api.token}")
    private String apiToken;

    @Value("${philsms.sender.id}")
    private String senderId;

    private final String philSmsApiUrl = "https://app.philsms.com/api/v3/sms/send";

    public void sendSms(String recipientPhoneNumber, String messageContent, Notification notification) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiToken);
            headers.set("Accept", "application/json");

            Map<String, String> body = new HashMap<>();
            body.put("recipient", formatPhoneNumber(recipientPhoneNumber));
            body.put("sender_id", senderId);
            body.put("type", "plain");
            body.put("message", messageContent);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(philSmsApiUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("SMS sent successfully to " + recipientPhoneNumber);
                createLogEntry(recipientPhoneNumber, "SENT_TO_GATEWAY", notification);
            } else {
                System.err.println("Failed to send SMS to " + recipientPhoneNumber + ". Status: " + response.getStatusCode());
                createLogEntry(recipientPhoneNumber, "FAILED", notification);
            }
        } catch (HttpClientErrorException e) {
            System.err.println("An error occurred while sending SMS to " + recipientPhoneNumber + ": " + e.getStatusCode() + " " + e.getResponseBodyAsString());
            createLogEntry(recipientPhoneNumber, "ERROR", notification);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while sending SMS to " + recipientPhoneNumber + ": " + e.getMessage());
            createLogEntry(recipientPhoneNumber, "ERROR", notification);
        }
    }

    private void createLogEntry(String recipient, String status, Notification notification) {
        if (notification == null) {
            return;
        }
        NotificationLog log = new NotificationLog();
        log.setNotification(notification);
        log.setStatus(status);
        log.setSentTo(recipient);
        notificationLogRepository.save(log);
    }

    private String formatPhoneNumber(String number) {
        if (number.startsWith("0")) {
            return "+63" + number.substring(1);
        }
        if (!number.startsWith("+")) {
            return "+" + number;
        }
        return number;
    }
}

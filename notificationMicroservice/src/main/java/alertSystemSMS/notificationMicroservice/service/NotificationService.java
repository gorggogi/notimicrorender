package alertSystemSMS.notificationMicroservice.service;

import alertSystemSMS.notificationMicroservice.model.AlertType;
import alertSystemSMS.notificationMicroservice.model.Notification;
import alertSystemSMS.notificationMicroservice.model.NotificationLog;
import alertSystemSMS.notificationMicroservice.model.User;
import alertSystemSMS.notificationMicroservice.model.UserPreferenceAlertType;
import alertSystemSMS.notificationMicroservice.repository.AlertTypeRepository;
import alertSystemSMS.notificationMicroservice.repository.NotificationLogRepository;
import alertSystemSMS.notificationMicroservice.repository.NotificationRepository;
import alertSystemSMS.notificationMicroservice.repository.UserPreferenceAlertTypeRepository;
import alertSystemSMS.notificationMicroservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserPreferenceAlertTypeRepository userPreferenceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AlertTypeRepository alertTypeRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationLogRepository notificationLogRepository;

    @Value("${philsms.api.token}")
    private String apiToken;

    @Value("${philsms.sender.id}")
    private String senderId;

    private final String philSmsApiUrl = "https://app.philsms.com/api/v3/sms/send";

    public void createAndSendNotification(Long alertId, String messageContent, Principal principal) {
        String sentBy = (principal != null) ? principal.getName() : "UNKNOWN";
        executeSend(alertId, messageContent, sentBy);
    }

    public void createAndSendNotification(Long alertId, String messageContent, String sentBy) {
        executeSend(alertId, messageContent, sentBy);
    }

    @Transactional
    private void executeSend(Long alertId, String messageContent, String sentBy) {
        System.out.println("-----");
        System.out.println("Processing notification for alert ID: " + alertId);

        AlertType alertType = alertTypeRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert type with ID " + alertId + " not found."));
        
        Notification notification = new Notification();
        notification.setAlertType(alertType);
        notification.setMessageContent(messageContent);
        notification.setSentBy(sentBy);
        Notification savedNotification = notificationRepository.save(notification);

        List<UserPreferenceAlertType> subscriptions = userPreferenceRepository.findByAlertType_AlertIdAndIsEnabledTrue(alertId);
        if (subscriptions.isEmpty()) {
            System.out.println("No users are subscribed to this alert type. No SMS sent.");
            return;
        }

        System.out.println("Found " + subscriptions.size() + " subscribed user(s). Sending SMS...");
        for (UserPreferenceAlertType subscription : subscriptions) {
            User user = subscription.getUser();
            if (user != null && user.getUserPhoneNumber() != null && !user.getUserPhoneNumber().isEmpty()) {
                sendSms(user.getUserPhoneNumber(), messageContent, savedNotification);
            }
        }
        System.out.println("-----");
    }

    private void sendSms(String recipientPhoneNumber, String messageContent, Notification notification) {
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
    
    public void sendDirectSms(String phoneNumber, String messageContent, String sentBy) {
        Notification notification = new Notification();
        notification.setMessageContent(messageContent);
        notification.setSentBy(sentBy);
        Notification savedNotification = notificationRepository.save(notification);
        sendSms(phoneNumber, messageContent, savedNotification);
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
    
    public void logSmsDeliveryStatus(String messageSid, String status) {
        System.out.println("-----");
        System.out.println("Received delivery status update for Message SID: " + messageSid);
        System.out.println("New Status: " + status);
        System.out.println("Status logged to console (not saved to DB).");
        System.out.println("-----");
    }

    private String formatPhoneNumber(String number) {
        if (number.startsWith("0")) {
            return "63" + number.substring(1);
        }
        return number;
    }

    @Transactional
    public void updateUserPreference(Long userId, Long alertId, boolean isEnabled) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<AlertType> alertTypeOpt = alertTypeRepository.findById(alertId);

        if (userOpt.isPresent() && alertTypeOpt.isPresent()) {
            User user = userOpt.get();
            AlertType alertType = alertTypeOpt.get();

            UserPreferenceAlertType.UserPreferencePK pk = new UserPreferenceAlertType.UserPreferencePK(userId, alertId);
            UserPreferenceAlertType preference = userPreferenceRepository.findById(pk)
                    .orElse(new UserPreferenceAlertType());

            preference.setUser(user);
            preference.setAlertType(alertType);
            preference.setEnabled(isEnabled);

            userPreferenceRepository.save(preference);
            System.out.println("Successfully updated preference in DB for User ID: " + userId + ", Alert ID: " + alertId + " to " + isEnabled);
        } else {
            System.err.println("Could not update preference: User or AlertType not found.");
        }
    }

    public boolean getUserPreference(Long userId, Long alertId) {
        UserPreferenceAlertType.UserPreferencePK pk = new UserPreferenceAlertType.UserPreferencePK(userId, alertId);
        Optional<UserPreferenceAlertType> preferenceOpt = userPreferenceRepository.findById(pk);
        return preferenceOpt.map(UserPreferenceAlertType::isEnabled).orElse(false);
    }

    public List<Map<String, Object>> getAllUserPreferences(Long userId) {
        List<AlertType> allAlertTypes = alertTypeRepository.findAll();
        List<UserPreferenceAlertType> userPreferences = userPreferenceRepository.findByUser_UserId(userId);

        Map<Long, Boolean> userPreferenceMap = userPreferences.stream()
                .collect(Collectors.toMap(
                        pref -> pref.getAlertType().getAlertId(),
                        UserPreferenceAlertType::isEnabled
                ));

        List<Map<String, Object>> responseList = new ArrayList<>();
        for (AlertType alertType : allAlertTypes) {
            Map<String, Object> preferenceData = new HashMap<>();
            preferenceData.put("alertId", alertType.getAlertId());
            preferenceData.put("alertName", alertType.getAlertName());
            preferenceData.put("alertDescription", alertType.getAlertDescription());
            preferenceData.put("isEnabled", userPreferenceMap.getOrDefault(alertType.getAlertId(), false));
            responseList.add(preferenceData);
        }

        return responseList;
    }

    public boolean sendNotificationToUser(Long userId, String content) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getUserPhoneNumber() != null && !user.getUserPhoneNumber().isEmpty()) {
                sendSms(user.getUserPhoneNumber(), content, null);
                System.out.println("Notification sent to user " + userId + ": " + content);
                return true;
            } else {
                System.err.println("User " + userId + " has no phone number");
                return false;
            }
        } else {
            System.err.println("User " + userId + " not found");
            return false;
        }
    }

    public int sendNotificationToAllUsers(String content) {
        List<User> allUsers = userRepository.findAll();
        int sentCount = 0;
        
        System.out.println("Sending notification to all users: " + content);
        for (User user : allUsers) {
            if (user.getUserPhoneNumber() != null && !user.getUserPhoneNumber().isEmpty()) {
                sendSms(user.getUserPhoneNumber(), content, null);
                sentCount++;
            }
        }
        
        System.out.println("Notification sent to " + sentCount + " out of " + allUsers.size() + " users");
        return sentCount;
    }
}

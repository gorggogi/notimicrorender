package alertSystemSMS.notificationMicroservice.service;

import alertSystemSMS.notificationMicroservice.model.AlertType;
import alertSystemSMS.notificationMicroservice.model.Notification;
import alertSystemSMS.notificationMicroservice.model.User;
import alertSystemSMS.notificationMicroservice.model.UserPreferenceAlertType;
import alertSystemSMS.notificationMicroservice.repository.AlertTypeRepository;
import alertSystemSMS.notificationMicroservice.repository.NotificationRepository;
import alertSystemSMS.notificationMicroservice.repository.UserPreferenceAlertTypeRepository;
import alertSystemSMS.notificationMicroservice.repository.UserRepository;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final UserPreferenceAlertTypeRepository userPreferenceRepository;
    private final UserRepository userRepository;
    private final AlertTypeRepository alertTypeRepository;
    private final NotificationRepository notificationRepository;
    private final SmsRoutingService smsRoutingService;

    public NotificationService(UserPreferenceAlertTypeRepository userPreferenceRepository, UserRepository userRepository, AlertTypeRepository alertTypeRepository, NotificationRepository notificationRepository, SmsRoutingService smsRoutingService) {
        this.userPreferenceRepository = userPreferenceRepository;
        this.userRepository = userRepository;
        this.alertTypeRepository = alertTypeRepository;
        this.notificationRepository = notificationRepository;
        this.smsRoutingService = smsRoutingService;
    }


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
                smsRoutingService.sendSms(user.getUserPhoneNumber(), messageContent, savedNotification);
            }
        }
        System.out.println("-----");
    }

    
    public void sendDirectSms(String phoneNumber, String messageContent, String sentBy) {
        // This method is now effectively handled by SmsRoutingService and the controller.
        // It can be kept for internal use or deprecated if the controller is the only entry point.
        smsRoutingService.sendSms(phoneNumber, messageContent, null);
    }

    
    public void logSmsDeliveryStatus(String messageSid, String status) {
        System.out.println("-----");
        System.out.println("Received delivery status update for Message SID: " + messageSid);
        System.out.println("New Status: " + status);
        System.out.println("Status logged to console (not saved to DB).");
        System.out.println("-----");
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
                smsRoutingService.sendSms(user.getUserPhoneNumber(), content, null);
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



    /**
     * Creates and saves a Notification for a direct message in its own new transaction.
     * This ensures the notification has a committed ID before any subsequent operations (like logging) reference it.
     * @param message The message content.
     * @param sentBy The sender.
     * @return The saved Notification entity with a database-generated ID.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Notification createAndLogDirectMessage(String message, String sentBy) {
        Notification notification = new Notification();
        notification.setMessageContent(message);
        notification.setSentBy(sentBy);
        return notificationRepository.save(notification);
    }

    public int sendNotificationToAllUsers(String content) {
        List<User> allUsers = userRepository.findAll();
        int sentCount = 0;
        
        System.out.println("Sending notification to all users: " + content);
        for (User user : allUsers) {
            if (user.getUserPhoneNumber() != null && !user.getUserPhoneNumber().isEmpty()) {
                smsRoutingService.sendSms(user.getUserPhoneNumber(), content, null);
                sentCount++;
            }
        }
        
        System.out.println("Notification sent to " + sentCount + " out of " + allUsers.size() + " users");
        return sentCount;
    }
}

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

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

    @Transactional
    public boolean sendNotificationToUser(Long userId, String content, Principal principal) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().getUserPhoneNumber() == null || userOpt.get().getUserPhoneNumber().isEmpty()) {
            logger.error("Cannot send notification: User {} not found or has no phone number.", userId);
            return false;
        }

        User user = userOpt.get();
        String sentBy = (principal != null) ? principal.getName() : "API";

        Notification notification = new Notification();
        notification.setMessageContent(content);
        notification.setSentBy(sentBy);
        // Note: No AlertType for this kind of direct notification
        Notification savedNotification = notificationRepository.save(notification);

        smsRoutingService.sendSms(user.getUserPhoneNumber(), content, savedNotification);
        logger.info("Notification sent to user {}: {}", userId, content);
        return true;
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
        try {
            logger.info("[SERVICE] Inside createAndLogDirectMessage. About to save.");
            Notification notification = new Notification();
            notification.setMessageContent(message);
            notification.setSentBy(sentBy);
            Notification savedNotification = notificationRepository.save(notification);
            logger.info("[SERVICE] Save complete. Notification ID: {}", savedNotification.getNotificationId());
            return savedNotification;
        } catch (Exception e) {
            logger.error("[SERVICE] CRITICAL ERROR inside createAndLogDirectMessage transaction: {}", e.getMessage(), e);
            // Re-throw to ensure the transaction rolls back and the caller is aware of the failure.
            throw e;
        }
    }

    @Transactional
    public int sendNotificationToAllUsers(String content, Principal principal) {
        List<User> allUsers = userRepository.findAll();
        int sentCount = 0;

        String sentBy = (principal != null) ? principal.getName() : "API";

        // Create a single notification record for this broadcast
        Notification notification = new Notification();
        notification.setMessageContent(content);
        notification.setSentBy(sentBy);
        Notification savedNotification = notificationRepository.save(notification);

        logger.info("Sending broadcast notification to all users: {}", content);
        for (User user : allUsers) {
            if (user.getUserPhoneNumber() != null && !user.getUserPhoneNumber().isEmpty()) {
                smsRoutingService.sendSms(user.getUserPhoneNumber(), content, savedNotification);
                sentCount++;
            }
        }
        
        logger.info("Broadcast notification sent to {} out of {} users", sentCount, allUsers.size());
        return sentCount;
    }
}

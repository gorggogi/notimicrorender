package alertSystemSMS.notificationMicroservice.controller;

import alertSystemSMS.notificationMicroservice.model.AlertType;
import alertSystemSMS.notificationMicroservice.model.User;
import alertSystemSMS.notificationMicroservice.model.UserPreferenceAlertType;
import alertSystemSMS.notificationMicroservice.repository.AlertTypeRepository;
import alertSystemSMS.notificationMicroservice.repository.UserRepository;
import alertSystemSMS.notificationMicroservice.repository.UserPreferenceAlertTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/preferences")
public class UserPreferencesController {

    private static final Logger logger = LoggerFactory.getLogger(UserPreferencesController.class);

    private final UserRepository userRepository;
    private final AlertTypeRepository alertTypeRepository;
    private final UserPreferenceAlertTypeRepository preferenceRepository;

    public UserPreferencesController(UserRepository userRepository, 
                                   AlertTypeRepository alertTypeRepository,
                                   UserPreferenceAlertTypeRepository preferenceRepository) {
        this.userRepository = userRepository;
        this.alertTypeRepository = alertTypeRepository;
        this.preferenceRepository = preferenceRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserPreferences(@PathVariable Long userId) {
        logger.info("Getting preferences for user with ID: {}", userId);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        List<AlertType> allAlertTypes = alertTypeRepository.findAll();
        List<UserPreferenceAlertType> userPreferences = preferenceRepository.findByUser_UserId(user.getUserId());

        List<Map<String, Object>> result = new ArrayList<>();
        for (AlertType alertType : allAlertTypes) {
            boolean isEnabled = userPreferences.stream()
                    .anyMatch(pref -> pref.getAlertType().getAlertId().equals(alertType.getAlertId()) && pref.isEnabled());
            
            result.add(Map.of(
                "alertId", alertType.getAlertId(),
                "alertName", alertType.getAlertName(),
                "alertDescription", alertType.getAlertDescription() != null ? alertType.getAlertDescription() : "",
                "enabled", isEnabled
            ));
        }

        return ResponseEntity.ok(Map.of(
            "userId", user.getUserId(),
            "email", user.getEmail(),
            "alertTypes", result
        ));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> updateUserPreferences(@PathVariable Long userId, 
                                                  @RequestBody Map<String, Object> preferences) {
        logger.info("Updating preferences for user with ID: {}", userId);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> alertTypes = (List<Map<String, Object>>) preferences.get("alertTypes");
            
            // Clear existing preferences
            List<UserPreferenceAlertType> existingPrefs = preferenceRepository.findByUser_UserId(user.getUserId());
            preferenceRepository.deleteAll(existingPrefs);

            // Add new preferences
            for (Map<String, Object> alertTypeData : alertTypes) {
                Long alertId = Long.valueOf(alertTypeData.get("alertId").toString());
                Boolean enabled = (Boolean) alertTypeData.get("enabled");

                Optional<AlertType> alertTypeOpt = alertTypeRepository.findById(alertId);
                if (alertTypeOpt.isPresent() && enabled) {
                    UserPreferenceAlertType preference = new UserPreferenceAlertType();
                    preference.setUser(user);
                    preference.setAlertType(alertTypeOpt.get());
                    preference.setEnabled(true);
                    preferenceRepository.save(preference);
                }
            }

            logger.info("Successfully updated preferences for user: {}", userId);
            return ResponseEntity.ok(Map.of("message", "Preferences updated successfully"));
            
        } catch (Exception e) {
            logger.error("Error updating preferences for user: {}. Error: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update preferences: " + e.getMessage()));
        }
    }
}

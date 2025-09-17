package alertSystemSMS.notificationMicroservice.service;

import alertSystemSMS.notificationMicroservice.model.AlertType;
import alertSystemSMS.notificationMicroservice.model.User;
import alertSystemSMS.notificationMicroservice.model.UserPreferenceAlertType;
import alertSystemSMS.notificationMicroservice.repository.AlertTypeRepository;
import alertSystemSMS.notificationMicroservice.repository.UserPreferenceAlertTypeRepository;
import alertSystemSMS.notificationMicroservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlertTypeService {

    @Autowired
    private AlertTypeRepository alertTypeRepository;

    @Autowired
    private UserRepository userRepository; // Added to get all users

    @Autowired
    private UserPreferenceAlertTypeRepository userPreferenceRepository; // Added for managing preferences

    public List<AlertType> findAllAlertTypes() {
        return alertTypeRepository.findAll();
    }

    /**
     * Saves a new alert type and automatically creates a disabled (false) preference
     * for it for every existing user.
     */
    @Transactional
    public void saveAlertType(AlertType alertType) {
        // First, save the new alert type to generate its ID
        AlertType savedAlertType = alertTypeRepository.save(alertType);

        // Then, get all existing users
        List<User> allUsers = userRepository.findAll();

        // Create a new, disabled preference for each user for this new alert type
        for (User user : allUsers) {
            UserPreferenceAlertType newPreference = new UserPreferenceAlertType();
            newPreference.setUser(user);
            newPreference.setAlertType(savedAlertType);
            newPreference.setEnabled(false); // Default to disabled
            userPreferenceRepository.save(newPreference);
        }
    }

    /**
     * Deletes an alert type and all associated user preference entries.
     */
    @Transactional
    public void deleteAlertType(Long alertId) {
        // First, delete all user preferences linked to this alert type
        userPreferenceRepository.deleteByAlertType_AlertId(alertId);

        // Then, delete the alert type itself
        alertTypeRepository.deleteById(alertId);
    }
}

package alertSystemSMS.notificationMicroservice.repository;

import alertSystemSMS.notificationMicroservice.model.UserPreferenceAlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserPreferenceAlertTypeRepository extends JpaRepository<UserPreferenceAlertType, UserPreferenceAlertType.UserPreferencePK> {

    List<UserPreferenceAlertType> findByAlertType_AlertIdAndIsEnabledTrue(Long alertId);

    List<UserPreferenceAlertType> findByUser_UserId(Long userId);

    /**
     * Deletes all user preference entries associated with a specific alert type.
     * This is necessary to clean up preferences before deleting an alert type.
     */
    @Transactional
    void deleteByAlertType_AlertId(Long alertId);
}


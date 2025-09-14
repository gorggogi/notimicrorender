package alertSystemSMS.notificationMicroservice.repository;

import alertSystemSMS.notificationMicroservice.model.UserPreferenceAlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserPreferenceAlertType entity.
 * Note that the primary key type is the composite key class, UserPreferencePK.
 */
@Repository
public interface UserPreferenceAlertTypeRepository extends JpaRepository<UserPreferenceAlertType, UserPreferenceAlertType.UserPreferencePK> {
}


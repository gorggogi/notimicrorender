package alertSystemSMS.notificationMicroservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import alertSystemSMS.notificationMicroservice.model.AlertType;

@Repository
public interface AlertTypeRepository extends JpaRepository<AlertType, Long> {
    Optional<AlertType> findByAlertName(String alertName);
}

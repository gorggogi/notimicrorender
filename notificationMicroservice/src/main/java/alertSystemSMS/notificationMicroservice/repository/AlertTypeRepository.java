package alertSystemSMS.notificationMicroservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import alertSystemSMS.notificationMicroservice.model.AlertType;

@Repository
public interface AlertTypeRepository extends JpaRepository<AlertType, Long> {}


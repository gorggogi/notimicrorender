package alertSystemSMS.notificationMicroservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import alertSystemSMS.notificationMicroservice.model.Notification;
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

}

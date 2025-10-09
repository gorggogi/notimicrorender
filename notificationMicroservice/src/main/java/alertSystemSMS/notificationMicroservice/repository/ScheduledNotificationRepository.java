package alertSystemSMS.notificationMicroservice.repository;

import alertSystemSMS.notificationMicroservice.model.ScheduledNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ScheduledNotificationRepository extends JpaRepository<ScheduledNotification, Long> {
    List<ScheduledNotification> findByScheduleTimeBefore(Date date);
}
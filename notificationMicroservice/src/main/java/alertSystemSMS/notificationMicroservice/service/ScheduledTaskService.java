package alertSystemSMS.notificationMicroservice.service;

import alertSystemSMS.notificationMicroservice.model.ScheduledNotification;
import alertSystemSMS.notificationMicroservice.repository.ScheduledNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ScheduledTaskService {

    @Autowired
    private ScheduledNotificationRepository scheduledNotificationRepository;

    @Autowired
    private NotificationService notificationService;

    @Scheduled(fixedRate = 60000) // Runs every minute
    public void processScheduledNotifications() {
        List<ScheduledNotification> notifications = scheduledNotificationRepository.findByScheduleTimeBefore(new Date());
        for (ScheduledNotification notification : notifications) {
            // Call the overloaded service method with the 'sentBy' string
            notificationService.createAndSendNotification(
                notification.getAlertId(), 
                notification.getMessage(),
                notification.getSentBy()
            );
            scheduledNotificationRepository.delete(notification);
        }
    }
}
package alertSystemSMS.notificationMicroservice.controller;

import alertSystemSMS.notificationMicroservice.model.Notification;
import alertSystemSMS.notificationMicroservice.model.NotificationLog;
import alertSystemSMS.notificationMicroservice.repository.NotificationRepository;
import alertSystemSMS.notificationMicroservice.repository.NotificationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class LogController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    /**
     * Shows the detailed recipient logs and analytics dashboard.
     */
    @GetMapping("/analytics")
    public String showAnalyticsPage(Model model) {
        List<NotificationLog> logs = notificationLogRepository.findAll();

        long totalSent = logs.stream().filter(log -> log.getSentTo() != null).count();
        long successful = logs.stream().filter(log -> "SENT_TO_GATEWAY".equals(log.getStatus())).count();
        long failed = totalSent - successful;
        double successRate = (totalSent == 0) ? 0 : ((double) successful / totalSent) * 100;
        long totalAnnouncements = notificationRepository.count();

        model.addAttribute("logs", logs);
        model.addAttribute("totalAnnouncements", totalAnnouncements);
        model.addAttribute("totalSent", totalSent);
        model.addAttribute("successful", successful);
        model.addAttribute("failed", failed);
        model.addAttribute("successRate", String.format("%.2f", successRate));

        return "analytics";
    }

    /**
     * Shows the master log of all created announcements.
     */
    @GetMapping("/announcement-logs")
    public String showAnnouncementLogs(Model model) {
        List<Notification> announcements = notificationRepository.findAll();
        model.addAttribute("announcements", announcements);
        return "announcement-logs";
    }
}
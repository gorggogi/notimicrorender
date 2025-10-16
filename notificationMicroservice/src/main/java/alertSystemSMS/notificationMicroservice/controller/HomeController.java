package alertSystemSMS.notificationMicroservice.controller;

import alertSystemSMS.notificationMicroservice.repository.NotificationLogRepository;
import alertSystemSMS.notificationMicroservice.repository.NotificationRepository;
import alertSystemSMS.notificationMicroservice.repository.ScheduledNotificationRepository;
import alertSystemSMS.notificationMicroservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    @Autowired
    private ScheduledNotificationRepository scheduledNotificationRepository;

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/admin/home")
    public String homePage(Model model) {
        long totalAnnouncements = notificationRepository.count();
        long totalUsers = userRepository.count();
        long successfulDeliveries = notificationLogRepository.findAll().stream()
                .filter(log -> "SENT_TO_GATEWAY".equals(log.getStatus()))
                .count();
        long scheduledCount = scheduledNotificationRepository.count();

        model.addAttribute("totalAnnouncements", totalAnnouncements);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("successfulDeliveries", successfulDeliveries);
        model.addAttribute("scheduledCount", scheduledCount);

        return "index";
    }

    @GetMapping("/admin")
    public String redirectToHome() {
        return "redirect:/admin/home";
    }

    @GetMapping("/admin/api-docs")
    public String apiDocsPage() {
        return "api-documentation";
    }
}

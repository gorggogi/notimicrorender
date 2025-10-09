package alertSystemSMS.notificationMicroservice.controller;

import alertSystemSMS.notificationMicroservice.service.AlertTypeService;
import alertSystemSMS.notificationMicroservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AlertTypeService alertTypeService;

    @GetMapping("/announcement")
    public String showAnnouncementForm(Model model) {
        model.addAttribute("alertTypes", alertTypeService.findAllAlertTypes());
        return "announcement-form";
    }

    @PostMapping("/announcement")
    public String createAnnouncement(@RequestParam Long alertId,
                                     @RequestParam String message,
                                     RedirectAttributes redirectAttributes) {
        if (message == null || message.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Message content cannot be empty.");
            return "redirect:/admin/announcement";
        }

        try {
            notificationService.createAndSendNotification(alertId, message);
            redirectAttributes.addFlashAttribute("success", "Announcement sent successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send announcement: " + e.getMessage());
        }

        return "redirect:/admin/announcement";
    }
}


package alertSystemSMS.notificationMicroservice.controller;

import alertSystemSMS.notificationMicroservice.model.ApiKey;
import alertSystemSMS.notificationMicroservice.model.Notification;
import alertSystemSMS.notificationMicroservice.model.ScheduledNotification;
import alertSystemSMS.notificationMicroservice.repository.ScheduledNotificationRepository;
import alertSystemSMS.notificationMicroservice.service.ApiKeyService;
import alertSystemSMS.notificationMicroservice.service.AlertTypeService;
import alertSystemSMS.notificationMicroservice.service.NotificationService;
import alertSystemSMS.notificationMicroservice.service.SmsRoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AlertTypeService alertTypeService;

    @Autowired
    private ScheduledNotificationRepository scheduledNotificationRepository;

    @Autowired
    private SmsRoutingService smsRoutingService;

    @Autowired
    private ApiKeyService apiKeyService;

    private static final long LOCKOUT_PERIOD_MILLIS = TimeUnit.MINUTES.toMillis(15);
    private static final String LOCKOUT_PERIOD_TEXT = "15 minutes";

    @GetMapping("/announcement")
    public String showAnnouncementForm(Model model) {
        model.addAttribute("alertTypes", alertTypeService.findAllAlertTypes());
        model.addAttribute("lockoutPeriodText", LOCKOUT_PERIOD_TEXT);
        return "announcement-form";
    }

    @PostMapping("/announcement")
    public String createAnnouncement(@RequestParam Long alertId,
                                     @RequestParam String message,
                                     @RequestParam(required = false) String scheduleTime,
                                     RedirectAttributes redirectAttributes,
                                     Principal principal) {
        if (message == null || message.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Message content cannot be empty.");
            return "redirect:/admin/announcement";
        }

        try {
            if (scheduleTime != null && !scheduleTime.isEmpty()) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                Date date = formatter.parse(scheduleTime);

                if (date.before(new Date())) {
                    redirectAttributes.addFlashAttribute("error", "Scheduled time cannot be in the past.");
                    return "redirect:/admin/announcement";
                }

                ScheduledNotification scheduledNotification = new ScheduledNotification();
                scheduledNotification.setAlertId(alertId);
                scheduledNotification.setMessage(message);
                scheduledNotification.setScheduleTime(date);
                scheduledNotification.setSentBy(principal.getName());
                scheduledNotificationRepository.save(scheduledNotification);
                redirectAttributes.addFlashAttribute("success", "Announcement scheduled successfully!");
            } else {
                notificationService.createAndSendNotification(alertId, message, principal);
                redirectAttributes.addFlashAttribute("success", "Announcement sent successfully!");
            }
        } catch (ParseException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid date format for schedule.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send or schedule announcement: " + e.getMessage());
        }

        return "redirect:/admin/announcement";
    }

    @GetMapping("/scheduled-announcements")
    public String showScheduledAnnouncements(Model model) {
        List<ScheduledNotification> scheduled = scheduledNotificationRepository.findAll();
        model.addAttribute("scheduledNotifications", scheduled);
        model.addAttribute("lockoutPeriodMillis", LOCKOUT_PERIOD_MILLIS);
        return "scheduled-announcements";
    }

    @GetMapping("/edit-scheduled/{id}")
    public String showEditScheduledForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<ScheduledNotification> optionalNotification = scheduledNotificationRepository.findById(id);
        if (optionalNotification.isPresent()) {
            ScheduledNotification notification = optionalNotification.get();
            if (notification.getScheduleTime().getTime() - System.currentTimeMillis() < LOCKOUT_PERIOD_MILLIS) {
                redirectAttributes.addFlashAttribute("error", "Cannot edit an announcement that is scheduled for less than " + LOCKOUT_PERIOD_TEXT + " from now.");
                return "redirect:/admin/scheduled-announcements";
            }
            model.addAttribute("scheduledNotification", notification);
            model.addAttribute("alertTypes", alertTypeService.findAllAlertTypes());
            return "edit-scheduled-announcement";
        }
        redirectAttributes.addFlashAttribute("error", "Scheduled announcement not found.");
        return "redirect:/admin/scheduled-announcements";
    }

    @PostMapping("/edit-scheduled/{id}")
    public String updateScheduledAnnouncement(@PathVariable("id") Long id,
                                              @RequestParam Long alertId,
                                              @RequestParam String message,
                                              @RequestParam String scheduleTime,
                                              RedirectAttributes redirectAttributes) {
        Optional<ScheduledNotification> optionalNotification = scheduledNotificationRepository.findById(id);
        if (optionalNotification.isPresent()) {
            try {
                ScheduledNotification notification = optionalNotification.get();
                if (notification.getScheduleTime().getTime() - System.currentTimeMillis() < LOCKOUT_PERIOD_MILLIS) {
                    redirectAttributes.addFlashAttribute("error", "Cannot edit an announcement that is scheduled for less than " + LOCKOUT_PERIOD_TEXT + " from now.");
                    return "redirect:/admin/scheduled-announcements";
                }

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                Date date = formatter.parse(scheduleTime);
                if (date.before(new Date())) {
                    redirectAttributes.addFlashAttribute("error", "Scheduled time cannot be in the past.");
                    return "redirect:/admin/edit-scheduled/" + id;
                }

                notification.setAlertId(alertId);
                notification.setMessage(message);
                notification.setScheduleTime(date);
                scheduledNotificationRepository.save(notification);
                redirectAttributes.addFlashAttribute("success", "Scheduled announcement updated successfully!");
                return "redirect:/admin/scheduled-announcements";

            } catch (ParseException e) {
                redirectAttributes.addFlashAttribute("error", "Invalid date format.");
                return "redirect:/admin/edit-scheduled/" + id;
            }
        }
        redirectAttributes.addFlashAttribute("error", "Scheduled announcement not found.");
        return "redirect:/admin/scheduled-announcements";
    }

    @PostMapping("/delete-scheduled/{id}")
    public String deleteScheduledAnnouncement(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<ScheduledNotification> optionalNotification = scheduledNotificationRepository.findById(id);
        if (optionalNotification.isPresent()) {
            ScheduledNotification notification = optionalNotification.get();
             if (notification.getScheduleTime().getTime() - System.currentTimeMillis() < LOCKOUT_PERIOD_MILLIS) {
                redirectAttributes.addFlashAttribute("error", "Cannot delete an announcement that is scheduled for less than " + LOCKOUT_PERIOD_TEXT + " from now.");
                return "redirect:/admin/scheduled-announcements";
            }
            scheduledNotificationRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Scheduled announcement deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Scheduled announcement not found.");
        }
        return "redirect:/admin/scheduled-announcements";
    }

    @GetMapping("/direct-message")
    public String showDirectMessageForm() {
        return "direct-message-form";
    }

    @PostMapping("/direct-message")
    public String sendDirectMessage(@RequestParam String phoneNumber,
                                    @RequestParam String message,
                                    RedirectAttributes redirectAttributes,
                                    Principal principal) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty() || message == null || message.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Phone number and message cannot be empty.");
            return "redirect:/admin/direct-message";
        }
        try {
            String sentBy = principal != null ? principal.getName() : "Admin";

            // 1. Save the notification to the database in its own transaction
            Notification savedNotification = notificationService.createAndLogDirectMessage(message, sentBy);

            // 2. Send the SMS
            smsRoutingService.sendSms(phoneNumber, message, savedNotification);
            redirectAttributes.addFlashAttribute("success", "Message sent successfully to " + phoneNumber);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send message: " + e.getMessage());
        }
        return "redirect:/admin/direct-message";
    }

    @GetMapping("/api-keys")
    public String showApiKeysPage() {
        return "api-keys";
    }

    @PostMapping("/generate-api-key")
    public String generateApiKey(@RequestParam String clientName, RedirectAttributes redirectAttributes) {
        if (clientName == null || clientName.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Client name cannot be empty.");
            return "redirect:/admin/api-keys";
        }

        try {
            ApiKey newApiKey = apiKeyService.generateApiKey(clientName);
            redirectAttributes.addFlashAttribute("success", "API Key generated successfully!");
            redirectAttributes.addFlashAttribute("generatedKey", newApiKey.getApiKey());
            redirectAttributes.addFlashAttribute("clientName", newApiKey.getClientName());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to generate API Key: " + e.getMessage());
        }

        return "redirect:/admin/api-keys";
    }
}

package alertSystemSMS.notificationMicroservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/admin/home")
    public String homePage() {
        return "index";
    }

    @GetMapping("/admin")
    public String redirectToHome() {
        return "redirect:/admin/home";
    }

    // REMOVED THIS METHOD - It is now handled by AnalyticsController
    // @GetMapping("/admin/logs")
    // public String logsPage() {
    //     return "logs";
    // }
}
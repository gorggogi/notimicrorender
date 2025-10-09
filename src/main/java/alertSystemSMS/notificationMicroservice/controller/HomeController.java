package alertSystemSMS.notificationMicroservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Root path redirects to the login page
    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    // Explicitly handle the /login GET request
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

    @GetMapping("/admin/logs")
    public String logsPage() {
        return "logs";
    }
}


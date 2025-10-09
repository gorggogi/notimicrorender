package alertSystemSMS.notificationMicroservice.controller;

import alertSystemSMS.notificationMicroservice.model.AlertType;
import alertSystemSMS.notificationMicroservice.service.AlertTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/alert-types")
public class AlertTypeController {

    @Autowired
    private AlertTypeService alertTypeService;

    @GetMapping
    public String showAlertTypesPage(Model model) {
        model.addAttribute("alertTypes", alertTypeService.findAllAlertTypes());
        model.addAttribute("newAlertType", new AlertType());
        return "alert-types";
    }

    @PostMapping
    public String addAlertType(@ModelAttribute AlertType newAlertType, RedirectAttributes redirectAttributes) {
        try {
            alertTypeService.saveAlertType(newAlertType);
            redirectAttributes.addFlashAttribute("success", "New alert type created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating alert type: " + e.getMessage());
        }
        return "redirect:/admin/alert-types";
    }

    @PostMapping("/delete/{id}")
    public String deleteAlertType(@PathVariable("id") Long alertId, RedirectAttributes redirectAttributes) {
        try {
            alertTypeService.deleteAlertType(alertId);
            redirectAttributes.addFlashAttribute("success", "Alert type deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting alert type. It might be in use.");
        }
        return "redirect:/admin/alert-types";
    }
}


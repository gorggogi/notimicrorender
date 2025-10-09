package alertSystemSMS.notificationMicroservice.controller;

import alertSystemSMS.notificationMicroservice.model.AlertType;
import alertSystemSMS.notificationMicroservice.service.AlertTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AlertTypeRestController {

    private static final Logger logger = LoggerFactory.getLogger(AlertTypeRestController.class);
    private final AlertTypeService alertTypeService;

    public AlertTypeRestController(AlertTypeService alertTypeService) {
        this.alertTypeService = alertTypeService;
    }

    @GetMapping("/alert-types")
    public List<AlertType> getAllAlertTypes() {
        logger.info("REST API: Fetching all alert types for external service integration");
        List<AlertType> alertTypes = alertTypeService.findAllAlertTypes();
        logger.info("REST API: Returning {} alert types", alertTypes.size());
        return alertTypes;
    }
}

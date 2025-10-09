package alertSystemSMS.notificationMicroservice.config;

import alertSystemSMS.notificationMicroservice.model.AlertType;
import alertSystemSMS.notificationMicroservice.repository.AlertTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component; // Unused since @Component is disabled

//@Component // Disabled - alert types should be created by admins via web interface
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final AlertTypeRepository alertTypeRepository;

    public DataInitializer(AlertTypeRepository alertTypeRepository) {
        this.alertTypeRepository = alertTypeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing sample alert types...");
        
        // Check if alert types already exist
        if (alertTypeRepository.count() == 0) {
            createSampleAlertTypes();
            logger.info("Sample alert types created successfully");
        } else {
            logger.info("Alert types already exist, skipping initialization");
        }
    }

    private void createSampleAlertTypes() {
        // Weather Alerts
        AlertType weatherAlert = new AlertType();
        weatherAlert.setAlertName("Weather Alert");
        weatherAlert.setAlertDescription("Receive notifications about severe weather conditions, storms, and weather warnings in your area.");
        alertTypeRepository.save(weatherAlert);

        // Emergency Alerts
        AlertType emergencyAlert = new AlertType();
        emergencyAlert.setAlertName("Emergency Alert");
        emergencyAlert.setAlertDescription("Critical emergency notifications including natural disasters, public safety warnings, and evacuation notices.");
        alertTypeRepository.save(emergencyAlert);

        // Traffic Alerts
        AlertType trafficAlert = new AlertType();
        trafficAlert.setAlertName("Traffic Alert");
        trafficAlert.setAlertDescription("Traffic updates, road closures, accidents, and transportation disruptions in your area.");
        alertTypeRepository.save(trafficAlert);

        // Health Alerts
        AlertType healthAlert = new AlertType();
        healthAlert.setAlertName("Health Alert");
        healthAlert.setAlertDescription("Public health notifications, disease outbreaks, vaccination reminders, and health advisories.");
        alertTypeRepository.save(healthAlert);

        // School Alerts
        AlertType schoolAlert = new AlertType();
        schoolAlert.setAlertName("School Alert");
        schoolAlert.setAlertDescription("School closures, schedule changes, and important educational announcements for students and parents.");
        alertTypeRepository.save(schoolAlert);

        // Community Alerts
        AlertType communityAlert = new AlertType();
        communityAlert.setAlertName("Community Alert");
        communityAlert.setAlertDescription("Local community events, municipal announcements, and neighborhood safety updates.");
        alertTypeRepository.save(communityAlert);

        logger.info("Created 6 sample alert types");
    }
}

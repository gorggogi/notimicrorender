package alertSystemSMS.notificationMicroservice.service;

import alertSystemSMS.notificationMicroservice.model.SmsGateRequest;
import alertSystemSMS.notificationMicroservice.model.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import alertSystemSMS.notificationMicroservice.model.Notification;
import alertSystemSMS.notificationMicroservice.model.NotificationLog;
import alertSystemSMS.notificationMicroservice.repository.NotificationLogRepository;

import java.util.Collections;

@Service
public class SmsGateService {

    private static final Logger logger = LoggerFactory.getLogger(SmsGateService.class);

    private final RestTemplate restTemplate;

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    private final String apiUrl = "https://api.sms-gate.app/3rdparty/v1/messages";

    @Value("${smsgate.api.username}")
    private String apiUsername;

    @Value("${smsgate.api.password}")
    private String apiPassword;

    @Value("${smsgate.api.deviceid}")
    private String apiDeviceId;

    public SmsGateService() {
        this.restTemplate = new RestTemplate();
    }

    public void sendSms(String phoneNumber, String message, Notification notification) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(apiUsername, apiPassword);
            headers.setContentType(MediaType.APPLICATION_JSON);

            SmsGateRequest requestBody = new SmsGateRequest(
                    new TextMessage(message),
                    apiDeviceId,
                    Collections.singletonList(formatPhoneNumber(phoneNumber))
            );

            HttpEntity<SmsGateRequest> entity = new HttpEntity<>(requestBody, headers);

            logger.info("Sending SMS to {} via SMSGate (fallback)", phoneNumber);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Successfully sent SMS to {} via SMSGate. Status: {}", phoneNumber, response.getStatusCode());
                createLogEntry(phoneNumber, "SENT_TO_GATEWAY", notification);
            } else {
                logger.error("Failed to send SMS via SMSGate. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
                createLogEntry(phoneNumber, "FAILED", notification);
            }
        } catch (Exception e) {
            logger.error("Error sending SMS via SMSGate to {}: {}", phoneNumber, e.getMessage());
            createLogEntry(phoneNumber, "ERROR", notification);
        }
    }

    private void createLogEntry(String recipient, String status, Notification notification) {
        if (notification == null) {
            return; // Don't log if there's no associated notification record
        }
        NotificationLog log = new NotificationLog();
        log.setNotification(notification);
        log.setStatus(status);
        log.setSentTo(recipient);
        notificationLogRepository.save(log);
    }

    private String formatPhoneNumber(String number) {
        if (number.startsWith("0")) {
            return "+63" + number.substring(1);
        }
        if (!number.startsWith("+")) {
            return "+" + number;
        }
        return number;
    }
}

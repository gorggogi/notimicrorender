package alertSystemSMS.notificationMicroservice.service;

import alertSystemSMS.notificationMicroservice.model.Notification;
import alertSystemSMS.notificationMicroservice.utils.CarrierUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsRoutingService {

    private static final Logger logger = LoggerFactory.getLogger(SmsRoutingService.class);

    private final PhilSmsSenderService philSmsSenderService; // For PhilSMS
    private final SmsGateService smsGateService;       // For SMART fallback

    @Autowired
    public SmsRoutingService(PhilSmsSenderService philSmsSenderService, SmsGateService smsGateService) {
        this.philSmsSenderService = philSmsSenderService;
        this.smsGateService = smsGateService;
    }

    /**
     * Sends an SMS to the given phone number, automatically routing to the correct gateway.
     * @param phoneNumber The recipient's phone number.
     * @param message The message to send.
     * @param sentBy The user or system sending the message.
     */
        public void sendSms(String phoneNumber, String message, Notification notification) {
        if (CarrierUtils.isSmartNumber(phoneNumber)) {
            logger.info("Detected SMART number for {}. Routing to SMSGate (fallback).");
            smsGateService.sendSms(phoneNumber, message, notification);
        } else {
            logger.info("Routing non-SMART number {} to PhilSms (primary).");
            philSmsSenderService.sendSms(phoneNumber, message, notification);
        }
    }
}

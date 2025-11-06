package alertSystemSMS.notificationMicroservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SmsGateRequest {

    @JsonProperty("textMessage")
    private TextMessage textMessage;

    @JsonProperty("deviceId")
    private String deviceId;

    @JsonProperty("phoneNumbers")
    private List<String> phoneNumbers;

    public SmsGateRequest(TextMessage textMessage, String deviceId, List<String> phoneNumbers) {
        this.textMessage = textMessage;
        this.deviceId = deviceId;
        this.phoneNumbers = phoneNumbers;
    }

    // Getters and setters
    public TextMessage getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(TextMessage textMessage) {
        this.textMessage = textMessage;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}

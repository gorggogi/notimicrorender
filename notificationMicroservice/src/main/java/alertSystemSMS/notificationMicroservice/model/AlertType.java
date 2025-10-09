package alertSystemSMS.notificationMicroservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "AlertTypes")
public class AlertType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertId;

    @Column(nullable = false, unique = true)
    private String alertName;

    @Column(columnDefinition = "TEXT")
    private String alertDescription;

    public AlertType() {}

    public Long getAlertId() { return alertId; }
    public void setAlertId(Long alertId) { this.alertId = alertId; }
    public String getAlertName() { return alertName; }
    public void setAlertName(String alertName) { this.alertName = alertName; }
    public String getAlertDescription() { return alertDescription; }
    public void setAlertDescription(String alertDescription) { this.alertDescription = alertDescription; }
}


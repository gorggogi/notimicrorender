package alertSystemSMS.notificationMicroservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import java.util.Date;

/**
 * Logs the status of each sent notification.
 * This class is mapped to the "NotificationLog" table in the database.
 */
@Entity
@Table(name = "NotificationLog")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @ManyToOne
    @JoinColumn(name = "notificationId", nullable = false)
    private Notification notification;

    @Column(nullable = false)
    private Date timestamp = new Date();

    @Column(nullable = false)
    private String status; // e.g., "SENT", "DELIVERED", "FAILED"

    // Constructors
    public NotificationLog() {}

    // Getters and Setters
    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


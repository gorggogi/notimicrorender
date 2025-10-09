package alertSystemSMS.notificationMicroservice.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "UserPreferenceAlertType")
@IdClass(UserPreferenceAlertType.UserPreferencePK.class)
public class UserPreferenceAlertType {

    @Id
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "alertId")
    private AlertType alertType;

    private boolean isEnabled;

    public UserPreferenceAlertType() {}

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public AlertType getAlertType() { return alertType; }
    public void setAlertType(AlertType alertType) { this.alertType = alertType; }
    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }

    public static class UserPreferencePK implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long user;
        private Long alertType;

        public UserPreferencePK() {}
        public UserPreferencePK(Long user, Long alertType) {
            this.user = user;
            this.alertType = alertType;
        }

        @Override
        public int hashCode() { return Objects.hash(user, alertType); }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            UserPreferencePK that = (UserPreferencePK) obj;
            return Objects.equals(user, that.user) && Objects.equals(alertType, that.alertType);
        }
    }
}


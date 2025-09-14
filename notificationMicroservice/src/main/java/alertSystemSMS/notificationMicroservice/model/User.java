package alertSystemSMS.notificationMicroservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Users")
public class User {

    @Id
    private Long userId;

    private String userPhoneNumber;
    private String firstName;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserPhoneNumber() { return userPhoneNumber; }
    public void setUserPhoneNumber(String userPhoneNumber) { this.userPhoneNumber = userPhoneNumber; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
}


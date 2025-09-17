package alertSystemSMS.notificationMicroservice.service;

import alertSystemSMS.notificationMicroservice.model.User;
import alertSystemSMS.notificationMicroservice.model.dto.UserDTO;
import alertSystemSMS.notificationMicroservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword()); // In a real application, you should hash the password here.
        user.setUserPhoneNumber(userDTO.getPhoneNumber());

        return userRepository.save(user);
    }
}

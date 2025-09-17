package alertSystemSMS.notificationMicroservice.controller;

import alertSystemSMS.notificationMicroservice.model.dto.UserDTO;
import alertSystemSMS.notificationMicroservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public void receiveUserData(@RequestBody UserDTO userDTO) {
        logger.info("Received user data request for email: {}", userDTO.getEmail());
        logger.info("User data received: firstName={}, lastName={}, email={}, phoneNumber={}", 
                userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), userDTO.getPhoneNumber());
        try {
            userService.createUser(userDTO);
            logger.info("Successfully created user in notification database for email: {}", userDTO.getEmail());
        } catch (Exception e) {
            logger.error("Failed to create user in notification database for email: {}. Error: {}", 
                    userDTO.getEmail(), e.getMessage(), e);
            throw e;
        }
    }
}

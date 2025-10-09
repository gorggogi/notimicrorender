package alertSystemSMS.notificationMicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@Async
@EnableScheduling
public class NotificationMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationMicroserviceApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@PostConstruct
	public void init() {
		// Set the default timezone to ensure consistency across the application
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Manila"));
	}
}
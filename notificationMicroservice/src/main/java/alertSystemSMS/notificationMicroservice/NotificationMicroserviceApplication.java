package alertSystemSMS.notificationMicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@Async
public class NotificationMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationMicroserviceApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
package alertSystemSMS.notificationMicroservice.service;

import alertSystemSMS.notificationMicroservice.model.ApiKey;
import alertSystemSMS.notificationMicroservice.repository.ApiKeyRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public ApiKeyService(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    public ApiKey generateApiKey(String clientName) {
        String newKey = generateNewKey();
        ApiKey apiKey = new ApiKey(newKey, clientName);
        return apiKeyRepository.save(apiKey);
    }

    private String generateNewKey() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}

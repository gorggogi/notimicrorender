package alertSystemSMS.notificationMicroservice.config.security;

import alertSystemSMS.notificationMicroservice.model.ApiKey;
import alertSystemSMS.notificationMicroservice.repository.ApiKeyRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class ApiKeyAuthInterceptor implements HandlerInterceptor {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyAuthInterceptor(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String apiKeyHeader = request.getHeader("X-API-KEY");

        if (apiKeyHeader == null || apiKeyHeader.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing API Key");
            return false;
        }

        Optional<ApiKey> apiKeyOptional = apiKeyRepository.findByApiKey(apiKeyHeader);

        if (apiKeyOptional.isEmpty() || !apiKeyOptional.get().isActive()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
            return false;
        }

        return true;
    }
}

package alertSystemSMS.notificationMicroservice.config.security;

import alertSystemSMS.notificationMicroservice.model.ApiKey;
import alertSystemSMS.notificationMicroservice.repository.ApiKeyRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class ApiKeyAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (apiKeyRepository == null) {
            WebApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
            this.apiKeyRepository = appContext.getBean(ApiKeyRepository.class);
        }

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

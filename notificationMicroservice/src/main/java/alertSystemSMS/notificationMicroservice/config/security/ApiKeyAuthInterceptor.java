package alertSystemSMS.notificationMicroservice.config.security;

import alertSystemSMS.notificationMicroservice.model.ApiKey;
import alertSystemSMS.notificationMicroservice.repository.ApiKeyRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        // 1. Check for API Key first (for external clients)
        String apiKeyHeader = request.getHeader("X-API-KEY");
        if (apiKeyHeader != null && !apiKeyHeader.isEmpty()) {
            Optional<ApiKey> apiKeyOptional = apiKeyRepository.findByApiKey(apiKeyHeader);
            if (apiKeyOptional.isPresent() && apiKeyOptional.get().isActive()) {
                return true; // Valid API Key found, allow access.
            }
        }

        // 2. If no valid API key, check for an authenticated admin session (for the admin dashboard)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
            if (isAdmin) {
                return true; // Authenticated admin found, allow access.
            }
        }

        // 3. If neither valid API key nor admin session, reject.
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        return false;
    }
}

package alertSystemSMS.notificationMicroservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain unifiedFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // 1. API security: Let ApiKeyAuthInterceptor handle it
                .requestMatchers("/api/**").permitAll()
                // 2. Static resources: Allow public access
                .requestMatchers("/styles/**", "/scripts/**").permitAll()
                // 3. Error endpoint: Allow public access for error rendering
                .requestMatchers("/error").permitAll()
                // 4. Admin pages: Require ADMIN role
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // 5. All other requests: Require authentication
                .anyRequest().authenticated()
            )
            // 5. CSRF Handling: Disable for API, enable for web
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            )
            // 6. Form Login for web UI
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/admin/home", true)
                .permitAll()
            )
            // 7. Logout for web UI
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        UserDetails admin = User.withUsername("admin")
            .password(encoder.encode("password"))
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(admin);
    }
}
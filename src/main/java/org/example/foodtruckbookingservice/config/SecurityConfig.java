package org.example.foodtruckbookingservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Security configuration for the API.
 * MVP: Basic Auth with in-memory users.
 * Phase 2: JWT-based authentication.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.security.staff.wagen1.password}")
    private String wagen1Password;

    @Value("${app.security.staff.wagen2.password}")
    private String wagen2Password;

    @Value("${app.security.admin.password}")
    private String adminPassword;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no auth required
                        .requestMatchers(HttpMethod.GET, "/api/v1/locations/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/schedule").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/reservations").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/reservations/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/reservations/code/**").permitAll()

                        // Actuator health endpoint
                        .requestMatchers("/actuator/health").permitAll()

                        // Staff endpoints
                        .requestMatchers("/api/v1/staff/**").hasAnyRole("STAFF", "ADMIN")

                        // Admin endpoints
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults());

        return http.build();
    }

    /**
     * In-memory user details for MVP.
     * Two truck accounts (wagen1, wagen2) for staff tablets.
     * Passwords are configured via environment variables.
     * TODO: Replace with database-backed UserDetailsService in Phase 2.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        var wagen1 = User.builder()
                .username("wagen1")
                .password(passwordEncoder.encode(wagen1Password))
                .roles("STAFF")
                .build();

        var wagen2 = User.builder()
                .username("wagen2")
                .password(passwordEncoder.encode(wagen2Password))
                .roles("STAFF")
                .build();

        var admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode(adminPassword))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(wagen1, wagen2, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

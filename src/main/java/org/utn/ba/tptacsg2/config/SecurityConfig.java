package org.utn.ba.tptacsg2.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    /**
     * Password encoder bean using Argon2 algorithm.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(
                16, // Salt length
                32, // Hash length
                1,  // Parallelism
                65536, // Memory cost
                4   // Iterations
        );
    }
    /**
     * Custom AuthenticationEntryPoint to handle unauthorized access attempts.
     * Responds with 401 status and a WWW-Authenticate header for Basic Auth.
     */
    @Bean
    AuthenticationEntryPoint basicEntryPoint() {
        return (HttpServletRequest req, HttpServletResponse res, AuthenticationException ex) -> {
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.setHeader("WWW-Authenticate", "Basic realm=\"Mi APP\", charset=\"UTF-8\"");
            res.getWriter().write("Unauthorized");
        };
    }

    /**
     * Security filter chain configuration.
     * Disables CSRF, sets session management to stateless, and configures endpoint access rules.
     * Allows unauthenticated access to /login and /user endpoints, while securing all other endpoints.
     * Uses HTTP Basic authentication with a custom entry point for handling unauthorized access.
     */
    @Bean
    SecurityFilterChain security(HttpSecurity http, AuthenticationEntryPoint basicEntryPoint) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST,"/login").permitAll()
                        .requestMatchers(HttpMethod.GET,"/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user").permitAll()
                        //.anyRequest().authenticated()
                        .anyRequest().permitAll()
                )
                .httpBasic(hb -> hb.authenticationEntryPoint(basicEntryPoint));
        return http.build();
    }
}
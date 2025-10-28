package org.utn.ba.tptacsg2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class CorsConfig {

    private final List<String> allowedCorsEntries;

    public CorsConfig(@Value("${app.security.cors.allowed-origins}") String allowedCorsOrigins) {
        this.allowedCorsEntries = Arrays.stream(allowedCorsOrigins.split(","))
                .map(String::trim)
                .filter(entry -> !entry.isEmpty())
                .toList();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = buildCorsConfiguration();

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                var originPatterns = buildAllowedPatterns();
                registry.addMapping("/**")
                        .allowedHeaders("*")
                        .allowedMethods("*")
                        .allowedOriginPatterns(originPatterns.toArray(new String[0]))
                        .allowCredentials(true);
            }
        };
    }

    private CorsConfiguration buildCorsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        buildAllowedPatterns().forEach(configuration::addAllowedOriginPattern);
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        return configuration;
    }

    private List<String> buildAllowedPatterns() {
        List<String> patterns = allowedCorsEntries.isEmpty()
                ? new ArrayList<>(List.of("*"))
                : allowedCorsEntries.stream()
                .filter(entry -> !entry.isBlank())
                .collect(Collectors.toCollection(ArrayList::new));

        if (!patterns.contains("*")) {
            patterns.add("*");
        }
        return patterns;
    }
}

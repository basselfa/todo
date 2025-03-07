package com.example.todo.todobackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {


    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Using allowedOriginPatterns instead of allowedOrigins for more flexibility with credentials
        config.addAllowedOriginPattern("http://localhost:[*]");  // Allows any localhost port
        config.addAllowedOriginPattern("https://*.netlify.app");  // Allows any Netlify subdomain
        // Add any other specific origins if needed
        // config.addAllowedOriginPattern("https://your-specific-domain.com");
        
        config.addAllowedHeader("*");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("PATCH");
        
        // Now we can safely enable credentials with patterns
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
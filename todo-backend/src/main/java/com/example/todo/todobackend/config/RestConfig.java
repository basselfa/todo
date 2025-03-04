package com.example.todo.todobackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import com.example.todo.todobackend.model.Task;

@Configuration
public class RestConfig implements RepositoryRestConfigurer {
    
    @Override
    public void configureRepositoryRestConfiguration(
            RepositoryRestConfiguration config, CorsRegistry cors) {
        
        // Expose entity IDs
        config.exposeIdsFor(Task.class);
        
        // Disable HATEOAS
        config.disableDefaultExposure();
        config.setDefaultMediaType(MediaType.APPLICATION_JSON);
        config.useHalAsDefaultJsonMediaType(false);
        
        // Use the same base path as in application.properties
        config.setBasePath("/api");
    }
}
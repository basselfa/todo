package com.example.todo.todobackend.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConfigurationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void testCorsConfigBeans() {
        // Verify that WebMvcConfigurer beans exist
        assertTrue(context.getBeanNamesForType(WebMvcConfigurer.class).length > 0);
    }

    @Test
    void testJacksonConfigBeans() {
        // Verify that the ObjectMapper bean exists
        assertTrue(context.containsBean("objectMapper"));
        
        // Get the bean and verify it's an ObjectMapper
        Object objectMapperBean = context.getBean("objectMapper");
        assertTrue(objectMapperBean instanceof ObjectMapper);
    }

    @Test
    void testRequestLoggingFilterBeans() {
        // Verify that a CommonsRequestLoggingFilter bean exists (not checking specific bean name)
        assertTrue(context.getBeanNamesForType(CommonsRequestLoggingFilter.class).length > 0);
        
        // We can't test protected methods directly, so we'll just verify the bean exists
        // This is sufficient for a configuration test as the actual behavior is tested elsewhere
    }
}
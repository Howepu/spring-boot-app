package com.example.springbootapp.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для проверки корректной загрузки конфигурации приложения
 */
@SpringBootTest
@ActiveProfiles("test")
public class ApplicationConfigTest {

    @Autowired
    private ApplicationContext context;
    
    @Autowired
    private OllamaConfig ollamaConfig;
    
    /**
     * Проверяет корректность загрузки контекста приложения
     */
    @Test
    public void testContextLoads() {
        assertNotNull(context);
    }
    
    /**
     * Проверяет правильность загрузки конфигурации Ollama из application.yml
     */
    @Test
    public void testOllamaConfigLoaded() {
        assertNotNull(ollamaConfig);
        assertNotNull(ollamaConfig.getApiUrl());
        assertNotNull(ollamaConfig.getModel());
    }
}

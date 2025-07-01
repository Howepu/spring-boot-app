package com.example.springbootapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация для работы с Ollama API
 */
@Configuration
@ConfigurationProperties(prefix = "ollama")
public class OllamaConfig {
    
    private String apiUrl;
    private String model;
    
    public String getApiUrl() {
        return apiUrl;
    }
    
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
}

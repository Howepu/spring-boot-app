package com.example.springbootapp.config;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Тесты для проверки конфигурации Ollama API
 */
public class OllamaConfigTest {

    @InjectMocks
    private OllamaConfig ollamaConfig;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(ollamaConfig, "apiUrl", "http://localhost:11434");
        ReflectionTestUtils.setField(ollamaConfig, "model", "llama2");
    }

    @Test
    public void testOllamaConfigProperties() {
        assertNotNull(ollamaConfig, "OllamaConfig должен быть автоматически загружен");
        assertEquals("http://localhost:11434", ollamaConfig.getApiUrl(), "URL API должен совпадать с заданным");
        assertEquals("llama2", ollamaConfig.getModel(), "Модель должна совпадать с заданной");
    }
}

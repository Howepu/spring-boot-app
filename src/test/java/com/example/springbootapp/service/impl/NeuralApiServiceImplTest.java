package com.example.springbootapp.service.impl;

import com.example.springbootapp.config.OllamaConfig;
import com.example.springbootapp.model.NeuralApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Тесты для проверки сервиса NeuralApiServiceImpl
 */
public class NeuralApiServiceImplTest {

    @Mock
    private OllamaConfig ollamaConfig;

    private NeuralApiServiceImpl neuralApiService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        // Настраиваем Mock OllamaConfig
        when(ollamaConfig.getApiUrl()).thenReturn("http://localhost:11434");
        when(ollamaConfig.getModel()).thenReturn("llama2");
        
        // Создаем сервис с мок-конфигурацией
        neuralApiService = new NeuralApiServiceImpl(ollamaConfig);
    }

    /**
     * Тест на правильное форматирование ответа нейросети
     */
    @Test
    public void testConvertResponseToInsightFormat() {
        // Создаем тестовый ответ от API
        NeuralApiResponse response = new NeuralApiResponse();
        response.setModel("llama2");
        response.setResponse("{\n  \"overview\": \"Тестовый обзор темы\",\n  \"keyConcepts\": [\"Концепция 1\", \"Концепция 2\"],\n  \"facts\": [\"Факт 1\", \"Факт 2\"],\n  \"applications\": \"Применение темы\"\n}");
        response.setDone(true);

        // Вызываем метод форматирования
        Map<String, Object> result = neuralApiService.convertResponseToInsightFormat(response);

        // Проверяем результат
        assertNotNull(result);
        assertEquals("Тестовый обзор темы", result.get("overview"));
        assertInstanceOf(List.class, result.get("keyConcepts"));
        assertInstanceOf(List.class, result.get("facts"));
        assertEquals("Применение темы", result.get("applications"));
        assertInstanceOf(List.class, result.get("relatedLinks"));
    }

    /**
     * Тест на резервный метод извлечения данных при неправильном формате ответа
     */
    @Test
    public void testFallbackParsing() {
        // Создаем тестовый ответ от API с неправильным JSON
        NeuralApiResponse response = new NeuralApiResponse();
        response.setModel("llama2");
        response.setResponse("Тестовый обзор темы без JSON формата.\n\nКлючевые понятия:\n- Понятие 1\n- Понятие 2");
        response.setDone(true);

        // Вызываем метод форматирования
        Map<String, Object> result = neuralApiService.convertResponseToInsightFormat(response);

        // Проверяем результат
        assertNotNull(result);
        assertTrue(result.containsKey("overview"));
        assertTrue(result.containsKey("keyConcepts"));
        assertTrue(result.containsKey("facts"));
        assertTrue(result.containsKey("applications"));
        assertTrue(result.containsKey("relatedLinks"));
    }
}

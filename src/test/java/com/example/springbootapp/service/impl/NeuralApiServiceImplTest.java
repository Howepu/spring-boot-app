package com.example.springbootapp.service.impl;

import com.example.springbootapp.config.OllamaConfig;
import com.example.springbootapp.model.NeuralApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Тесты для проверки сервиса NeuralApiServiceImpl
 */
public class NeuralApiServiceImplTest {

    @Mock
    private OllamaConfig ollamaConfig;

    private NeuralApiServiceImpl neuralApiService;
    
    @Mock
    private WebClient webClientMock;
    
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpecMock;
    
    @Mock
    private WebClient.RequestBodySpec requestBodySpecMock;
    
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;
    
    @Mock
    private WebClient.ResponseSpec responseSpecMock;

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
    
    /**
     * Тест на обработку ошибки в ответе от API
     */
    @Test
    public void testErrorHandlingInResponse() {
        // Создаем тестовый ответ с ошибкой
        NeuralApiResponse response = new NeuralApiResponse();
        response.setModel("llama2");
        response.setError("Тестовая ошибка от API");
        response.setDone(true);
        
        // Вызываем метод форматирования
        Map<String, Object> result = neuralApiService.convertResponseToInsightFormat(response);
        
        // Проверяем результат
        assertTrue((Boolean) result.get("error"));
        assertTrue(((String) result.get("message")).contains("Тестовая ошибка от API"));
    }
    
    /**
     * Тест для проверки корректности промпта
     */
    @Test
    public void testPromptBuilding() {
        // Вызываем приватный метод buildPrompt через рефлексию
        String prompt = null;
        try {
            java.lang.reflect.Method method = NeuralApiServiceImpl.class.getDeclaredMethod("buildPrompt", String.class);
            method.setAccessible(true);
            prompt = (String) method.invoke(neuralApiService, "тестовая тема");
        } catch (Exception e) {
            fail("Не удалось вызвать метод buildPrompt: " + e.getMessage());
        }
        
        // Проверяем содержимое промпта
        assertNotNull(prompt);
        assertTrue(prompt.contains("тестовая тема"));
        assertTrue(prompt.contains("JSON"));
        assertTrue(prompt.contains("overview"));
        assertTrue(prompt.contains("keyConcepts"));
        assertTrue(prompt.contains("facts"));
        assertTrue(prompt.contains("applications"));
    }
    
    /**
     * Тест для проверки извлечения JSON из ответа
     */
    @Test
    public void testJsonExtraction() {
        String testContent = "Вот ответ на ваш вопрос:\n```json\n{\"key\":\"value\"}\n```\nНадеюсь, это помогло!";
        String extracted = null;
        
        try {
            java.lang.reflect.Method method = NeuralApiServiceImpl.class.getDeclaredMethod("extractJsonFromResponse", String.class);
            method.setAccessible(true);
            extracted = (String) method.invoke(neuralApiService, testContent);
        } catch (Exception e) {
            fail("Не удалось вызвать метод extractJsonFromResponse: " + e.getMessage());
        }
        
        // Проверяем извлеченный JSON
        assertNotNull(extracted);
        assertEquals("{\"key\":\"value\"}", extracted);
    }
    
    /**
     * Тест для проверки поведения при null значениях конфигурации
     */
    @Test
    public void testNullConfigValues() {
        // Подготавливаем конфигурацию с null значениями
        when(ollamaConfig.getApiUrl()).thenReturn(null);
        when(ollamaConfig.getModel()).thenReturn(null);
        
        // Создаем новый экземпляр сервиса с такой конфигурацией
        NeuralApiServiceImpl serviceWithNullConfig = new NeuralApiServiceImpl(ollamaConfig);
        
        // Проверяем, что сервис был создан и не выбросил исключение
        assertNotNull(serviceWithNullConfig);
    }
}

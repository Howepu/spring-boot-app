package com.example.springbootapp.service.impl;

import com.example.springbootapp.model.NeuralApiResponse;
import com.example.springbootapp.service.NeuralApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Модульные тесты для проверки функционирования сервиса InsightServiceImpl
 */
public class InsightServiceImplTest {

    // Mock для NeuralApiService
    @Mock
    private NeuralApiService neuralApiService;
    
    private InsightServiceImpl insightService;

    @BeforeEach
    public void setup() {
        // Инициализация моков с использованием MockitoAnnotations
        MockitoAnnotations.openMocks(this);
        
        // Создаем обычный экземпляр сервиса с mock-зависимостью, без использования spy
        insightService = new InsightServiceImpl(neuralApiService);
        
        // Настраиваем поведение mock-объекта NeuralApiService для обычных запросов
        NeuralApiResponse mockResponse = new NeuralApiResponse();
        mockResponse.setModel("test-model");
        mockResponse.setResponse("Тестовый ответ для темы");
        mockResponse.setDone(true);
        
        when(neuralApiService.requestInsightsFromApi(anyString()))
            .thenReturn(Mono.just(mockResponse));
            
        Map<String, Object> mockFormattedResponse = new HashMap<>();
        mockFormattedResponse.put("overview", "Обзор темы: тестовая тема");
        
        List<String> concepts = new ArrayList<>();
        concepts.add("Ключевое понятие 1 для темы тестовая тема");
        concepts.add("Ключевое понятие 2 для темы тестовая тема");
        mockFormattedResponse.put("keyConcepts", concepts);
        
        List<Map<String, String>> links = new ArrayList<>();
        Map<String, String> link = new HashMap<>();
        link.put("title", "Тестовая ссылка");
        link.put("url", "https://example.com/test");
        links.add(link);
        mockFormattedResponse.put("relatedLinks", links);
        
        when(neuralApiService.convertResponseToInsightFormat(any(NeuralApiResponse.class)))
            .thenReturn(mockFormattedResponse);
    }

    /**
     * Тест на успешный сценарий получения данных по теме
     */
    @Test
    public void testGetInsightsForTopic_Success() {
        // Подготавливаем данные для теста
        String topic = "тестовая тема";
        
        // Вызываем метод, который тестируем
        Map<String, Object> result = insightService.getInsightsForTopic(topic);
        
        // Проверяем результат
        assertNotNull(result, "Результат не должен быть null");
        
        // Проверяем наличие всех необходимых полей в ответе
        assertTrue(result.containsKey("overview"), "Результат должен содержать поле overview");
        assertTrue(result.containsKey("keyConcepts"), "Результат должен содержать поле keyConcepts");
        assertTrue(result.containsKey("relatedLinks"), "Результат должен содержать поле relatedLinks");
        
        // Проверяем значения полей
        assertNotNull(result.get("overview"), "Поле overview не должно быть null");
        assertTrue(((String) result.get("overview")).contains(topic), 
                "Обзор должен содержать запрошенную тему");
        
        // Проверяем список ключевых концепций
        assertTrue(result.get("keyConcepts") instanceof List, "keyConcepts должен быть списком");
        List<?> keyConcepts = (List<?>) result.get("keyConcepts");
        assertFalse(keyConcepts.isEmpty(), "Список ключевых концепций не должен быть пустым");
        
        // Проверяем список связанных ссылок
        assertTrue(result.get("relatedLinks") instanceof List, "relatedLinks должен быть списком");
        List<?> relatedLinks = (List<?>) result.get("relatedLinks");
        assertFalse(relatedLinks.isEmpty(), "Список связанных ссылок не должен быть пустым");
        
        // Проверяем содержимое первой связанной ссылки
        Map<?, ?> firstLink = (Map<?, ?>) relatedLinks.get(0);
        assertTrue(firstLink.containsKey("title"), "Ссылка должна содержать заголовок");
        assertTrue(firstLink.containsKey("url"), "Ссылка должна содержать URL");
    }
    
    /**
     * Тест на проверку обработки пустой темы
     */
    @Test
    public void testGetInsightsForTopic_EmptyTopic() {
        // Настраиваем поведение mock-объекта для пустой темы
        when(neuralApiService.requestInsightsFromApi(""))
            .thenReturn(Mono.error(new IllegalArgumentException("Тема не может быть пустой")));
        
        // Подготавливаем ответ для пустой темы
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", "Ошибка при обработке запроса к нейросети: Тема не может быть пустой");
        when(neuralApiService.convertResponseToInsightFormat(any(NeuralApiResponse.class)))
            .thenReturn(errorResponse);
            
        // Вызываем метод и проверяем результат
        Map<String, Object> result = insightService.getInsightsForTopic("");
        
        // Проверяем что результат содержит информацию об ошибке
        assertNotNull(result, "Результат не должен быть null");
        assertTrue(result.containsKey("error"), "Результат должен содержать поле error");
        assertEquals(true, result.get("error"), "Поле error должно быть true");
        assertTrue(result.containsKey("message"), "Результат должен содержать поле message");
        assertTrue(((String) result.get("message")).contains("Тема не может быть пустой"), 
                "Сообщение об ошибке должно содержать причину");
    }
    
    /**
     * Тест на проверку обработки таймаута внешнего API
     */
    @Test
    public void testGetInsightsForTopic_ApiTimeout() {
        // Создаем мок ответа с ошибкой
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", "Ошибка при обработке запроса к нейросети: API timeout");
        
        // Настраиваем мок NeuralApiResponse с ошибкой
        NeuralApiResponse errorApiResponse = new NeuralApiResponse();
        errorApiResponse.setError("API timeout");
        
        // Настраиваем поведение mock-объекта для темы, вызывающей таймаут
        when(neuralApiService.requestInsightsFromApi(eq("таймаут-тема")))
            .thenReturn(Mono.just(errorApiResponse));
        
        // Настраиваем поведение convertResponseToInsightFormat для ответа с ошибкой
        when(neuralApiService.convertResponseToInsightFormat(any(NeuralApiResponse.class)))
            .thenReturn(errorResponse);
        
        // Вызываем метод для получения результата
        Map<String, Object> result = insightService.getInsightsForTopic("таймаут-тема");
        
        // Проверяем что результат содержит информацию об ошибке
        assertNotNull(result, "Результат не должен быть null");
        assertTrue(result.containsKey("error"), "Результат должен содержать поле error");
        assertEquals(true, result.get("error"), "Поле error должно быть true");
        assertTrue(result.containsKey("message"), "Результат должен содержать поле message");
        assertTrue(result.get("message").toString().contains("API timeout"), "Сообщение об ошибке должно содержать текст о таймауте");
        assertTrue(result.containsKey("message"), "Результат должен содержать поле message");
        assertTrue(((String) result.get("message")).contains("API timeout"), 
                "Сообщение об ошибке должно содержать причину");
    }
    
    /**
     * Тест для проверки корректности вызовов методов
     */
    @Test
    public void testGetInsightsForTopic_MethodCalls() {
        // Подготавливаем данные для теста
        String testTopic = "метод-тема";
        
        // Вызываем метод, который тестируем
        Map<String, Object> result = insightService.getInsightsForTopic(testTopic);
        
        // Проверяем вызов нейросети через мок NeuralApiService
        verify(neuralApiService, times(1)).requestInsightsFromApi(eq(testTopic));
        
        // Проверяем наличие результата
        assertNotNull(result, "Результат не должен быть null");
    }
}

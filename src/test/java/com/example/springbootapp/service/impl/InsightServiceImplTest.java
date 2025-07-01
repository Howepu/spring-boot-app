package com.example.springbootapp.service.impl;

import com.example.springbootapp.model.NeuralApiResponse;
import com.example.springbootapp.service.NeuralApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
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
    
    // Используем Spy для частичного мокирования реального сервиса
    @Spy
    private InsightServiceImpl insightService;

    @BeforeEach
    public void setup() {
        // Инициализация моков
        MockitoAnnotations.openMocks(this);
        
        // Создаем экземпляр сервиса с mock-зависимостью
        insightService = new InsightServiceImpl(neuralApiService);
        
        // Настраиваем поведение mock-объекта NeuralApiService
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
            .thenThrow(new IllegalArgumentException("Тема не может быть пустой"));
            
        // Проверяем что исключение выбрасывается
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            insightService.getInsightsForTopic("");
        });
        
        assertEquals("Тема не может быть пустой", exception.getMessage());
    }
    
    /**
     * Тест на проверку обработки таймаута внешнего API
     */
    @Test
    public void testGetInsightsForTopic_ApiTimeout() {
        // Настраиваем поведение mock-объекта для темы, вызывающей таймаут
        when(neuralApiService.requestInsightsFromApi("таймаут-тема"))
            .thenReturn(Mono.error(new RuntimeException("API timeout")));
            
        // Проверяем что исключение корректно обрабатывается
        Map<String, Object> result = insightService.getInsightsForTopic("таймаут-тема");
        
        // Проверяем что результат содержит информацию об ошибке
        assertTrue((Boolean)result.get("error"), "Результат должен содержать флаг ошибки");
        assertTrue(((String)result.get("message")).contains("API timeout"), 
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
        insightService.getInsightsForTopic(testTopic);
        
        // Проверяем, что метод был вызван с правильными параметрами
        verify(insightService, times(1)).getInsightsForTopic(eq(testTopic));
    }
}

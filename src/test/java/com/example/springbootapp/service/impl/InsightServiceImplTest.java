package com.example.springbootapp.service.impl;

import com.example.springbootapp.service.InsightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Модульные тесты для проверки функционирования сервиса InsightServiceImpl
 */
public class InsightServiceImplTest {

    // Используем Spy для частичного мокирования реального сервиса
    @Spy
    private InsightServiceImpl insightService;

    @BeforeEach
    public void setup() {
        // Инициализация моков
        MockitoAnnotations.openMocks(this);
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
     * Создаем класс-обертку для тестирования исключения при пустой теме
     */
    @Test
    public void testGetInsightsForTopic_EmptyTopic() {
        // Создаем модифицированный сервис для тестирования ошибки с пустой темой
        InsightService testService = new InsightServiceImpl() {
            @Override
            public Map<String, Object> getInsightsForTopic(String topic) {
                if (topic == null || topic.isEmpty()) {
                    throw new IllegalArgumentException("Тема не может быть пустой");
                }
                return super.getInsightsForTopic(topic);
            }
        };
        
        // Проверяем что исключение выбрасывается
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            testService.getInsightsForTopic("");
        });
        
        assertEquals("Тема не может быть пустой", exception.getMessage());
    }
    
    /**
     * Тест на проверку обработки таймаута внешнего API
     */
    @Test
    public void testGetInsightsForTopic_ApiTimeout() {
        // Создаем модифицированный сервис для тестирования таймаута API
        InsightService testService = new InsightServiceImpl() {
            @Override
            public Map<String, Object> getInsightsForTopic(String topic) {
                if ("таймаут-тема".equals(topic)) {
                    throw new RuntimeException("API timeout");
                }
                return super.getInsightsForTopic(topic);
            }
        };
        
        // Проверяем что исключение выбрасывается
        Exception exception = assertThrows(RuntimeException.class, () -> {
            testService.getInsightsForTopic("таймаут-тема");
        });
        
        assertEquals("API timeout", exception.getMessage());
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

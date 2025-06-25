package com.example.springbootapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для обработки запросов к ИИ API для получения аналитических данных по заданной теме
 */
@RestController
@RequestMapping("/api/insights")
public class InsightController {

    /**
     * Обрабатывает POST-запрос для получения аналитических данных по указанной теме
     * 
     * @param requestBody тело запроса, содержащее поле topic с темой для анализа
     * @return ResponseEntity с JSON, содержащим обзор, ключевые понятия и связанные ссылки
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> getInsights(@RequestBody Map<String, String> requestBody) {
        try {
            // Проверка наличия обязательного поля
            if (!requestBody.containsKey("topic") || requestBody.get("topic").isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Не указана тема для анализа"));
            }
            
            String topic = requestBody.get("topic");
            
            // Вызов внешнего ИИ API (заглушка)
            Map<String, Object> aiResponse = callExternalAiApi(topic);
            
            return ResponseEntity.ok(aiResponse);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Произошла ошибка при обработке запроса: " + e.getMessage()));
        }
    }
    
    /**
     * Заглушка для вызова внешнего ИИ API
     * 
     * @param topic тема для анализа
     * @return карта с результатами анализа
     */
    private Map<String, Object> callExternalAiApi(String topic) {
        // Здесь должен быть реальный вызов ИИ API
        // Пока просто возвращаем заглушку с данными
        
        Map<String, Object> response = new HashMap<>();
        
        // Добавляем обзор
        response.put("overview", "Это общий обзор темы '" + topic + "', сгенерированный заглушкой ИИ API. " +
                "В реальной реализации здесь будет содержательный текст, полученный от ИИ.");
        
        // Добавляем ключевые понятия
        List<String> keyConcepts = new ArrayList<>();
        keyConcepts.add("Ключевое понятие 1 для темы " + topic);
        keyConcepts.add("Ключевое понятие 2 для темы " + topic);
        keyConcepts.add("Ключевое понятие 3 для темы " + topic);
        response.put("keyConcepts", keyConcepts);
        
        // Добавляем связанные ссылки
        List<Map<String, String>> relatedLinks = new ArrayList<>();
        
        Map<String, String> link1 = new HashMap<>();
        link1.put("title", "Первая связанная ссылка для " + topic);
        link1.put("url", "https://example.com/resource1");
        relatedLinks.add(link1);
        
        Map<String, String> link2 = new HashMap<>();
        link2.put("title", "Вторая связанная ссылка для " + topic);
        link2.put("url", "https://example.com/resource2");
        relatedLinks.add(link2);
        
        response.put("relatedLinks", relatedLinks);
        
        return response;
    }
    
    /**
     * Создает стандартную структуру ответа с ошибкой
     * 
     * @param errorMessage сообщение об ошибке
     * @return карта с информацией об ошибке
     */
    private Map<String, Object> createErrorResponse(String errorMessage) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", errorMessage);
        return errorResponse;
    }
    
    /**
     * Глобальный обработчик исключений для контроллера
     * 
     * @param e перехваченное исключение
     * @return ResponseEntity с информацией об ошибке
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Необработанная ошибка сервера: " + e.getMessage()));
    }
}

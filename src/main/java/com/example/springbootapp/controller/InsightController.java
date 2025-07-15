package com.example.springbootapp.controller;

import com.example.springbootapp.service.InsightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер для обработки запросов к ИИ API для получения аналитических данных по заданной теме
 * Делегирует обработку запросов соответствующему сервису
 */
@RestController
@RequestMapping("/api/insights")
public class InsightController {

    private final InsightService insightService;
    
    /**
     * Конструктор с автоматическим внедрением зависимостей
     * 
     * @param insightService сервис для работы с ИИ API
     */
    @Autowired
    public InsightController(InsightService insightService) {
        this.insightService = insightService;
    }

    /**
     * Обрабатывает POST-запрос для получения аналитических данных по указанной теме
     * Проверяет входные данные и делегирует обработку сервису InsightService
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
            
            // Вызов сервиса для получения данных от ИИ API
            Map<String, Object> aiResponse = insightService.getInsightsForTopic(topic);
            
            return ResponseEntity.ok(aiResponse);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Произошла ошибка при обработке запроса: " + e.getMessage()));
        }
    }
    
    /**
     * Обрабатывает POST-запрос для генерации инсайтов по указанной теме с дополнительными параметрами
     * Поддерживает структуру запроса, отправляемую с клиентской стороны
     * 
     * @param requestBody тело запроса, содержащее тему и параметры генерации
     * @return ResponseEntity с JSON, содержащим результат генерации
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateInsights(@RequestBody Map<String, Object> requestBody) {
        try {
            // Проверка наличия обязательного поля
            if (!requestBody.containsKey("topic") || requestBody.get("topic") == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Не указана тема для анализа"));
            }
            
            String topic = requestBody.get("topic").toString();
            
            // Извлечение параметров, если есть (не используются в текущей реализации, но могут быть добавлены)
            @SuppressWarnings("unchecked")
            Map<String, Object> parameters = requestBody.containsKey("parameters") ? 
                                           (Map<String, Object>) requestBody.get("parameters") : 
                                           new HashMap<>();
            
            // Вызов сервиса для получения данных от ИИ API
            Map<String, Object> aiResponse = insightService.getInsightsForTopic(topic);
            
            return ResponseEntity.ok(aiResponse);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Произошла ошибка при обработке запроса: " + e.getMessage()));
        }
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

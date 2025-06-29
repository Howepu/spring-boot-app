package com.example.springbootapp.service.impl;

import com.example.springbootapp.service.InsightService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Реализация сервиса для взаимодействия с внешним ИИ API
 * В текущей реализации используется заглушка вместо реального API
 */
@Service
public class InsightServiceImpl implements InsightService {

    /**
     * Получает аналитические данные по указанной теме через внешний ИИ API
     * В текущей реализации создаёт тестовые данные вместо вызова реального API
     * 
     * @param topic тема для анализа
     * @return карта, содержащая обзор, ключевые понятия и связанные ссылки
     */
    @Override
    public Map<String, Object> getInsightsForTopic(String topic) {
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
}

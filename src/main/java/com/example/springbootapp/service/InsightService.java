package com.example.springbootapp.service;

import java.util.Map;

/**
 * Сервис для взаимодействия с внешним ИИ API
 * Предоставляет методы для получения аналитических данных по заданной теме
 */
public interface InsightService {
    
    /**
     * Получает аналитические данные по указанной теме через внешний ИИ API
     * 
     * @param topic тема для анализа
     * @return карта, содержащая обзор, ключевые понятия и связанные ссылки
     */
    Map<String, Object> getInsightsForTopic(String topic);
}

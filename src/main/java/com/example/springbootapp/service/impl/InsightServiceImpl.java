package com.example.springbootapp.service.impl;

import com.example.springbootapp.model.NeuralApiResponse;
import com.example.springbootapp.service.InsightService;
import com.example.springbootapp.service.NeuralApiService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Реализация сервиса для взаимодействия с внешним ИИ API
 * В текущей реализации используется заглушка вместо реального API
 */
@Service
public class InsightServiceImpl implements InsightService {

    private final NeuralApiService neuralApiService;
    
    /**
     * Конструктор с автоматическим внедрением зависимостей
     * 
     * @param neuralApiService сервис для работы с API нейросети
     */
    public InsightServiceImpl(NeuralApiService neuralApiService) {
        this.neuralApiService = neuralApiService;
    }

    /**
     * Получает аналитические данные по указанной теме через API нейросети Ollama
     * 
     * @param topic тема для анализа
     * @return карта, содержащая обзор, ключевые понятия и связанные ссылки
     */
    @Override
    public Map<String, Object> getInsightsForTopic(String topic) {
        try {
            // Вызываем Ollama API и блокируем поток до получения ответа
            NeuralApiResponse response = neuralApiService.requestInsightsFromApi(topic)
                .onErrorResume(e -> {
                    NeuralApiResponse errorResponse = new NeuralApiResponse();
                    errorResponse.setError("Ошибка при обращении к API: " + e.getMessage());
                    return Mono.just(errorResponse);
                })
                .block();
            
            // Преобразуем ответ нейросети в ожидаемый клиентом формат
            return neuralApiService.convertResponseToInsightFormat(response);
        } catch (Exception e) {
            // Если произошла ошибка, возвращаем информацию об ошибке
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "Ошибка при обработке запроса к нейросети: " + e.getMessage());
            return errorResponse;
        }
    }
}

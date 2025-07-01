package com.example.springbootapp.service;

import com.example.springbootapp.model.NeuralApiResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Сервис для взаимодействия с API нейросети
 */
public interface NeuralApiService {
    
    /**
     * Отправляет запрос к API нейросети и получает структурированный ответ
     * 
     * @param topic тема для анализа
     * @return Mono с ответом от нейросети
     */
    Mono<NeuralApiResponse> requestInsightsFromApi(String topic);
    
    /**
     * Преобразует ответ нейросети в формат, ожидаемый клиентами
     * 
     * @param response ответ от нейросети
     * @return карта с данными в ожидаемом формате
     */
    Map<String, Object> convertResponseToInsightFormat(NeuralApiResponse response);
}

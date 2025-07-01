package com.example.springbootapp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель ответа от API Ollama
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NeuralApiResponse {
    private String model;
    private String response;
    private boolean done;
    private String error;
    
    // Дополнительные поля для структурированного ответа
    private String overview;
    private String[] keyConcepts;
    private LinkInfo[] relatedLinks;
    
    /**
     * Модель для представления ссылок
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LinkInfo {
        private String title;
        private String url;
    }
}

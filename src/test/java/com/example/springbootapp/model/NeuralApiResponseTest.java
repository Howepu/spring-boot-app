package com.example.springbootapp.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тест для проверки модели NeuralApiResponse
 */
public class NeuralApiResponseTest {

    @Test
    public void testNeuralApiResponseProperties() {
        // Создание тестового объекта
        NeuralApiResponse response = new NeuralApiResponse();
        
        // Установка значений
        response.setModel("test-model");
        response.setResponse("Test response content");
        response.setDone(true);
        
        // Проверка значений
        assertEquals("test-model", response.getModel());
        assertEquals("Test response content", response.getResponse());
        assertTrue(response.isDone());
        assertNull(response.getError());
    }
    
    @Test
    public void testNeuralApiResponseError() {
        // Создание тестового объекта с ошибкой
        NeuralApiResponse response = new NeuralApiResponse();
        
        // Установка значений
        response.setError("Test error message");
        
        // Проверка значений
        assertNotNull(response.getError());
        assertEquals("Test error message", response.getError());
    }
    
    @Test
    public void testLinkInfoClass() {
        // Тестирование вложенного класса LinkInfo
        NeuralApiResponse.LinkInfo link = new NeuralApiResponse.LinkInfo();
        link.setTitle("Test Link");
        link.setUrl("http://example.com");
        
        assertEquals("Test Link", link.getTitle());
        assertEquals("http://example.com", link.getUrl());
    }
    
    @Test
    public void testAllArgsConstructor() {
        // Тестирование конструктора со всеми аргументами
        String model = "gpt-model";
        String response = "GPT response";
        boolean done = true;
        String error = null;
        String overview = "Test overview";
        String[] concepts = {"concept1", "concept2"};
        NeuralApiResponse.LinkInfo[] links = {
            new NeuralApiResponse.LinkInfo("Link 1", "http://link1.com"),
            new NeuralApiResponse.LinkInfo("Link 2", "http://link2.com")
        };
        
        NeuralApiResponse apiResponse = new NeuralApiResponse(model, response, done, error, overview, concepts, links);
        
        assertEquals(model, apiResponse.getModel());
        assertEquals(response, apiResponse.getResponse());
        assertTrue(apiResponse.isDone());
        assertNull(apiResponse.getError());
        assertEquals(overview, apiResponse.getOverview());
        assertEquals(concepts, apiResponse.getKeyConcepts());
        assertEquals(links, apiResponse.getRelatedLinks());
        assertEquals(2, apiResponse.getRelatedLinks().length);
    }
}

package com.example.springbootapp.controller;

import com.example.springbootapp.service.InsightService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Модульные тесты для InsightController
 */
public class InsightControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InsightService insightService;

    @InjectMocks
    private InsightController insightController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(insightController).build();
    }

    /**
     * Тест успешного получения инсайтов по теме
     */
    @Test
    public void testGetInsights_Success() throws Exception {
        // Подготавливаем тестовые данные
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("topic", "тестовая тема");

        // Настраиваем поведение мока
        Map<String, Object> serviceResponse = prepareSuccessServiceResponse();
        when(insightService.getInsightsForTopic(anyString())).thenReturn(serviceResponse);

        // Выполняем запрос и проверяем ответ
        mockMvc.perform(post("/api/insights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overview", is("Обзор темы: тестовая тема")))
                .andExpect(jsonPath("$.keyConcepts", hasSize(2)))
                .andExpect(jsonPath("$.relatedLinks", hasSize(1)));
    }

    /**
     * Тест для проверки обработки отсутствующей темы в запросе
     */
    @Test
    public void testGetInsights_MissingTopic() throws Exception {
        // Пустое тело запроса
        Map<String, String> requestBody = new HashMap<>();

        // Выполняем запрос и проверяем ответ
        mockMvc.perform(post("/api/insights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(true)));
    }

    /**
     * Тест для проверки обработки пустой темы в запросе
     */
    @Test
    public void testGetInsights_EmptyTopic() throws Exception {
        // Тело запроса с пустой темой
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("topic", "");

        // Выполняем запрос и проверяем ответ
        mockMvc.perform(post("/api/insights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is(true)));
    }

    /**
     * Тест для проверки обработки внутренней ошибки сервера
     */
    @Test
    public void testGetInsights_ServerError() throws Exception {
        // Подготавливаем тестовые данные
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("topic", "ошибочная тема");

        // Настраиваем поведение мока для имитации ошибки
        when(insightService.getInsightsForTopic(anyString())).thenThrow(new RuntimeException("Тестовая ошибка"));

        // Выполняем запрос и проверяем ответ
        mockMvc.perform(post("/api/insights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is(true)))
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Подготавливает успешный ответ от сервиса для тестов
     */
    private Map<String, Object> prepareSuccessServiceResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("overview", "Обзор темы: тестовая тема");

        List<String> concepts = new ArrayList<>();
        concepts.add("Ключевое понятие 1 для темы тестовая тема");
        concepts.add("Ключевое понятие 2 для темы тестовая тема");
        response.put("keyConcepts", concepts);

        List<Map<String, String>> links = new ArrayList<>();
        Map<String, String> link = new HashMap<>();
        link.put("title", "Тестовая ссылка");
        link.put("url", "https://example.com/test");
        links.add(link);
        response.put("relatedLinks", links);

        return response;
    }
}

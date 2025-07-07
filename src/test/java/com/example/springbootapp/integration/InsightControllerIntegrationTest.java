package com.example.springbootapp.integration;

import com.example.springbootapp.SpringBootAppApplication;
import com.example.springbootapp.service.InsightService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционные тесты для InsightController
 * Проверяют работу контроллера вместе с реальными сервисами в контексте Spring
 */
@SpringBootTest(classes = SpringBootAppApplication.class)
@AutoConfigureMockMvc
public class InsightControllerIntegrationTest {

    @MockBean
    private InsightService insightService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public WebClient.Builder webClientBuilder() {
            WebClient.Builder builder = mock(WebClient.Builder.class);
            WebClient webClientMock = mock(WebClient.class);
            WebClient.RequestBodyUriSpec requestBodyUriSpecMock = mock(WebClient.RequestBodyUriSpec.class);
            WebClient.RequestBodySpec requestBodySpecMock = mock(WebClient.RequestBodySpec.class);
            WebClient.RequestHeadersSpec requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
            WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);
            
            // Настройка цепочки моков
            when(builder.baseUrl(anyString())).thenReturn(builder);
            when(builder.build()).thenReturn(webClientMock);
            when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
            when(requestBodyUriSpecMock.uri(anyString())).thenReturn(requestBodySpecMock);
            when(requestBodySpecMock.contentType(any())).thenReturn(requestBodySpecMock);
            when(requestBodySpecMock.bodyValue(any())).thenReturn(requestHeadersSpecMock);
            when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
            
            // Настройка успешного ответа по умолчанию
            Map<String, Object> aiResponse = new HashMap<>();
            aiResponse.put("model", "test-model");
            aiResponse.put("response", "{\"overview\":\"Интеграционный тестовый ответ\",\"keyConcepts\":[\"Понятие 1\",\"Понятие 2\"],\"facts\":[\"Факт 1\"]}");
            aiResponse.put("done", true);
            when(responseSpecMock.bodyToMono(any(Class.class))).thenReturn(Mono.just(aiResponse));
            
            return builder;
        }
        
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @BeforeEach
    public void setup() {
        // Настройка успешного ответа от сервиса по умолчанию
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("overview", "Интеграционный тестовый ответ");
        successResponse.put("keyConcepts", new String[] {"Понятие 1", "Понятие 2"});
        successResponse.put("facts", new String[] {"Факт 1"});
        when(insightService.getInsightsForTopic(anyString())).thenReturn(successResponse);
    }
    
    /**
     * Интеграционный тест для проверки полного цикла запроса на получение инсайтов
     */
    @Test
    public void testFullInsightRequestCycle() throws Exception {
        // Создаем тело запроса
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("topic", "интеграционный тест");

        // Выполняем запрос и проверяем результаты
        mockMvc.perform(post("/api/insights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overview").exists());
    }

    /**
     * Тест для проверки обработки ошибки в полном цикле
     */
    @Test
    public void testFullInsightRequestCycle_Error() throws Exception {
        // Настраиваем возвращение ошибки для конкретной темы
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", true);
        errorResponse.put("message", "API недоступно");
        when(insightService.getInsightsForTopic("ошибка интеграции")).thenReturn(errorResponse);

        // Создаем тело запроса
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("topic", "ошибка интеграции");

        // Выполняем запрос и проверяем результаты обработки ошибки
        mockMvc.perform(post("/api/insights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value(true));
    }
}

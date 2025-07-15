package com.example.springbootapp.service.impl;

import com.example.springbootapp.config.OllamaConfig;
import com.example.springbootapp.model.NeuralApiResponse;
import com.example.springbootapp.service.NeuralApiService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Реализация сервиса для взаимодействия с API нейросети Ollama
 */
@Service
public class NeuralApiServiceImpl implements NeuralApiService {

    private final WebClient webClient;
    private final OllamaConfig ollamaConfig;
    private String baseApiUrl;

    public NeuralApiServiceImpl(OllamaConfig ollamaConfig) {
        this.ollamaConfig = ollamaConfig;
        
        String apiUrl = ollamaConfig.getApiUrl();
        System.out.println("Initializing NeuralApiService with API URL: " + apiUrl);
        
        if (apiUrl == null || apiUrl.isEmpty()) {
            // Установка URL по умолчанию, если он не загрузился из конфигурации
            apiUrl = "http://localhost:11434";
            System.out.println("WARNING: API URL is null or empty, using default: " + apiUrl);
        }
        
        // Убедимся, что apiUrl содержит полный URL с протоколом
        if (!apiUrl.startsWith("http://") && !apiUrl.startsWith("https://")) {
            apiUrl = "http://" + apiUrl;
            System.out.println("Adding protocol to API URL: " + apiUrl);
        }
        
        this.baseApiUrl = apiUrl;
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .build();
    }

    @Override
    public Mono<NeuralApiResponse> requestInsightsFromApi(String topic) {
        String model = ollamaConfig.getModel();
        if (model == null || model.isEmpty()) {
            model = "llama2";
            System.out.println("WARNING: Model name is null or empty, using default: " + model);
        }
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("prompt", buildPrompt(topic));
        requestBody.put("stream", false);
        
        System.out.println("Отправка запроса к Ollama API: " + this.baseApiUrl + "/api/generate");
        System.out.println("Используемая модель: " + model);
        
        return webClient.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(NeuralApiResponse.class)
                .doOnSuccess(response -> {
                    System.out.println("Успешно получен ответ от Ollama API");
                    System.out.println("Ответ: " + (response.getResponse() != null ? 
                        (response.getResponse().length() > 100 ? 
                            response.getResponse().substring(0, 100) + "..." : 
                            response.getResponse()) : 
                        "null"));
                })
                .doOnError(error -> {
                    System.err.println("Ошибка при запросе к Ollama API: " + error.getMessage());
                    error.printStackTrace();
                });
    }

    @Override
    public Map<String, Object> convertResponseToInsightFormat(NeuralApiResponse response) {
        Map<String, Object> result = new HashMap<>();
        
        if (response.getError() != null) {
            result.put("error", true);
            result.put("message", "Ошибка нейросети: " + response.getError());
            return result;
        }

        // Обработка ответа от нейросети
        String content = response.getResponse();
        System.out.println("Преобразование ответа нейросети: " + (content != null ? 
            (content.length() > 100 ? content.substring(0, 100) + "..." : content) : "null"));
        
        // Парсим ответ и получаем структурированные данные
        Map<String, Object> parsedData = parseNeuralResponse(content);
        
        if (parsedData.isEmpty() && content != null && !content.isEmpty()) {
            // Если не удалось распарсить JSON, пытаемся извлечь его из текста
            try {
                String jsonStr = extractJsonFromResponse(content);
                if (jsonStr != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    parsedData = mapper.readValue(jsonStr, new TypeReference<Map<String, Object>>() {});
                    System.out.println("Успешно извлечен и распарсен JSON из ответа");
                }
            } catch (Exception e) {
                System.err.println("Ошибка при извлечении JSON из ответа: " + e.getMessage());
                // Если не удалось распарсить JSON, используем весь ответ как overview
                parsedData.put("overview", content);
            }
        }
        
        // Если после всех попыток данные всё равно пустые, создаем базовую структуру
        if (parsedData.isEmpty() && content != null) {
            parsedData.put("overview", content);
        }
        
        // Копируем данные в результирующую структуру
        result.putAll(parsedData);
        
        // Добавляем связанные ссылки
        List<Map<String, String>> relatedLinks = new ArrayList<>();
        Map<String, String> link1 = new HashMap<>();
        link1.put("title", "Дополнительная информация о " + response.getModel());
        link1.put("url", "https://ollama.com/library/" + response.getModel());
        relatedLinks.add(link1);
        
        // Если есть приложения, добавляем ссылку на них
        if (parsedData.containsKey("applications")) {
            Map<String, String> link2 = new HashMap<>();
            link2.put("title", "Практические применения и примеры");
            link2.put("url", "https://example.com/search?q=примеры+" + 
                     URLEncoder.encode(parsedData.get("applications").toString().substring(0, 
                        Math.min(20, parsedData.get("applications").toString().length())), StandardCharsets.UTF_8));
            relatedLinks.add(link2);
        }
        
        result.put("relatedLinks", relatedLinks);
        
        return result;
    }

    /**
     * Создает структурированный промпт для запроса к нейросети
     * 
     * @param topic тема для анализа
     * @return промпт для нейросети
     */
    private String buildPrompt(String topic) {
        return "Ты - умный аналитический ассистент. Твоя задача - предоставить структурированный и информативный анализ на тему '" + topic + "'."
               + "\n\nОтвет должен быть в следующем формате JSON (важно придерживаться этого формата):\n"
               + "```json\n{"
               + "\n  \"overview\": \"Всесторонний обзор темы на 2-3 абзаца, охватывающий определение, историю, значимость и актуальность темы.\","
               + "\n  \"keyConcepts\": [\"Концепция 1 с кратким пояснением\", \"Концепция 2 с кратким пояснением\", ...],"
               + "\n  \"facts\": [\"Интересный факт 1\", \"Интересный факт 2\", ...],"
               + "\n  \"applications\": \"Описание практических применений или значимости темы в реальной жизни\""
               + "\n}\n```\n\n"
               + "Ответ должен быть точным, информативным и научно обоснованным. Используй современные данные."
               + " Не включай информацию, в которой не уверен.";
    }

    /**
     * Извлекает JSON из текстового ответа нейросети
     * Учитывает различные форматы ответа, включая вложенный JSON в текст
     * 
     * @param response текстовый ответ от нейросети
     * @return извлеченный JSON в виде строки или null, если не найден
     */
    private String extractJsonFromResponse(String response) {
        if (response == null || response.isEmpty()) {
            return null;
        }

        // Пытаемся найти JSON в обратных кавычках (как в markdown)
        Pattern jsonPattern = Pattern.compile("```(?:json)?([\\s\\S]*?)```");
        Matcher jsonMatcher = jsonPattern.matcher(response);

        if (jsonMatcher.find()) {
            String jsonContent = jsonMatcher.group(1).trim();
            if (jsonContent.startsWith("{") && jsonContent.endsWith("}")) {
                System.out.println("Извлечен JSON из markdown блока кода");
                return jsonContent;
            }
        }

        // Если JSON не найден в markdown блоке, ищем JSON между фигурными скобками
        Pattern bracesPattern = Pattern.compile("\\{[\\s\\S]*?\\}");
        Matcher bracesMatcher = bracesPattern.matcher(response);

        if (bracesMatcher.find()) {
            String jsonCandidate = bracesMatcher.group(0);
            try {
                // Проверяем, является ли найденная строка валидным JSON
                ObjectMapper mapper = new ObjectMapper();
                mapper.readTree(jsonCandidate);
                System.out.println("Извлечен JSON между фигурными скобками");
                return jsonCandidate;
            } catch (Exception e) {
                System.out.println("Найденная строка не является валидным JSON: " + e.getMessage());
            }
        }

        return null;
    }

    /**
     * Парсит ответ нейросети и преобразует его в структурированный формат
     *
     * @param content ответ от нейросети
     * @return структурированный ответ в виде Map
     */
    private Map<String, Object> parseNeuralResponse(String content) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Извлекаем JSON из ответа
            String jsonContent = extractJsonFromResponse(content);
            
            if (jsonContent != null) {
                // Если нашли JSON, пробуем его распарсить
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(jsonContent);
                
                // Извлекаем поля из JSON
                if (rootNode.has("overview")) {
                    result.put("overview", rootNode.get("overview").asText());
                }
                
                if (rootNode.has("keyConcepts") && rootNode.get("keyConcepts").isArray()) {
                    List<String> concepts = new ArrayList<>();
                    for (JsonNode concept : rootNode.get("keyConcepts")) {
                        concepts.add(concept.asText());
                    }
                    result.put("keyConcepts", concepts);
                }
                
                if (rootNode.has("facts") && rootNode.get("facts").isArray()) {
                    List<String> facts = new ArrayList<>();
                    for (JsonNode fact : rootNode.get("facts")) {
                        facts.add(fact.asText());
                    }
                    result.put("facts", facts);
                }
                
                if (rootNode.has("applications")) {
                    result.put("applications", rootNode.get("applications").asText());
                }
                
                return result;
            }
        } catch (Exception e) {
            // При ошибке парсинга используем резервный метод извлечения
            System.err.println("Ошибка при парсинге JSON из ответа нейросети: " + e.getMessage());
        }
        
        // Если не удалось распарсить JSON, используем резервный метод
        return extractDataManually(content);
    }
    
    /**
     * Резервный метод для извлечения данных из неструктурированного ответа
     *
     * @param content ответ от нейросети
     * @return структурированный ответ в виде Map
     */
    private Map<String, Object> extractDataManually(String content) {
        Map<String, Object> result = new HashMap<>();
        
        // Извлекаем обзор - первый или два абзаца
        String[] paragraphs = content.split("\\n\\n|\n\n");
        if (paragraphs.length > 0) {
            if (paragraphs[0].length() < 100 && paragraphs.length > 1) {
                result.put("overview", paragraphs[0] + "\n\n" + paragraphs[1]);
            } else {
                result.put("overview", paragraphs[0]);
            }
        } else {
            result.put("overview", "Не удалось сгенерировать обзор по запрошенной теме.");
        }
        
        // Извлекаем ключевые понятия - ищем маркеры списков или просто берем предложения
        List<String> concepts = new ArrayList<>();
        Pattern listPattern = Pattern.compile("[•\\-\\*]\\s+(.+?)(?=\n|$)");
        Matcher listMatcher = listPattern.matcher(content);
        
        // Если нашли маркеры списков
        int count = 0;
        while (listMatcher.find() && count < 7) {
            String concept = listMatcher.group(1).trim();
            if (concept.length() > 10) {
                concepts.add(concept);
                count++;
            }
        }
        
        // Если списков не нашли, разбиваем на предложения
        if (concepts.isEmpty()) {
            String[] sentences = content.split("\\. ");
            count = 0;
            for (String sentence : sentences) {
                if (sentence.length() > 20 && sentence.length() < 120) {
                    concepts.add(sentence.trim() + ".");
                    count++;
                }
                if (count >= 5) break;
            }
        }
        
        // Если все еще пусто, добавляем заглушку
        if (concepts.isEmpty()) {
            concepts.add("Ключевые понятия не выявлены.");
        }
        
        result.put("keyConcepts", concepts);
        
        // Добавляем заглушку для фактов и применений
        List<String> facts = new ArrayList<>();
        facts.add("Интересные факты не выявлены.");
        result.put("facts", facts);
        
        result.put("applications", "Информация о практических применениях не выявлена.");
        
        return result;
    }
}

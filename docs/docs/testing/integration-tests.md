---
sidebar_position: 3
---

# Интеграционное тестирование

## Основы интеграционного тестирования в Spring Boot

Интеграционное тестирование проверяет взаимодействие между различными компонентами системы, чтобы убедиться, что они работают корректно вместе. В контексте Spring Boot приложения с Ollama API интеграцией, интеграционные тесты особенно важны для проверки взаимодействия между слоями приложения и внешними системами.

### Отличия от модульных тестов

| Аспект | Модульные тесты | Интеграционные тесты |
|--------|----------------|---------------------|
| Объект тестирования | Отдельные классы/методы | Взаимодействие компонентов |
| Использование моков | Активно используются | Минимальное использование |
| Скорость выполнения | Быстрые | Относительно медленные |
| Зависимости | Изолированы (заглушки) | Реальные или близкие к реальным |
| Стоимость поддержки | Низкая | Средняя |

### Типы интеграционных тестов в Spring Boot

1. **Spring Boot Test** - тестирование с загрузкой контекста Spring
2. **WebMVC Test** - тестирование REST контроллеров с использованием MockMvc
3. **REST Test** - тестирование с использованием TestRestTemplate или WebTestClient
4. **Data Test** - тестирование взаимодействия с базой данных
5. **External API Test** - тестирование взаимодействия с внешними API (Ollama API)

## Конфигурация для интеграционного тестирования

### Основные аннотации

```java
@SpringBootTest  // Загружает полный контекст Spring Boot приложения
@WebMvcTest      // Загружает только веб-слой (контроллеры и конфигурацию)
@DataJpaTest     // Загружает только слой доступа к данным (репозитории)
@RestClientTest  // Тестирование REST клиентов
@AutoConfigureMockMvc  // Настройка MockMvc для тестирования API
@TestPropertySource  // Указание тестовых свойств
@ActiveProfiles("test")  // Активация тестового профиля
```

### Пример конфигурации тестового профиля

Файл `src/test/resources/application-test.yml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: 
    driver-class-name: org.h2.Driver
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: true

ollama:
  api:
    url: http://localhost:11434
    timeout: 5000
    model: llama2
```

### Использование TestContainers

Для тестирования с реальными зависимостями (база данных, API) используется библиотека TestContainers:

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>${testcontainers.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>${testcontainers.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>${testcontainers.version}</version>
    <scope>test</scope>
</dependency>
```

## Тестирование REST API

### Тестирование с MockMvc

```java
@SpringBootTest
@AutoConfigureMockMvc
class InsightControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NeuralApiService neuralApiService;

    @Test
    void shouldCreateInsight() throws Exception {
        // Arrange
        InsightRequest request = new InsightRequest();
        request.setText("Тестовый запрос для анализа");
        request.setParameters(Map.of("temperature", 0.7));

        NeuralApiResponse apiResponse = new NeuralApiResponse();
        apiResponse.setResponse("Результат анализа");
        apiResponse.setModel("llama2");
        apiResponse.setProcessingTimeMs(1500L);

        when(neuralApiService.generateResponse(any(), any(), any()))
            .thenReturn(apiResponse);

        // Act & Assert
        mockMvc.perform(post("/api/insights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.text").value("Тестовый запрос для анализа"))
            .andExpect(jsonPath("$.result").value("Результат анализа"))
            .andExpect(jsonPath("$.modelUsed").value("llama2"))
            .andExpect(jsonPath("$.processingTimeMs").value(1500));

        verify(neuralApiService).generateResponse(
            eq("Тестовый запрос для анализа"),
            eq(Map.of("temperature", 0.7)),
            eq(null)
        );
    }

    @Test
    void shouldReturnBadRequestForInvalidRequest() throws Exception {
        // Arrange
        InsightRequest request = new InsightRequest();
        request.setText(""); // Пустой текст - невалидный запрос

        // Act & Assert
        mockMvc.perform(post("/api/insights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("Текст запроса не может быть пустым")));

        verifyNoInteractions(neuralApiService);
    }

    @Test
    void shouldGetInsightById() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        
        InsightDto insightDto = new InsightDto();
        insightDto.setId(id);
        insightDto.setText("Запрос");
        insightDto.setResult("Результат");
        insightDto.setModelUsed("llama2");
        insightDto.setTimestamp(Instant.now());

        when(insightService.getInsightById(id))
            .thenReturn(Optional.of(insightDto));

        // Act & Assert
        mockMvc.perform(get("/api/insights/{id}", id)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.text").value("Запрос"))
            .andExpect(jsonPath("$.result").value("Результат"))
            .andExpect(jsonPath("$.modelUsed").value("llama2"));

        verify(insightService).getInsightById(id);
    }

    @Test
    void shouldReturn404WhenInsightNotFound() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        
        when(insightService.getInsightById(id))
            .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/insights/{id}", id)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(insightService).getInsightById(id);
    }
}
```

### Тестирование с TestRestTemplate

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InsightApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private NeuralApiService neuralApiService;

    @Test
    void shouldCreateAndRetrieveInsight() {
        // Arrange
        InsightRequest request = new InsightRequest();
        request.setText("Тестовый запрос для анализа");
        request.setParameters(Map.of("temperature", 0.7));

        NeuralApiResponse apiResponse = new NeuralApiResponse();
        apiResponse.setResponse("Результат анализа");
        apiResponse.setModel("llama2");
        apiResponse.setProcessingTimeMs(1500L);

        when(neuralApiService.generateResponse(any(), any(), any()))
            .thenReturn(apiResponse);

        // Act - создаем инсайт
        ResponseEntity<InsightDto> createResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/insights",
            request,
            InsightDto.class
        );

        // Assert
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().getText()).isEqualTo("Тестовый запрос для анализа");
        assertThat(createResponse.getBody().getResult()).isEqualTo("Результат анализа");

        // Получаем созданный инсайт по ID
        UUID id = createResponse.getBody().getId();
        ResponseEntity<InsightDto> getResponse = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/insights/" + id,
            InsightDto.class
        );

        // Assert
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getId()).isEqualTo(id);
        assertThat(getResponse.getBody().getText()).isEqualTo("Тестовый запрос для анализа");
        assertThat(getResponse.getBody().getResult()).isEqualTo("Результат анализа");
    }
}
```

## Тестирование взаимодействия с базой данных

### Использование @DataJpaTest

```java
@DataJpaTest
class InsightRepositoryIntegrationTest {

    @Autowired
    private InsightRepository insightRepository;

    @Test
    void shouldSaveAndRetrieveInsight() {
        // Arrange
        Insight insight = new Insight();
        insight.setText("Тестовый запрос");
        insight.setResult("Результат анализа");
        insight.setModelUsed("llama2");
        insight.setProcessingTimeMs(1500L);
        insight.setTimestamp(Instant.now());

        // Act
        Insight savedInsight = insightRepository.save(insight);
        Optional<Insight> foundInsight = insightRepository.findById(savedInsight.getId());

        // Assert
        assertThat(foundInsight).isPresent();
        assertThat(foundInsight.get().getText()).isEqualTo("Тестовый запрос");
        assertThat(foundInsight.get().getResult()).isEqualTo("Результат анализа");
        assertThat(foundInsight.get().getModelUsed()).isEqualTo("llama2");
    }

    @Test
    void shouldFindInsightsByText() {
        // Arrange
        Insight insight1 = new Insight();
        insight1.setText("Погода в Москве");
        insight1.setResult("Результат 1");
        insightRepository.save(insight1);

        Insight insight2 = new Insight();
        insight2.setText("Погода в Санкт-Петербурге");
        insight2.setResult("Результат 2");
        insightRepository.save(insight2);

        Insight insight3 = new Insight();
        insight3.setText("Курс валют");
        insight3.setResult("Результат 3");
        insightRepository.save(insight3);

        // Act
        List<Insight> foundInsights = insightRepository.findByTextContaining("Погода");

        // Assert
        assertThat(foundInsights).hasSize(2);
        assertThat(foundInsights.stream().map(Insight::getText))
            .containsExactlyInAnyOrder("Погода в Москве", "Погода в Санкт-Петербурге");
    }

    @Test
    void shouldFindInsightsByModelUsed() {
        // Arrange
        Insight insight1 = new Insight();
        insight1.setText("Запрос 1");
        insight1.setModelUsed("llama2");
        insightRepository.save(insight1);

        Insight insight2 = new Insight();
        insight2.setText("Запрос 2");
        insight2.setModelUsed("mistral");
        insightRepository.save(insight2);

        Insight insight3 = new Insight();
        insight3.setText("Запрос 3");
        insight3.setModelUsed("llama2");
        insightRepository.save(insight3);

        // Act
        List<Insight> foundInsights = insightRepository.findByModelUsed("llama2");

        // Assert
        assertThat(foundInsights).hasSize(2);
        assertThat(foundInsights.stream().map(Insight::getText))
            .containsExactlyInAnyOrder("Запрос 1", "Запрос 3");
    }
}
```

### Использование TestContainers для тестирования с PostgreSQL

```java
@SpringBootTest
@Testcontainers
class PostgresIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
        .withDatabaseName("integration-tests-db")
        .withUsername("testuser")
        .withPassword("testpass");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private InsightRepository insightRepository;

    @BeforeEach
    void clearDb() {
        insightRepository.deleteAll();
    }

    @Test
    void shouldPersistDataInPostgresContainer() {
        // Arrange
        Insight insight = new Insight();
        insight.setText("Тестовый запрос");
        insight.setResult("Результат анализа");

        // Act
        Insight savedInsight = insightRepository.save(insight);
        Optional<Insight> foundInsight = insightRepository.findById(savedInsight.getId());

        // Assert
        assertThat(foundInsight).isPresent();
        assertThat(foundInsight.get().getText()).isEqualTo("Тестовый запрос");
        assertThat(foundInsight.get().getResult()).isEqualTo("Результат анализа");
    }
}
```

## Тестирование Ollama API интеграции

### Мокирование внешнего API с WireMock

```java
@SpringBootTest
@AutoConfigureWireMock(port = 0)
class OllamaApiIntegrationTest {

    @Autowired
    private OllamaApiClient ollamaApiClient;
    
    @Value("${wiremock.server.port}")
    private int wiremockPort;
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(ollamaApiClient, "ollamaApiUrl", 
            "http://localhost:" + wiremockPort);
    }
    
    @Test
    void shouldCallGenerateEndpointAndReturnResponse() {
        // Arrange
        stubFor(post(urlEqualTo("/api/generate"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "model": "llama2",
                        "created_at": "2023-01-01T12:00:00Z",
                        "response": "Это тестовый ответ от модели",
                        "done": true,
                        "context": [1, 2, 3, 4, 5],
                        "total_duration": 1500000000,
                        "load_duration": 100000000,
                        "prompt_eval_count": 10,
                        "eval_count": 20,
                        "eval_duration": 1400000000
                    }
                    """)
            ));
        
        OllamaApiRequest request = new OllamaApiRequest();
        request.setPrompt("Тестовый запрос");
        request.setModel("llama2");
        request.setOptions(Map.of("temperature", 0.7));
        
        // Act
        OllamaApiResponse response = ollamaApiClient.generate(request);
        
        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getModel()).isEqualTo("llama2");
        assertThat(response.getResponse()).isEqualTo("Это тестовый ответ от модели");
        assertThat(response.getTotalDuration()).isEqualTo(1500000000);
        
        verify(postRequestedFor(urlEqualTo("/api/generate"))
            .withRequestBody(matchingJsonPath("$.prompt", equalTo("Тестовый запрос")))
            .withRequestBody(matchingJsonPath("$.model", equalTo("llama2")))
            .withRequestBody(matchingJsonPath("$.options.temperature", equalTo("0.7")))
        );
    }
    
    @Test
    void shouldHandleErrorResponseFromOllamaApi() {
        // Arrange
        stubFor(post(urlEqualTo("/api/generate"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\":\"Invalid model specified\"}")
            ));
        
        OllamaApiRequest request = new OllamaApiRequest();
        request.setPrompt("Тестовый запрос");
        request.setModel("несуществующая_модель");
        
        // Act & Assert
        assertThatThrownBy(() -> ollamaApiClient.generate(request))
            .isInstanceOf(OllamaApiException.class)
            .hasMessageContaining("Invalid model specified");
        
        verify(postRequestedFor(urlEqualTo("/api/generate"))
            .withRequestBody(matchingJsonPath("$.model", equalTo("несуществующая_модель")))
        );
    }
    
    @Test
    void shouldHandleConnectionIssues() {
        // Arrange
        stubFor(post(urlEqualTo("/api/generate"))
            .willReturn(aResponse()
                .withFixedDelay(5000) // Большая задержка
                .withStatus(200)
            ));
        
        OllamaApiRequest request = new OllamaApiRequest();
        request.setPrompt("Тестовый запрос");
        request.setModel("llama2");
        
        // Уменьшаем таймаут для теста
        ReflectionTestUtils.setField(ollamaApiClient, "timeout", 100);
        
        // Act & Assert
        assertThatThrownBy(() -> ollamaApiClient.generate(request))
            .isInstanceOf(OllamaApiTimeoutException.class)
            .hasMessageContaining("Timeout");
        
        verify(postRequestedFor(urlEqualTo("/api/generate")));
    }
}
```

## Тестирование с использованием реального Ollama API

Для полноценного интеграционного тестирования иногда необходимо протестировать взаимодействие с реальным Ollama API, особенно для проверки правильности маппинга сущностей и обработки ответов.

```java
@SpringBootTest
@ActiveProfiles("integration-test")
class RealOllamaApiIntegrationTest {

    @Autowired
    private OllamaApiClient ollamaApiClient;
    
    @Autowired
    private NeuralApiService neuralApiService;
    
    @Test
    @Disabled("Run manually when Ollama API is available")
    void shouldSendRequestToRealOllamaApi() {
        // Arrange
        OllamaApiRequest request = new OllamaApiRequest();
        request.setPrompt("Привет, как дела?");
        request.setModel("llama2");
        request.setOptions(Map.of(
            "temperature", 0.7,
            "max_tokens", 100
        ));
        
        // Act
        OllamaApiResponse response = ollamaApiClient.generate(request);
        
        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getResponse()).isNotBlank();
        assertThat(response.getModel()).isEqualTo("llama2");
        assertThat(response.getTotalDuration()).isGreaterThan(0);
    }
    
    @Test
    @Disabled("Run manually when Ollama API is available")
    void shouldGenerateResponseViaService() {
        // Arrange
        String text = "Расскажи о погоде в Москве";
        Map<String, Object> parameters = Map.of(
            "temperature", 0.7,
            "max_tokens", 200
        );
        
        // Act
        NeuralApiResponse response = neuralApiService.generateResponse(text, parameters, null);
        
        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getResponse()).isNotBlank();
        assertThat(response.getModel()).isEqualTo("llama2");
        assertThat(response.getProcessingTimeMs()).isGreaterThan(0);
    }
}
```

## Интеграционное тестирование полного потока

Для тестирования полного потока от контроллера до базы данных с мокированием только внешнего API:

```java
@SpringBootTest
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
class FullFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InsightRepository insightRepository;
    
    @Value("${wiremock.server.port}")
    private int wiremockPort;
    
    @Autowired
    private OllamaApiClient ollamaApiClient;
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(ollamaApiClient, "ollamaApiUrl", 
            "http://localhost:" + wiremockPort);
        insightRepository.deleteAll();
    }

    @Test
    void shouldCreateInsightAndStoreInDatabase() throws Exception {
        // Arrange
        InsightRequest request = new InsightRequest();
        request.setText("Тестовый запрос для полного потока");
        request.setParameters(Map.of("temperature", 0.7));
        
        stubFor(post(urlEqualTo("/api/generate"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "model": "llama2",
                        "response": "Результат анализа для полного потока",
                        "total_duration": 1500000000
                    }
                    """)
            ));

        // Act
        MvcResult mvcResult = mockMvc.perform(post("/api/insights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn();
        
        // Извлекаем ID созданного инсайта
        String responseContent = mvcResult.getResponse().getContentAsString();
        InsightDto createdInsight = objectMapper.readValue(responseContent, InsightDto.class);
        UUID insightId = createdInsight.getId();
        
        // Assert - проверяем данные в ответе
        assertThat(createdInsight.getText()).isEqualTo("Тестовый запрос для полного потока");
        assertThat(createdInsight.getResult()).isEqualTo("Результат анализа для полного потока");
        assertThat(createdInsight.getModelUsed()).isEqualTo("llama2");
        
        // Assert - проверяем данные в базе данных
        Optional<Insight> savedInsight = insightRepository.findById(insightId);
        assertThat(savedInsight).isPresent();
        assertThat(savedInsight.get().getText()).isEqualTo("Тестовый запрос для полного потока");
        assertThat(savedInsight.get().getResult()).isEqualTo("Результат анализа для полного потока");
        assertThat(savedInsight.get().getModelUsed()).isEqualTo("llama2");
        
        // Проверяем, что был вызван Ollama API
        verify(postRequestedFor(urlEqualTo("/api/generate"))
            .withRequestBody(matchingJsonPath("$.prompt", equalTo("Тестовый запрос для полного потока")))
        );
    }
}
```

## Лучшие практики интеграционного тестирования

### Что делать

1. **Используйте соответствующие аннотации** для каждого типа интеграционного теста
2. **Изолируйте тестовые данные** для каждого теста
3. **Используйте отдельные профили** для тестирования
4. **Мокируйте внешние зависимости** или используйте TestContainers
5. **Проверяйте полный поток данных** от запроса до базы данных
6. **Учитывайте возможные проблемы производительности** - интеграционные тесты выполняются дольше
7. **Организуйте тесты** по функциональности или слоям приложения

### Чего избегать

1. **Избегайте неконтролируемых внешних зависимостей** - они делают тесты нестабильными
2. **Не создавайте слишком много тестовых данных** - это замедляет выполнение
3. **Не пишите слишком большие тесты** - каждый тест должен проверять одну функциональность
4. **Не дублируйте тестовый код** - используйте общие методы настройки
5. **Не полагайтесь на определенный порядок выполнения тестов**

## Запуск интеграционных тестов

### Через Maven

```bash
# Запуск только интеграционных тестов (классы с именами *IT.java)
mvn verify -DskipUnitTests

# Запуск всех тестов, включая интеграционные
mvn verify
```

### Через Docker Compose

Для тестирования с полностью реальным окружением можно использовать Docker Compose:

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:13
    environment:
      POSTGRES_USER: testuser
      POSTGRES_PASSWORD: testpass
      POSTGRES_DB: integration-tests-db
    ports:
      - "5432:5432"
  
  ollama:
    image: ollama/ollama:latest
    ports:
      - "11434:11434"
    volumes:
      - ollama-models:/root/.ollama

  app-tests:
    build:
      context: .
      dockerfile: Dockerfile.test
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/integration-tests-db
      SPRING_DATASOURCE_USERNAME: testuser
      SPRING_DATASOURCE_PASSWORD: testpass
      OLLAMA_API_URL: http://ollama:11434
    depends_on:
      - postgres
      - ollama

volumes:
  ollama-models:
```

Команда для запуска:

```bash
docker-compose -f docker-compose.test.yml up --build --abort-on-container-exit
```

## Сравнительный анализ и рекомендации

| Сценарий | Рекомендуемый подход | Обоснование |
|----------|---------------------|------------|
| Тестирование бизнес-логики | Модульные тесты с моками | Быстрее, проще отладка |
| Тестирование REST API | MockMvc тесты | Легко проверить статус, заголовки и тело ответа |
| Тестирование баз данных | @DataJpaTest + TestContainers | Реальное взаимодействие с БД в изолированной среде |
| Тестирование Ollama API | WireMock | Контроль над ответами API, стабильные тесты |
| Полный поток | SpringBootTest + TestContainers + WireMock | Реалистичное тестирование с контролируемыми внешними зависимостями |
| Проверка регрессий | Автоматизированные интеграционные тесты в CI | Раннее обнаружение проблем интеграции |

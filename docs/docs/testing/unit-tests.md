---
sidebar_position: 2
---

# Модульное тестирование

## Основы модульного тестирования в Spring Boot

Модульное тестирование (Unit Testing) является фундаментом процесса тестирования и направлено на проверку корректности работы отдельных компонентов приложения в изоляции от других компонентов.

### Принципы модульного тестирования

1. **Изоляция** - тестирование компонента в изоляции от его зависимостей
2. **Быстрота выполнения** - модульные тесты должны выполняться быстро
3. **Независимость** - тесты не должны зависеть от результатов других тестов
4. **Повторяемость** - многократный запуск теста должен давать одинаковый результат
5. **Однозначность** - тест должен либо однозначно проходить, либо однозначно падать

## Структура модульных тестов

Все модульные тесты расположены в директории `src/test/java` и соответствуют структуре основного кода. Имена тестовых классов заканчиваются на `Test`.

```
src/
├── main/java/
│   └── com/example/springbootapp/
│       ├── controller/
│       ├── service/
│       └── repository/
└── test/java/
    └── com/example/springbootapp/
        ├── controller/
        │   └── InsightControllerTest.java
        ├── service/
        │   └── InsightServiceTest.java
        └── repository/
            └── InsightRepositoryTest.java
```

## Тестирование слоев приложения

### Тестирование сервисного слоя

Сервисный слой содержит бизнес-логику приложения и является основным объектом модульного тестирования.

#### Пример тестирования InsightService

```java
@ExtendWith(MockitoExtension.class)
class InsightServiceTest {

    @Mock
    private InsightRepository insightRepository;
    
    @Mock
    private NeuralApiService neuralApiService;
    
    @InjectMocks
    private InsightServiceImpl insightService;
    
    @Test
    void shouldCreateInsightSuccessfully() {
        // Arrange
        String text = "Тестовый запрос для анализа";
        Map<String, Object> parameters = Map.of("temperature", 0.7);
        
        NeuralApiResponse apiResponse = new NeuralApiResponse();
        apiResponse.setResponse("Результат анализа");
        apiResponse.setModel("llama2");
        apiResponse.setProcessingTimeMs(1500L);
        
        when(neuralApiService.generateResponse(any(), any(), any()))
            .thenReturn(apiResponse);
        
        Insight savedInsight = new Insight();
        savedInsight.setId(UUID.randomUUID());
        savedInsight.setText(text);
        savedInsight.setResult("Результат анализа");
        savedInsight.setModelUsed("llama2");
        savedInsight.setProcessingTimeMs(1500L);
        
        when(insightRepository.save(any(Insight.class)))
            .thenReturn(savedInsight);
        
        // Act
        InsightDto result = insightService.createInsight(text, parameters);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo(text);
        assertThat(result.getResult()).isEqualTo("Результат анализа");
        assertThat(result.getModelUsed()).isEqualTo("llama2");
        assertThat(result.getProcessingTimeMs()).isEqualTo(1500L);
        
        verify(neuralApiService).generateResponse(eq(text), eq(parameters), eq(null));
        verify(insightRepository).save(any(Insight.class));
    }
    
    @Test
    void shouldThrowExceptionWhenNeuralApiServiceFails() {
        // Arrange
        String text = "Тестовый запрос для анализа";
        Map<String, Object> parameters = Map.of("temperature", 0.7);
        
        when(neuralApiService.generateResponse(any(), any(), any()))
            .thenThrow(new RuntimeException("API недоступен"));
        
        // Act & Assert
        assertThatThrownBy(() -> insightService.createInsight(text, parameters))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("API недоступен");
        
        verify(neuralApiService).generateResponse(eq(text), eq(parameters), eq(null));
        verify(insightRepository, never()).save(any(Insight.class));
    }
    
    @Test
    void shouldGetInsightById() {
        // Arrange
        UUID id = UUID.randomUUID();
        
        Insight insight = new Insight();
        insight.setId(id);
        insight.setText("Запрос");
        insight.setResult("Результат");
        insight.setModelUsed("llama2");
        insight.setTimestamp(Instant.now());
        
        when(insightRepository.findById(id))
            .thenReturn(Optional.of(insight));
        
        // Act
        Optional<InsightDto> result = insightService.getInsightById(id);
        
        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getText()).isEqualTo("Запрос");
        assertThat(result.get().getResult()).isEqualTo("Результат");
        
        verify(insightRepository).findById(id);
    }
    
    @Test
    void shouldReturnEmptyWhenInsightNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        
        when(insightRepository.findById(id))
            .thenReturn(Optional.empty());
        
        // Act
        Optional<InsightDto> result = insightService.getInsightById(id);
        
        // Assert
        assertThat(result).isEmpty();
        
        verify(insightRepository).findById(id);
    }
}
```

### Тестирование NeuralApiService

```java
@ExtendWith(MockitoExtension.class)
class NeuralApiServiceTest {

    @Mock
    private OllamaApiClient ollamaApiClient;
    
    @Mock
    private OllamaProperties ollamaProperties;
    
    @InjectMocks
    private NeuralApiServiceImpl neuralApiService;
    
    @BeforeEach
    void setUp() {
        when(ollamaProperties.getModel()).thenReturn("llama2");
    }
    
    @Test
    void shouldGenerateResponseUsingDefaultModel() {
        // Arrange
        String text = "Тестовый запрос";
        Map<String, Object> parameters = Collections.emptyMap();
        
        OllamaApiRequest expectedRequest = new OllamaApiRequest();
        expectedRequest.setPrompt(text);
        expectedRequest.setModel("llama2");
        expectedRequest.setOptions(Collections.emptyMap());
        
        OllamaApiResponse apiResponse = new OllamaApiResponse();
        apiResponse.setResponse("Тестовый ответ");
        apiResponse.setModel("llama2");
        apiResponse.setTotalDuration(1500L);
        
        when(ollamaApiClient.generate(any(OllamaApiRequest.class)))
            .thenReturn(apiResponse);
        
        // Act
        NeuralApiResponse result = neuralApiService.generateResponse(text, parameters, null);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo("Тестовый ответ");
        assertThat(result.getModel()).isEqualTo("llama2");
        assertThat(result.getProcessingTimeMs()).isEqualTo(1500L);
        
        ArgumentCaptor<OllamaApiRequest> captor = ArgumentCaptor.forClass(OllamaApiRequest.class);
        verify(ollamaApiClient).generate(captor.capture());
        
        OllamaApiRequest actualRequest = captor.getValue();
        assertThat(actualRequest.getPrompt()).isEqualTo(text);
        assertThat(actualRequest.getModel()).isEqualTo("llama2");
    }
    
    @Test
    void shouldGenerateResponseUsingSpecifiedModel() {
        // Arrange
        String text = "Тестовый запрос";
        Map<String, Object> parameters = Collections.emptyMap();
        String model = "mistral";
        
        OllamaApiResponse apiResponse = new OllamaApiResponse();
        apiResponse.setResponse("Тестовый ответ от другой модели");
        apiResponse.setModel(model);
        
        when(ollamaApiClient.generate(any(OllamaApiRequest.class)))
            .thenReturn(apiResponse);
        
        // Act
        NeuralApiResponse result = neuralApiService.generateResponse(text, parameters, model);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getResponse()).isEqualTo("Тестовый ответ от другой модели");
        assertThat(result.getModel()).isEqualTo(model);
        
        ArgumentCaptor<OllamaApiRequest> captor = ArgumentCaptor.forClass(OllamaApiRequest.class);
        verify(ollamaApiClient).generate(captor.capture());
        
        OllamaApiRequest actualRequest = captor.getValue();
        assertThat(actualRequest.getModel()).isEqualTo(model);
    }
    
    @Test
    void shouldApplyParametersToOllamaRequest() {
        // Arrange
        String text = "Тестовый запрос";
        Map<String, Object> parameters = Map.of(
            "temperature", 0.7,
            "top_p", 0.9,
            "max_tokens", 500
        );
        
        OllamaApiResponse apiResponse = new OllamaApiResponse();
        apiResponse.setResponse("Тестовый ответ");
        
        when(ollamaApiClient.generate(any(OllamaApiRequest.class)))
            .thenReturn(apiResponse);
        
        // Act
        neuralApiService.generateResponse(text, parameters, null);
        
        // Assert
        ArgumentCaptor<OllamaApiRequest> captor = ArgumentCaptor.forClass(OllamaApiRequest.class);
        verify(ollamaApiClient).generate(captor.capture());
        
        OllamaApiRequest actualRequest = captor.getValue();
        assertThat(actualRequest.getOptions()).containsEntry("temperature", 0.7);
        assertThat(actualRequest.getOptions()).containsEntry("top_p", 0.9);
        assertThat(actualRequest.getOptions()).containsEntry("max_tokens", 500);
    }
    
    @Test
    void shouldHandleApiClientException() {
        // Arrange
        String text = "Тестовый запрос";
        Map<String, Object> parameters = Collections.emptyMap();
        
        when(ollamaApiClient.generate(any(OllamaApiRequest.class)))
            .thenThrow(new OllamaApiException("API недоступен"));
        
        // Act & Assert
        assertThatThrownBy(() -> neuralApiService.generateResponse(text, parameters, null))
            .isInstanceOf(NeuralApiException.class)
            .hasMessageContaining("Ошибка при обращении к нейросети");
        
        verify(ollamaApiClient).generate(any(OllamaApiRequest.class));
    }
}
```

### Тестирование утилитных классов

```java
class OllamaRequestConverterTest {

    @Test
    void shouldConvertNeuralApiRequestToOllamaRequest() {
        // Arrange
        NeuralApiRequest request = new NeuralApiRequest();
        request.setPrompt("Тестовый запрос");
        request.setModel("llama2");
        request.setParameters(Map.of(
            "temperature", 0.7,
            "top_p", 0.9,
            "max_tokens", 500
        ));
        
        // Act
        OllamaApiRequest result = OllamaRequestConverter.convert(request);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPrompt()).isEqualTo("Тестовый запрос");
        assertThat(result.getModel()).isEqualTo("llama2");
        assertThat(result.getOptions()).containsEntry("temperature", 0.7);
        assertThat(result.getOptions()).containsEntry("top_p", 0.9);
        assertThat(result.getOptions()).containsEntry("max_tokens", 500);
    }
    
    @Test
    void shouldHandleNullParameters() {
        // Arrange
        NeuralApiRequest request = new NeuralApiRequest();
        request.setPrompt("Тестовый запрос");
        request.setModel("llama2");
        request.setParameters(null);
        
        // Act
        OllamaApiRequest result = OllamaRequestConverter.convert(request);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPrompt()).isEqualTo("Тестовый запрос");
        assertThat(result.getModel()).isEqualTo("llama2");
        assertThat(result.getOptions()).isEmpty();
    }
}
```

## Тестирование исключений и граничных условий

### Проверка различных исключительных ситуаций

```java
@ExtendWith(MockitoExtension.class)
class InsightValidationServiceTest {

    @InjectMocks
    private InsightValidationServiceImpl validationService;
    
    @Test
    void shouldThrowExceptionWhenTextIsEmpty() {
        // Act & Assert
        assertThatThrownBy(() -> validationService.validateInsightRequest("", Collections.emptyMap()))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Текст запроса не может быть пустым");
    }
    
    @Test
    void shouldThrowExceptionWhenTemperatureOutOfRange() {
        // Arrange
        String text = "Валидный текст";
        Map<String, Object> parameters = Map.of("temperature", 2.0);
        
        // Act & Assert
        assertThatThrownBy(() -> validationService.validateInsightRequest(text, parameters))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Значение temperature должно быть в диапазоне от 0.0 до 1.0");
    }
    
    @Test
    void shouldThrowExceptionWhenTopPOutOfRange() {
        // Arrange
        String text = "Валидный текст";
        Map<String, Object> parameters = Map.of("top_p", -0.5);
        
        // Act & Assert
        assertThatThrownBy(() -> validationService.validateInsightRequest(text, parameters))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Значение top_p должно быть в диапазоне от 0.0 до 1.0");
    }
    
    @Test
    void shouldThrowExceptionWhenMaxTokensTooLarge() {
        // Arrange
        String text = "Валидный текст";
        Map<String, Object> parameters = Map.of("max_tokens", 10000);
        
        // Act & Assert
        assertThatThrownBy(() -> validationService.validateInsightRequest(text, parameters))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Значение max_tokens должно быть в диапазоне от 1 до 4096");
    }
    
    @Test
    void shouldPassValidationForValidParameters() {
        // Arrange
        String text = "Валидный текст";
        Map<String, Object> parameters = Map.of(
            "temperature", 0.7,
            "top_p", 0.9,
            "max_tokens", 1000
        );
        
        // Act & Assert
        // Если метод не выбрасывает исключение, то тест считается успешным
        assertThatCode(() -> validationService.validateInsightRequest(text, parameters))
            .doesNotThrowAnyException();
    }
}
```

## Использование аннотаций JUnit 5

```java
@ExtendWith(MockitoExtension.class)
class TimeBasedCacheTest {

    private TimeBasedCache<String, String> cache;
    
    @BeforeEach
    void setUp() {
        cache = new TimeBasedCache<>(Duration.ofMillis(100));
    }
    
    @Test
    void shouldCacheValueAndReturnIt() {
        // Act
        cache.put("key", "value");
        
        // Assert
        assertThat(cache.get("key")).isEqualTo("value");
    }
    
    @Test
    void shouldReturnNullForNonExistentKey() {
        // Assert
        assertThat(cache.get("nonExistentKey")).isNull();
    }
    
    @Test
    void shouldExpireValueAfterTtl() throws InterruptedException {
        // Arrange
        cache.put("key", "value");
        
        // Act
        Thread.sleep(150); // Ждем больше TTL
        
        // Assert
        assertThat(cache.get("key")).isNull();
    }
    
    @Test
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    void shouldCompleteWithinTimeout() {
        // Проверяем, что тест не занимает больше 1 секунды
        for (int i = 0; i < 1000; i++) {
            cache.put("key" + i, "value" + i);
            assertThat(cache.get("key" + i)).isEqualTo("value" + i);
        }
    }
    
    @Test
    @DisplayName("Кеш должен очищаться методом clear")
    void shouldClearAllValues() {
        // Arrange
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        
        // Act
        cache.clear();
        
        // Assert
        assertThat(cache.get("key1")).isNull();
        assertThat(cache.get("key2")).isNull();
    }
    
    @Nested
    @DisplayName("Тесты для операций обновления кеша")
    class UpdateTests {
        
        @Test
        void shouldUpdateExistingValue() {
            // Arrange
            cache.put("key", "value");
            
            // Act
            cache.put("key", "newValue");
            
            // Assert
            assertThat(cache.get("key")).isEqualTo("newValue");
        }
        
        @Test
        void shouldResetExpirationTimeWhenUpdating() throws InterruptedException {
            // Arrange
            cache.put("key", "value");
            
            Thread.sleep(50); // Ждем половину TTL
            
            // Act
            cache.put("key", "newValue");
            
            Thread.sleep(75); // Ждем еще 3/4 от TTL
            
            // Assert - значение все еще должно быть в кеше, т.к. мы обновили его и сбросили таймер
            assertThat(cache.get("key")).isEqualTo("newValue");
        }
    }
}
```

## Параметризованные тесты

```java
class ParametersConverterTest {

    @ParameterizedTest
    @CsvSource({
        "temperature,0.7,temperature,0.7",
        "top_p,0.9,top_p,0.9",
        "max_tokens,500,max_tokens,500",
        "temperature,1.0,temperature,1.0",
        "temperature,0.0,temperature,0.0"
    })
    void shouldConvertValidParameters(String paramName, Object paramValue, 
                                     String expectedName, Object expectedValue) {
        // Arrange
        Map<String, Object> parameters = Map.of(paramName, paramValue);
        
        // Act
        Map<String, Object> result = ParametersConverter.convertToOllamaParameters(parameters);
        
        // Assert
        assertThat(result).containsEntry(expectedName, expectedValue);
    }
    
    @ParameterizedTest
    @CsvSource({
        "temperature_invalid,0.7",
        "unknown_param,value",
        "non_supported,true"
    })
    void shouldIgnoreInvalidParameterNames(String paramName, Object paramValue) {
        // Arrange
        Map<String, Object> parameters = Map.of(paramName, paramValue);
        
        // Act
        Map<String, Object> result = ParametersConverter.convertToOllamaParameters(parameters);
        
        // Assert
        assertThat(result).isEmpty();
    }
    
    @ParameterizedTest
    @MethodSource("provideTemperatureValues")
    void shouldValidateTemperatureRange(double temperature, boolean isValid) {
        // Arrange
        Map<String, Object> parameters = Map.of("temperature", temperature);
        
        // Act & Assert
        if (isValid) {
            assertThatCode(() -> ParametersValidator.validate(parameters))
                .doesNotThrowAnyException();
        } else {
            assertThatThrownBy(() -> ParametersValidator.validate(parameters))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("temperature");
        }
    }
    
    static Stream<Arguments> provideTemperatureValues() {
        return Stream.of(
            Arguments.of(0.0, true),
            Arguments.of(0.5, true),
            Arguments.of(1.0, true),
            Arguments.of(-0.1, false),
            Arguments.of(1.1, false)
        );
    }
}
```

## Лучшие практики модульного тестирования

### Что делать

1. **Используйте паттерн AAA** (Arrange-Act-Assert) для структурирования тестов
2. **Тестируйте один сценарий** в одном тестовом методе
3. **Давайте тестам понятные имена**, отражающие тестируемый сценарий
4. **Используйте фикстуры** для подготовки тестовых данных
5. **Мокируйте внешние зависимости**, такие как API или базы данных
6. **Тестируйте исключительные ситуации** и граничные условия
7. **Используйте параметризованные тесты** для проверки разных входных данных

### Чего избегать

1. **Избегайте логики в тестах** - тесты должны быть простыми и понятными
2. **Не тестируйте private методы** напрямую - тестируйте public API
3. **Не создавайте зависимости между тестами** - каждый тест должен быть независимым
4. **Не используйте Thread.sleep()** без необходимости - это делает тесты нестабильными
5. **Не пишите тесты, которые зависят от порядка выполнения**

## Запуск модульных тестов

### Через Maven

```bash
# Запуск всех модульных тестов
mvn test

# Запуск конкретного тестового класса
mvn test -Dtest=InsightServiceTest

# Запуск конкретного тестового метода
mvn test -Dtest=InsightServiceTest#shouldCreateInsightSuccessfully
```

### Через IDE

1. **IntelliJ IDEA**: Правый клик на тестовый класс или метод -> Run
2. **Eclipse**: Правый клик на тестовый класс или метод -> Run As -> JUnit Test

### Отчет о покрытии тестами

Для генерации отчета о покрытии кода тестами используется JaCoCo:

```bash
# Генерация отчета о покрытии
mvn jacoco:report

# Отчет будет доступен в директории target/site/jacoco/index.html
```

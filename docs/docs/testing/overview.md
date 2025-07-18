---
sidebar_position: 1
---

# Стратегия тестирования

## Обзор подхода к тестированию

Spring Boot Ollama Integration использует многоуровневый подход к тестированию, который включает в себя различные типы тестов для обеспечения качества и надежности приложения. Эта документация предоставляет обзор стратегии тестирования и примеры реализации тестов для различных компонентов приложения.

### Пирамида тестирования

Наша стратегия тестирования основана на классической пирамиде тестирования:

```
         /\
        /  \
       /    \
      / E2E  \
     /--------\
    /          \
   /Integration \
  /--------------\
 /                \
/      Unit        \
--------------------
```

- **Модульные тесты (Unit Tests)** - тестирование отдельных компонентов в изоляции
- **Интеграционные тесты (Integration Tests)** - тестирование взаимодействия между компонентами
- **End-to-End тесты (E2E)** - тестирование всего приложения в условиях, приближенных к реальным

### Инструменты и фреймворки

В проекте используются следующие инструменты для тестирования:

| Инструмент | Назначение |
|------------|------------|
| JUnit 5 | Основной фреймворк для написания тестов |
| Mockito | Создание и настройка моков |
| MockMvc | Тестирование REST контроллеров |
| TestContainers | Интеграционное тестирование с реальными зависимостями в контейнерах |
| AssertJ | Библиотека утверждений (assertions) для удобного написания проверок |
| WireMock | Мокирование внешних HTTP-сервисов (Ollama API) |
| Selenium/WebDriver | E2E тестирование пользовательского интерфейса |

### Структура тестов

Тесты в проекте организованы по следующим принципам:

1. **Структура пакетов** соответствует структуре основного кода
2. **Naming conventions**:
   - Имена тестовых классов заканчиваются на `Test` для модульных тестов
   - Имена тестовых классов заканчиваются на `IT` для интеграционных тестов
3. **Группировка тестов** по категориям с помощью аннотаций JUnit 5 `@Tag`
4. **Организация кода внутри тестов** по принципу AAA (Arrange-Act-Assert)

### Категории тестов

| Категория | Описание | Примеры тестируемых компонентов |
|-----------|----------|--------------------------------|
| Unit | Модульные тесты отдельных компонентов | Services, Repositories, Utils |
| Integration | Тесты взаимодействия компонентов | Controllers + Services, Services + Repositories |
| API | Тесты REST API | Controllers через MockMvc или TestRestTemplate |
| Database | Тесты взаимодействия с базой данных | Repositories с использованием TestContainers |
| E2E | End-to-End тесты полного приложения | UI + API + Database |
| Performance | Тесты производительности | JMeter тесты для API |

### Конфигурация для тестирования

Для тестов используются отдельные конфигурационные файлы:

- `application-test.yml` - конфигурация для модульных и интеграционных тестов
- `application-e2e.yml` - конфигурация для end-to-end тестов

### Метрики покрытия тестами

Для измерения покрытия тестами используется JaCoCo. Целевые показатели покрытия:

- **Инструкции (Instructions)**: 80%
- **Ветви (Branches)**: 70%
- **Методы (Methods)**: 85%
- **Классы (Classes)**: 90%

### CI/CD интеграция

Тесты запускаются на каждом этапе CI/CD пайплайна:

- **Модульные тесты** - при каждом коммите
- **Интеграционные тесты** - при создании Pull Request
- **E2E тесты** - перед деплоем в staging окружение

## Тестирование компонентов с интеграцией Ollama API

Поскольку приложение интегрируется с Ollama API, особое внимание уделяется тестированию этого взаимодействия:

1. **Мокирование Ollama API** - для модульных и некоторых интеграционных тестов
2. **Тестирование с реальным Ollama API** - для глубоких интеграционных и E2E тестов
3. **Проверка отказоустойчивости** - тестирование поведения приложения при недоступности Ollama API

### Мокирование Ollama API

Для мокирования Ollama API используются следующие подходы:

1. **Mockito** - для простых сценариев в модульных тестах
2. **WireMock** - для более сложных сценариев, требующих эмуляции HTTP-взаимодействия
3. **Тестовые заглушки** - специальные реализации сервисов для тестирования

Пример мокирования с использованием Mockito:

```java
@ExtendWith(MockitoExtension.class)
class InsightServiceTest {

    @Mock
    private OllamaApiClient ollamaApiClient;
    
    @InjectMocks
    private InsightServiceImpl insightService;
    
    @Test
    void shouldGenerateInsightUsingOllamaApi() {
        // Arrange
        NeuralApiRequest expectedRequest = new NeuralApiRequest();
        expectedRequest.setPrompt("Test prompt");
        expectedRequest.setModel("llama2");
        
        NeuralApiResponse mockResponse = new NeuralApiResponse();
        mockResponse.setResponse("Test response");
        mockResponse.setProcessingTimeMs(1000L);
        
        when(ollamaApiClient.generateResponse(any(NeuralApiRequest.class)))
            .thenReturn(mockResponse);
        
        // Act
        InsightDto result = insightService.createInsight("Test prompt", Collections.emptyMap());
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getResult()).isEqualTo("Test response");
        assertThat(result.getModelUsed()).isEqualTo("llama2");
        assertThat(result.getProcessingTimeMs()).isEqualTo(1000L);
        
        verify(ollamaApiClient).generateResponse(any(NeuralApiRequest.class));
    }
}
```

Пример использования WireMock:

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
    void shouldCallOllamaApiAndParseResponse() {
        // Arrange
        stubFor(post(urlEqualTo("/api/generate"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"response\":\"Mocked response\",\"model\":\"llama2\",\"processingTimeMs\":1250}")
            ));
        
        NeuralApiRequest request = new NeuralApiRequest();
        request.setPrompt("Test prompt");
        request.setModel("llama2");
        
        // Act
        NeuralApiResponse response = ollamaApiClient.generateResponse(request);
        
        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getResponse()).isEqualTo("Mocked response");
        assertThat(response.getModel()).isEqualTo("llama2");
        assertThat(response.getProcessingTimeMs()).isEqualTo(1250L);
        
        verify(postRequestedFor(urlEqualTo("/api/generate"))
            .withRequestBody(matchingJsonPath("$.prompt", equalTo("Test prompt")))
            .withRequestBody(matchingJsonPath("$.model", equalTo("llama2")))
        );
    }
}
```

### Тестирование отказоустойчивости

Пример теста на отказоустойчивость при недоступности Ollama API:

```java
@SpringBootTest
@AutoConfigureWireMock(port = 0)
class OllamaApiResilienceTest {

    @Autowired
    private InsightService insightService;
    
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
    void shouldHandleOllamaApiTimeout() {
        // Arrange
        stubFor(post(urlEqualTo("/api/generate"))
            .willReturn(aResponse()
                .withFixedDelay(5000) // 5-секундная задержка
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"response\":\"Late response\",\"model\":\"llama2\"}")
            ));
        
        // Act & Assert
        assertThatThrownBy(() -> insightService.createInsight("Test prompt", Collections.emptyMap()))
            .isInstanceOf(OllamaApiTimeoutException.class)
            .hasMessageContaining("Timeout");
    }
    
    @Test
    void shouldHandleOllamaApiUnavailability() {
        // Arrange
        stubFor(post(urlEqualTo("/api/generate"))
            .willReturn(aResponse()
                .withStatus(503)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\":\"Service unavailable\"}")
            ));
        
        // Act & Assert
        assertThatThrownBy(() -> insightService.createInsight("Test prompt", Collections.emptyMap()))
            .isInstanceOf(OllamaApiUnavailableException.class)
            .hasMessageContaining("Ollama API is unavailable");
    }
}
```

### Рекомендации по тестированию интеграции с Ollama API

1. **Используйте профили тестирования** для переключения между моками и реальным Ollama API
2. **Создайте тестовую модель** в Ollama с быстрыми ответами для интеграционных тестов
3. **Имитируйте различные условия** - таймауты, ошибки, некорректные ответы
4. **Проверяйте логику обработки ошибок** и механизмы повторных попыток
5. **Контролируйте время выполнения тестов** с использованием таймаутов JUnit

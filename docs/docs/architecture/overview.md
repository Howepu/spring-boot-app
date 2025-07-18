---
sidebar_position: 1
---

# Архитектура приложения

## Общий обзор

Spring Boot Ollama Integration представляет собой многоуровневое приложение, построенное на принципах чистой архитектуры. Приложение интегрируется с локально запущенным Ollama API для использования нейросетевых моделей и предоставляет REST API для работы с аналитическими данными.

## Диаграмма компонентов

```
+----------------------------------+
|         Presentation Layer       |
|  +------------+  +------------+  |
|  |   Web UI   |  |  REST API  |  |
|  | (Bootstrap)|  | Controllers|  |
|  +------------+  +------------+  |
+--------------+-------------------+
               |
+--------------v-------------------+
|         Service Layer            |
|  +------------+  +------------+  |
|  |  Insight   |  |  Neural    |  |
|  |  Service   |  |   API      |  |
|  |            |  |  Service   |  |
|  +------------+  +------------+  |
+--------------+-------------------+
               |
+--------------v-------------------+
|         Data Access Layer        |
|  +------------+  +------------+  |
|  | Repository |  |  External  |  |
|  | Interface  |  |  API Client|  |
|  +------------+  +------------+  |
+----------------------------------+
```

## Слои приложения

### Presentation Layer (Слой представления)

Отвечает за взаимодействие с пользователем и внешними системами.

**Компоненты:**
- **Web UI (Bootstrap)** - веб-интерфейс для взаимодействия с пользователем
- **REST Controllers** - контроллеры REST API для обработки HTTP-запросов

**Ключевые классы:**
- `InsightController` - обрабатывает запросы к API для работы с аналитическими данными
- `ModelController` - предоставляет API для работы с нейросетевыми моделями

### Service Layer (Сервисный слой)

Реализует бизнес-логику приложения и оркестрирует взаимодействие между слоями.

**Компоненты:**
- **InsightService** - бизнес-логика для работы с аналитическими данными
- **NeuralApiService** - логика интеграции с Ollama API

**Ключевые классы:**
- `InsightServiceImpl` - реализация сервиса для работы с инсайтами
- `NeuralApiServiceImpl` - реализация сервиса для взаимодействия с API нейросетевых моделей

### Data Access Layer (Слой доступа к данным)

Обеспечивает доступ к хранилищам данных и внешним API.

**Компоненты:**
- **Repository Interfaces** - интерфейсы для работы с хранилищем данных
- **External API Clients** - клиенты для взаимодействия с внешними API

**Ключевые классы:**
- `InsightRepository` - интерфейс для доступа к хранилищу инсайтов
- `OllamaApiClient` - клиент для взаимодействия с Ollama API

## Основные потоки данных

### Создание инсайта

1. Клиент отправляет POST-запрос на `/api/insights` с текстом для анализа
2. `InsightController` принимает запрос и передает его в `InsightService`
3. `InsightService` обращается к `NeuralApiService` для выполнения анализа
4. `NeuralApiService` формирует запрос к Ollama API через `OllamaApiClient`
5. Результат анализа сохраняется через `InsightRepository` и возвращается клиенту

### Получение аналитических данных

1. Клиент отправляет GET-запрос на `/api/insights` или `/api/insights/{id}`
2. `InsightController` принимает запрос и передает его в `InsightService`
3. `InsightService` получает данные из `InsightRepository`
4. Результат возвращается клиенту

## Конфигурация и настройка

### Конфигурация Ollama API

Настройки Ollama API определяются в `application.yml` и доступны через `@ConfigurationProperties`:

```yaml
ollama:
  api:
    url: http://localhost:11434  # URL API Ollama
  model: llama2                  # Используемая модель
```

Класс конфигурации:
```java
@ConfigurationProperties(prefix = "ollama")
public class OllamaProperties {
    private ApiConfig api = new ApiConfig();
    private String model = "llama2";
    
    // getters, setters...
    
    public static class ApiConfig {
        private String url = "http://localhost:11434";
        
        // getters, setters...
    }
}
```

### Spring Security

Приложение использует Spring Security для защиты API и веб-интерфейса:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/css/**", "/js/**", "/docs/**").permitAll()
                .requestMatchers("/api/**").authenticated()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );
        
        return http.build();
    }
}
```

## Модель данных

### Основные сущности

**Insight (Инсайт)**
```java
@Entity
public class Insight {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String text;
    
    @Column(columnDefinition = "TEXT")
    private String result;
    
    @Column(nullable = false)
    private Instant timestamp;
    
    private String modelUsed;
    
    private Long processingTimeMs;
    
    // getters, setters...
}
```

**NeuralApiRequest (Запрос к нейросети)**
```java
public class NeuralApiRequest {
    private String prompt;
    private Map<String, Object> parameters;
    private String model;
    
    // getters, setters...
}
```

**NeuralApiResponse (Ответ от нейросети)**
```java
public class NeuralApiResponse {
    private String response;
    private String model;
    private Long processingTimeMs;
    private String error;
    
    // getters, setters...
}
```

## Интеграция с внешними системами

### Ollama API

Приложение интегрируется с Ollama API для работы с нейросетевыми моделями:

```java
@Service
public class OllamaApiClient {
    private final RestTemplate restTemplate;
    private final OllamaProperties ollamaProperties;
    
    public OllamaApiClient(RestTemplate restTemplate, OllamaProperties ollamaProperties) {
        this.restTemplate = restTemplate;
        this.ollamaProperties = ollamaProperties;
    }
    
    public NeuralApiResponse generateResponse(NeuralApiRequest request) {
        String url = ollamaProperties.getApi().getUrl() + "/api/generate";
        // логика формирования и отправки запроса к API
        return restTemplate.postForObject(url, request, NeuralApiResponse.class);
    }
}
```

## Мониторинг и метрики

Приложение использует Spring Boot Actuator для мониторинга и сбора метрик:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when_authorized
```

### Prometheus метрики

Для продвинутого мониторинга настроены метрики Prometheus:

```java
@Configuration
public class MetricsConfig {
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags("application", "spring-boot-ollama-integration");
    }
    
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}
```

## Заключение

Архитектура приложения Spring Boot Ollama Integration построена с учетом принципов чистой архитектуры и разделения ответственности. Многоуровневый подход обеспечивает гибкость, масштабируемость и удобство тестирования приложения. Интеграция с Ollama API позволяет использовать возможности нейросетевых моделей для анализа данных, а REST API предоставляет удобный интерфейс для работы с аналитическими данными.

---
sidebar_position: 1
---

# Настройка приложения

## Конфигурация через application.yml

Spring Boot Ollama Integration настраивается через конфигурационный файл `application.yml`. Ниже приведены основные разделы конфигурации и их описание.

### Базовая конфигурация

```yaml
spring:
  application:
    name: Spring Boot Ollama Integration
  profiles:
    active: dev

server:
  port: 8080
  servlet:
    context-path: /
```

| Параметр | Описание |
|----------|----------|
| `spring.application.name` | Имя приложения, используется для идентификации в логах и мониторинге |
| `spring.profiles.active` | Активный профиль (dev, test, prod) |
| `server.port` | Порт, на котором будет запущен сервер |
| `server.servlet.context-path` | Базовый путь для всех эндпоинтов |

### Конфигурация подключения к базе данных

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ollama_insights
    username: ollama_user
    password: your_password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

| Параметр | Описание |
|----------|----------|
| `spring.datasource.url` | URL подключения к базе данных |
| `spring.datasource.username` | Имя пользователя базы данных |
| `spring.datasource.password` | Пароль для подключения к базе данных |
| `spring.datasource.driver-class-name` | Класс JDBC драйвера |
| `spring.jpa.hibernate.ddl-auto` | Стратегия создания/обновления схемы БД (update, create, create-drop, validate, none) |
| `spring.jpa.show-sql` | Вывод SQL запросов в логи |
| `spring.jpa.properties.hibernate.format_sql` | Форматирование SQL запросов в логах |
| `spring.jpa.properties.hibernate.dialect` | Диалект SQL для Hibernate |

### Конфигурация Ollama API

```yaml
ollama:
  api:
    url: http://localhost:11434
  model: llama2
```

| Параметр | Описание |
|----------|----------|
| `ollama.api.url` | URL для подключения к Ollama API |
| `ollama.model` | Модель по умолчанию для использования |

### Конфигурация Spring Security

```yaml
spring:
  security:
    user:
      name: admin
      password: admin
      roles: ADMIN
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://your-auth-server.com
          jwk-set-uri: https://your-auth-server.com/.well-known/jwks.json
```

| Параметр | Описание |
|----------|----------|
| `spring.security.user.name` | Имя пользователя для базовой аутентификации |
| `spring.security.user.password` | Пароль для базовой аутентификации |
| `spring.security.user.roles` | Роли пользователя |
| `spring.security.oauth2.resourceserver.jwt.issuer-uri` | URI сервера авторизации (для OAuth2/JWT) |
| `spring.security.oauth2.resourceserver.jwt.jwk-set-uri` | URI для получения ключей JWT |

### Конфигурация логирования

```yaml
logging:
  level:
    root: INFO
    com.example.springbootapp: DEBUG
    org.springframework: INFO
    org.hibernate: INFO
  file:
    name: logs/app.log
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

| Параметр | Описание |
|----------|----------|
| `logging.level.*` | Уровни логирования для различных пакетов |
| `logging.file.name` | Имя файла для логов |
| `logging.pattern.console` | Формат логов в консоли |
| `logging.pattern.file` | Формат логов в файле |

### Конфигурация Spring Boot Actuator

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when_authorized
  info:
    app:
      name: ${spring.application.name}
      description: Spring Boot приложение для интеграции с Ollama API
      version: 1.0.0
```

| Параметр | Описание |
|----------|----------|
| `management.endpoints.web.exposure.include` | Список доступных Actuator эндпоинтов |
| `management.endpoint.health.show-details` | Уровень детализации эндпоинта health |
| `management.info.*` | Информация о приложении для эндпоинта info |

### Конфигурация SpringDoc/Swagger

```yaml
springdoc:
  swagger-ui:
    path: /swagger-ui.html
  api-docs:
    path: /api-docs
  packages-to-scan: com.example.springbootapp.controller
```

| Параметр | Описание |
|----------|----------|
| `springdoc.swagger-ui.path` | Путь к Swagger UI |
| `springdoc.api-docs.path` | Путь к OpenAPI документации в формате JSON |
| `springdoc.packages-to-scan` | Пакеты, которые будут сканироваться для API документации |

## Конфигурация через переменные окружения

Все параметры конфигурации могут быть также заданы через переменные окружения. Spring Boot автоматически преобразует имена параметров из формата "точка-нотации" в формат "UPPER_CASE_WITH_UNDERSCORES".

Примеры:

| Параметр в application.yml | Переменная окружения |
|----------------------------|----------------------|
| `server.port` | `SERVER_PORT` |
| `spring.datasource.url` | `SPRING_DATASOURCE_URL` |
| `ollama.api.url` | `OLLAMA_API_URL` |

Пример использования переменных окружения в Docker:

```bash
docker run -d \
  -e SERVER_PORT=8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/ollama_insights \
  -e SPRING_DATASOURCE_USERNAME=ollama_user \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -e OLLAMA_API_URL=http://ollama:11434 \
  -e OLLAMA_MODEL=llama2 \
  -p 8080:8080 \
  spring-boot-ollama-integration
```

## Профили Spring Boot

Приложение поддерживает использование различных профилей для разных окружений. Профили могут быть активированы через параметр `spring.profiles.active` в `application.yml` или через переменную окружения `SPRING_PROFILES_ACTIVE`.

### Доступные профили

#### Профиль `dev`

Файл конфигурации: `application-dev.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ollama_insights_dev
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update

logging:
  level:
    com.example.springbootapp: DEBUG
```

#### Профиль `test`

Файл конфигурации: `application-test.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ollama_insights_test
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

ollama:
  model: llama2:test
```

#### Профиль `prod`

Файл конфигурации: `application-prod.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://db-server:5432/ollama_insights_prod
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate

logging:
  level:
    root: WARN
    com.example.springbootapp: INFO

server:
  tomcat:
    max-threads: 200
    min-spare-threads: 20
```

### Активация профилей

Через командную строку:

```bash
java -jar app.jar --spring.profiles.active=prod
```

Через переменную окружения:

```bash
export SPRING_PROFILES_ACTIVE=prod
java -jar app.jar
```

В Docker:

```bash
docker run -e SPRING_PROFILES_ACTIVE=prod -p 8080:8080 spring-boot-ollama-integration
```

## Конфигурационные классы

### OllamaProperties

```java
@Configuration
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

### OpenApiConfig

```java
@Configuration
@OpenAPIDefinition
public class OpenApiConfig {
    
    @Value("${spring.application.name}")
    private String applicationName;
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationName)
                        .description("API для интеграции с Ollama и получения аналитических данных на основе нейросети")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("dev@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("/")
                                .description("Локальный сервер")
                ));
    }
}
```

### SecurityConfig

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/css/**", "/js/**", "/docs/**", "/swagger-ui/**", "/api-docs/**").permitAll()
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

## Настройка Beans

### RestTemplate для Ollama API

```java
@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate ollamaApiRestTemplate() {
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
    }
}
```

### Настройка ExecutorService для асинхронных операций

```java
@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean(name = "ollamaTaskExecutor")
    public Executor ollamaTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("ollama-task-");
        executor.initialize();
        return executor;
    }
}
```

## Файл конфигурации для различных сред

### Пример полной конфигурации для production окружения

```yaml
# application-prod.yml
spring:
  application:
    name: Spring Boot Ollama Integration
  datasource:
    url: jdbc:postgresql://db-server:5432/ollama_insights_prod
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      idle-timeout: 120000
      connection-timeout: 30000
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${AUTH_SERVER_URL}
          jwk-set-uri: ${AUTH_SERVER_URL}/.well-known/jwks.json

server:
  port: 8080
  servlet:
    context-path: /
  tomcat:
    max-threads: 200
    min-spare-threads: 20
    max-connections: 10000
    accept-count: 100
    connection-timeout: 20000

ollama:
  api:
    url: http://ollama-service:11434
  model: llama2

logging:
  level:
    root: WARN
    com.example.springbootapp: INFO
    org.springframework: WARN
    org.hibernate: WARN
  file:
    name: /var/log/app/spring-boot-ollama.log
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %highlight(%-5level) [%thread] %logger{36} : %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
    shutdown:
      enabled: false
  metrics:
    export:
      prometheus:
        enabled: true

springdoc:
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    disable-swagger-default-url: true
  api-docs:
    path: /api-docs
    enabled: true
  packages-to-scan: com.example.springbootapp.controller
```

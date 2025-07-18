---
sidebar_position: 4
---

# E2E-тестирование

## Обзор End-to-End тестирования

End-to-End (E2E) тестирование проверяет всю систему от начала до конца, имитируя реальные сценарии использования. Для Spring Boot Ollama Integration E2E тесты играют важную роль в проверке корректности работы всего приложения в условиях, максимально приближенных к реальному использованию.

### Отличия от интеграционных тестов

| Аспект | Интеграционные тесты | E2E тесты |
|--------|---------------------|-----------|
| Объект тестирования | Взаимодействие компонентов | Вся система целиком |
| Окружение | Частично изолированное | Максимально приближенное к боевому |
| Скорость выполнения | Средняя | Низкая |
| Стоимость поддержки | Средняя | Высокая |
| Стабильность | Средняя | Низкая |

## Инструменты для E2E-тестирования

### Тестирование API

- **REST Assured** - библиотека для тестирования REST API
- **Karate** - фреймворк для тестирования API с использованием BDD
- **Postman/Newman** - тестирование API через коллекции запросов

### Тестирование UI

- **Selenium WebDriver** - автоматизация действий в браузере
- **Playwright** - современная альтернатива Selenium с поддержкой разных браузеров
- **Cypress** - фреймворк для тестирования веб-приложений

### Производительность

- **JMeter** - тестирование производительности и нагрузки
- **Gatling** - нагрузочное тестирование для веб-приложений
- **Apache Bench (ab)** - простой инструмент для бенчмаркинга HTTP-серверов

## Тестирование REST API с использованием REST Assured

REST Assured предоставляет возможности для написания E2E тестов для REST API с выразительным и удобным DSL.

### Настройка зависимостей

```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.3.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>json-path</artifactId>
    <version>5.3.0</version>
    <scope>test</scope>
</dependency>
```

### Пример E2E теста для API

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("e2e")
class ApiE2ETest {

    private static final String BASE_URL = "http://localhost:8080/api";
    private static UUID createdInsightId;

    @Test
    @Order(1)
    void shouldCreateInsight() {
        // Arrange
        Map<String, Object> request = Map.of(
            "text", "E2E тестовый запрос",
            "parameters", Map.of("temperature", 0.7)
        );

        // Act & Assert
        Response response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post(BASE_URL + "/insights")
            .then()
            .statusCode(201)
            .body("text", equalTo("E2E тестовый запрос"))
            .body("result", not(emptyOrNullString()))
            .body("modelUsed", not(emptyOrNullString()))
            .extract().response();

        // Сохраняем ID для использования в следующих тестах
        createdInsightId = UUID.fromString(response.path("id"));
    }

    @Test
    @Order(2)
    void shouldRetrieveInsightById() {
        // Act & Assert
        RestAssured.given()
            .accept(ContentType.JSON)
            .when()
            .get(BASE_URL + "/insights/" + createdInsightId)
            .then()
            .statusCode(200)
            .body("id", equalTo(createdInsightId.toString()))
            .body("text", equalTo("E2E тестовый запрос"));
    }

    @Test
    @Order(3)
    void shouldListInsights() {
        // Act & Assert
        RestAssured.given()
            .accept(ContentType.JSON)
            .when()
            .get(BASE_URL + "/insights")
            .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(1)))
            .body("findAll { it.id == '" + createdInsightId + "' }.size()", greaterThanOrEqualTo(1));
    }

    @Test
    @Order(4)
    void shouldReturn404ForNonExistentInsight() {
        // Act & Assert
        RestAssured.given()
            .accept(ContentType.JSON)
            .when()
            .get(BASE_URL + "/insights/" + UUID.randomUUID())
            .then()
            .statusCode(404);
    }

    @Test
    @Order(5)
    void shouldReturn400ForInvalidRequest() {
        // Arrange
        Map<String, Object> request = Map.of(
            "text", "",
            "parameters", Map.of()
        );

        // Act & Assert
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post(BASE_URL + "/insights")
            .then()
            .statusCode(400)
            .body("message", containsString("Текст запроса не может быть пустым"));
    }
}
```

## Тестирование UI с использованием Selenium WebDriver

Для тестирования веб-интерфейса приложения используется Selenium WebDriver, который позволяет автоматизировать действия пользователя в браузере.

### Настройка зависимостей

```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.10.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.github.bonigarcia</groupId>
    <artifactId>webdrivermanager</artifactId>
    <version>5.4.0</version>
    <scope>test</scope>
</dependency>
```

### Пример E2E теста для веб-интерфейса

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("e2e")
class UiE2ETest {

    private WebDriver driver;
    private static final String BASE_URL = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Для запуска без GUI
        driver = new ChromeDriver(options);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    void shouldNavigateToHomePage() {
        // Act
        driver.get(BASE_URL);
        
        // Assert
        assertThat(driver.getTitle()).contains("Spring Boot Ollama Integration");
        assertThat(driver.findElement(By.tagName("h1")).getText())
            .contains("Spring Boot Ollama Integration");
    }

    @Test
    @Order(2)
    void shouldCreateNewInsight() {
        // Arrange
        driver.get(BASE_URL + "/insights/new");
        
        // Act - заполняем форму
        WebElement textArea = driver.findElement(By.id("insightText"));
        textArea.sendKeys("Тестовый запрос из Selenium");
        
        WebElement temperatureInput = driver.findElement(By.id("temperature"));
        temperatureInput.clear();
        temperatureInput.sendKeys("0.7");
        
        WebElement submitButton = driver.findElement(By.id("submitBtn"));
        submitButton.click();
        
        // Assert - проверяем, что мы перенаправлены на страницу с результатом
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.urlContains("/insights/"));
        
        WebElement resultElement = driver.findElement(By.id("insightResult"));
        assertThat(resultElement.getText()).isNotBlank();
        
        WebElement modelElement = driver.findElement(By.id("modelInfo"));
        assertThat(modelElement.getText()).contains("llama2");
    }

    @Test
    @Order(3)
    void shouldShowInsightsList() {
        // Act
        driver.get(BASE_URL + "/insights");
        
        // Assert
        List<WebElement> insightCards = driver.findElements(By.className("insight-card"));
        assertThat(insightCards).hasSizeGreaterThan(0);
        
        boolean foundTestInsight = insightCards.stream()
            .anyMatch(card -> card.getText().contains("Тестовый запрос из Selenium"));
        
        assertThat(foundTestInsight).isTrue();
    }

    @Test
    @Order(4)
    void shouldShowValidationErrorForEmptyText() {
        // Arrange
        driver.get(BASE_URL + "/insights/new");
        
        // Act - отправляем форму с пустым текстом
        WebElement submitButton = driver.findElement(By.id("submitBtn"));
        submitButton.click();
        
        // Assert - проверяем наличие сообщения об ошибке
        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(ExpectedConditions.visibilityOfElementLocated(By.className("error-message")));
        
        WebElement errorMessage = driver.findElement(By.className("error-message"));
        assertThat(errorMessage.getText()).contains("Текст запроса не может быть пустым");
    }
}
```

## Тестирование производительности с использованием JMeter

JMeter используется для тестирования производительности и нагрузки на API приложения.

### Пример JMeter теста для API

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("e2e")
class JMeterPerformanceTest {

    private static final String BASE_URL = "http://localhost:8080/api";
    private StandardJMeterEngine jmeter;
    private HashTree testPlanTree;

    @BeforeEach
    void setUp() {
        // Инициализация JMeter
        jmeter = new StandardJMeterEngine();
        
        // Настройка тестового плана
        TestPlan testPlan = new TestPlan("Spring Boot Ollama API Performance Test");
        
        // Группа потоков (Thread Group)
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("API Users");
        threadGroup.setNumThreads(10); // 10 пользователей
        threadGroup.setRampUp(5); // Разгон за 5 секунд
        threadGroup.setScheduler(true);
        threadGroup.setDuration(60); // Тест длится 60 секунд
        
        // HTTP запрос
        HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
        httpSampler.setDomain("localhost");
        httpSampler.setPort(8080);
        httpSampler.setPath("/api/insights");
        httpSampler.setMethod("POST");
        httpSampler.setContentEncoding("UTF-8");
        
        // Тело запроса
        httpSampler.addNonEncodedArgument("", 
            "{\"text\":\"Performance test request\",\"parameters\":{\"temperature\":0.7}}", 
            "application/json");
        
        // Заголовки запроса
        HeaderManager headerManager = new HeaderManager();
        headerManager.add(new Header("Content-Type", "application/json"));
        headerManager.add(new Header("Accept", "application/json"));
        
        // Добавление элементов в тестовый план
        testPlanTree = new HashTree();
        HashTree threadGroupHashTree = testPlanTree.add(testPlan).add(threadGroup);
        threadGroupHashTree.add(httpSampler);
        threadGroupHashTree.add(headerManager);
        
        // Listener для сбора результатов
        SummaryReport summaryReport = new SummaryReport();
        threadGroupHashTree.add(summaryReport);
        
        // Настройка JMeter
        jmeter.configure(testPlanTree);
    }

    @Test
    @Disabled("Run manually for performance testing")
    void shouldHandleApiLoad() {
        // Act
        jmeter.run();
        
        // Assert
        SummaryReport report = testPlanTree.getTree(new Object[]{}).get("SummaryReport");
        assertThat(report.getErrorCount()).isZero();
        assertThat(report.getErrorPercentage()).isZero();
        assertThat(report.getMedian()).isLessThan(2000); // Медианное время ответа < 2 секунд
        assertThat(report.getMax()).isLessThan(5000); // Максимальное время ответа < 5 секунд
    }
}
```

## Docker-контейнеры для E2E тестирования

Для создания полноценного окружения для E2E тестирования используется Docker Compose:

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:13
    environment:
      POSTGRES_USER: e2euser
      POSTGRES_PASSWORD: e2epass
      POSTGRES_DB: e2edb
    ports:
      - "5433:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U e2euser -d e2edb"]
      interval: 5s
      timeout: 5s
      retries: 5
  
  ollama:
    image: ollama/ollama:latest
    ports:
      - "11435:11434"
    volumes:
      - ollama-models:/root/.ollama
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:11434/api/tags"]
      interval: 10s
      timeout: 5s
      retries: 5

  spring-app:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8081:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=e2e
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/e2edb
      - SPRING_DATASOURCE_USERNAME=e2euser
      - SPRING_DATASOURCE_PASSWORD=e2epass
      - OLLAMA_API_URL=http://ollama:11434
    depends_on:
      postgres:
        condition: service_healthy
      ollama:
        condition: service_healthy

  e2e-tests:
    image: maven:3.8-openjdk-17
    working_dir: /app
    volumes:
      - ./:/app
    command: mvn test -Pe2e
    environment:
      - API_BASE_URL=http://spring-app:8080
    depends_on:
      - spring-app

volumes:
  ollama-models:
```

## Настройка CI/CD для E2E тестирования

### GitHub Actions

```yaml
name: E2E Tests

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  e2e-tests:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven
    
    - name: Start Docker Compose
      run: docker-compose -f docker-compose.e2e.yml up -d
    
    - name: Wait for application to start
      run: |
        timeout 300 bash -c 'until $(curl --output /dev/null --silent --head --fail http://localhost:8081/actuator/health); do printf "."; sleep 5; done'
    
    - name: Run E2E Tests
      run: mvn test -Pe2e -Dtest=*E2ETest
    
    - name: Docker logs on failure
      if: failure()
      run: docker-compose -f docker-compose.e2e.yml logs
    
    - name: Stop Docker Compose
      run: docker-compose -f docker-compose.e2e.yml down -v
```

## Лучшие практики E2E тестирования

### Что делать

1. **Тестируйте критические пути** - сосредоточьтесь на основных пользовательских сценариях
2. **Используйте стабильные селекторы** для UI тестирования (ID, data-атрибуты)
3. **Изолируйте тестовые данные** - используйте отдельную базу данных для E2E тестов
4. **Ограничьте количество E2E тестов** - они дорогие в поддержке
5. **Добавьте повторные попытки и таймауты** для повышения стабильности тестов
6. **Используйте аннотации @Order** для последовательного выполнения зависимых тестов
7. **Документируйте предварительные условия** для ручного запуска E2E тестов

### Чего избегать

1. **Избегайте зависимости от внешних систем**, которые вы не контролируете
2. **Не создавайте излишне сложные E2E тесты** - разбивайте на более мелкие
3. **Не полагайтесь на хрупкие селекторы** (XPath по полному пути, селекторы по тексту)
4. **Не храните учетные данные в тестовом коде** - используйте переменные окружения
5. **Не игнорируйте падающие E2E тесты** - они часто указывают на реальные проблемы

## Стратегия запуска E2E тестов

1. **Локальная разработка**: Запуск минимального набора E2E тестов по требованию
2. **CI/CD**: Полный набор E2E тестов на предрелизных ветках
3. **Ночные сборки**: Расширенный набор включая нагрузочное тестирование
4. **Перед деплоем**: Проверка критического пути на staging-окружении

### Команды для запуска E2E тестов

```bash
# Запуск всех E2E тестов
mvn test -Pe2e

# Запуск только API E2E тестов
mvn test -Pe2e -Dtest=*ApiE2ETest

# Запуск только UI E2E тестов
mvn test -Pe2e -Dtest=*UiE2ETest

# Запуск с использованием Docker Compose
docker-compose -f docker-compose.e2e.yml up --build --abort-on-container-exit
```

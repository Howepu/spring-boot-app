# AI-Insight Dashboard

Интерактивное веб-приложение для генерации аналитических данных и инсайтов по любой теме с использованием локально запущенного Ollama API. Приложение построено на Spring Boot с Bootstrap веб-интерфейсом.

## О проекте

AI-Insight Dashboard позволяет пользователям получить глубокий анализ любой интересующей темы. Пользователь вводит тему, система отправляет запрос к локальному Ollama API для генерации аналитических данных на основе нейросети, и отображает результаты в удобном веб-интерфейсе.

### Функциональность

- Ввод темы для анализа
- Генерация детального обзора по теме с использованием нейросети
- Обработка и визуализация результатов
- Мониторинг и аналитика работы приложения
- Защищенный API с OpenAPI документацией

## Технологический стек

### Backend
- **Java 17**
- **Spring Boot 3.x** - каркас для создания веб-приложений
- **Spring Security** - для обеспечения безопасности API
- **Spring MVC** - для обработки HTTP-запросов
- **RESTful API** - для обмена данными между клиентом и сервером
- **@ConfigurationProperties** - для конфигурации подключения к Ollama API
- **Prometheus** - для мониторинга приложения
- **OpenAPI/Swagger** - для документирования API
- **Lombok** - для уменьшения шаблонного кода

### Frontend
- **Bootstrap** - CSS фреймворк для создания адаптивного интерфейса
- **Thymeleaf** - шаблонизатор для серверного рендеринга страниц
- **JavaScript** - для интерактивности на клиентской стороне

### Интеграции
- **Ollama API** - локальная нейросеть для генерации аналитики
  - Модель по умолчанию: llama2
  - URL по умолчанию: http://localhost:11434

### Архитектура

- **Многослойная архитектура**:
  - Controller слой - обработка HTTP-запросов (InsightController)
  - Service слой - бизнес-логика (InsightService, NeuralApiService)
  - Config слой - конфигурация приложения (SecurityConfig, OpenApiConfig)

## Установка и запуск

### Предварительные требования

- Java 17 или выше
- Maven 3.6.0 или выше (или можно использовать встроенный Maven Wrapper)
- Ollama - локально запущенный сервис с API на порту 11434
- Prometheus (опционально, для мониторинга)

### Настройка Ollama

1. Установите Ollama согласно инструкции с официального сайта (https://ollama.ai/)
2. Запустите Ollama сервис локально
3. Убедитесь, что API доступен по адресу http://localhost:11434
4. Загрузите модель llama2 с помощью команды:
   ```bash
   ollama pull llama2
   ```

### Настройка окружения

1. Клонировать репозиторий:
   ```bash
   git clone https://github.com/your-username/spring-boot-app.git
   cd spring-boot-app
   ```

2. Настройка параметров (при необходимости) в `application.yml`:
   ```yaml
   ollama:
     api:
       url: http://localhost:11434
     model: llama2
   ```

### Запуск приложения

1. Компиляция и запуск:
   ```bash
   # Используя Maven Wrapper
   ./mvnw spring-boot:run

   # Или если Maven уже установлен
   mvn spring-boot:run
   ```

2. Приложение будет доступно по адресу: http://localhost:8080
3. API документация Swagger будет доступна по адресу: http://localhost:8080/swagger-ui/index.html

### Запуск Prometheus мониторинга (опционально)

1. Запустите Prometheus с конфигурацией из папки prometheus:
   ```bash
   prometheus --config.file=prometheus/prometheus.yml
   ```

2. Prometheus будет доступен по адресу: http://localhost:9090

## API Endpoints

### InsightController

- `POST /api/insights` - получить аналитические данные по указанной теме
  - Request body: `{"topic": "название темы для анализа"}`
  - Response: JSON с аналитическими данными от нейросети

- `GET /api/insights/status` - получить статус сервиса
  - Response: Информация о доступности Ollama API

- `GET /actuator/prometheus` - метрики для Prometheus (требует соответствующих прав)

## Структура проекта

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── springbootapp/
│   │               ├── controller/
│   │               │   └── InsightController.java
│   │               ├── service/
│   │               │   ├── InsightService.java
│   │               │   └── impl/
│   │               │       └── InsightServiceImpl.java
│   │               └── SpringBootAppApplication.java
│   └── resources/
│       ├── static/
│       │   └── js/
│       │       ├── components/
│       │       │   ├── InsightForm.js
│       │       │   └── HomePage.js
│       │       └── main.js
│       └── templates/
│           └── index.html
└── test/
    └── java/
        └── com/
            └── example/
                └── springbootapp/
                    ├── controller/
                    └── service/
                        └── impl/
                            └── InsightServiceImplTest.java
```

## Разработка

### Добавление новых функций

1. **Backend**: Расширение функционала через сервисы и контроллеры
2. **Frontend**: Модификация React-компонентов для новых возможностей

## Тестирование

Проект содержит тесты для сервисов и контроллеров:

```bash
# Запуск тестов
mvn test
```

## Лицензия

МИТ

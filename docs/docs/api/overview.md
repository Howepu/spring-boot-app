---
sidebar_position: 1
---

# Обзор API

## Введение

API приложения Spring Boot Ollama Integration обеспечивает доступ к функциональности для работы с нейросетевыми моделями через Ollama API. Оно позволяет получать аналитические данные на основе запросов к нейросети и управлять параметрами нейросетевых моделей.

## Основные эндпоинты

### Insights API

REST API для получения аналитических данных на основе нейросетевых моделей:

| Метод | Эндпоинт | Описание |
|-------|---------|----------|
| GET | `/api/insights` | Получение списка всех инсайтов |
| GET | `/api/insights/{id}` | Получение инсайта по идентификатору |
| POST | `/api/insights` | Создание нового инсайта на основе текстовых данных |
| PUT | `/api/insights/{id}` | Обновление существующего инсайта |
| DELETE | `/api/insights/{id}` | Удаление инсайта |

### Управление моделями

REST API для управления нейросетевыми моделями:

| Метод | Эндпоинт | Описание |
|-------|---------|----------|
| GET | `/api/models` | Получение списка доступных моделей |
| GET | `/api/models/current` | Получение информации о текущей используемой модели |
| POST | `/api/models/switch/{modelName}` | Переключение на другую нейросетевую модель |

## Форматы данных

### InsightRequest

```json
{
  "text": "Текст для анализа нейросетью",
  "parameters": {
    "temperature": 0.7,
    "maxTokens": 100
  }
}
```

### InsightResponse

```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "text": "Текст для анализа нейросетью",
  "result": "Результат анализа, полученный от нейросети",
  "timestamp": "2025-07-17T10:30:45.123Z",
  "modelUsed": "llama2",
  "processingTimeMs": 1250
}
```

### ModelInfo

```json
{
  "name": "llama2",
  "size": "7B",
  "parameters": {
    "temperature": 0.7,
    "maxTokens": 2048
  },
  "capabilities": [
    "text-generation",
    "summarization",
    "question-answering"
  ]
}
```

## Аутентификация и авторизация

API защищено Spring Security. Для доступа к API требуется аутентификация.

### Базовая аутентификация

```bash
curl -X GET "http://localhost:8080/api/insights" -H "Authorization: Basic base64(username:password)"
```

### JWT аутентификация

```bash
curl -X GET "http://localhost:8080/api/insights" -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Коды ответов

| Код | Описание |
|-----|----------|
| 200 | Успешный запрос |
| 201 | Ресурс успешно создан |
| 400 | Некорректный запрос |
| 401 | Ошибка аутентификации |
| 403 | Доступ запрещен |
| 404 | Ресурс не найден |
| 500 | Внутренняя ошибка сервера |

## Ограничения и лимиты

- Максимальный размер запроса: 16 КБ
- Лимит запросов: 100 запросов в минуту
- Таймаут запроса к нейросети: 60 секунд

## Примеры использования

### Создание нового инсайта

```bash
curl -X POST "http://localhost:8080/api/insights" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46cGFzc3dvcmQ=" \
  -d '{
    "text": "Проанализируй финансовые показатели компании за последний квартал",
    "parameters": {
      "temperature": 0.5,
      "maxTokens": 200
    }
  }'
```

### Получение списка инсайтов

```bash
curl -X GET "http://localhost:8080/api/insights" \
  -H "Authorization: Basic YWRtaW46cGFzc3dvcmQ="
```

### Переключение модели

```bash
curl -X POST "http://localhost:8080/api/models/switch/mistral" \
  -H "Authorization: Basic YWRtaW46cGFzc3dvcmQ="
```

## Управление ошибками

При возникновении ошибок API возвращает JSON-объект с информацией об ошибке:

```json
{
  "timestamp": "2025-07-17T12:34:56.789Z",
  "status": 404,
  "error": "Not Found",
  "message": "Инсайт с ID 123 не найден",
  "path": "/api/insights/123"
}
```

## Версионирование API

Текущая версия API: v1 (по умолчанию)

Для явного указания версии можно использовать заголовок:

```bash
curl -X GET "http://localhost:8080/api/insights" \
  -H "X-API-Version: v1" \
  -H "Authorization: Basic YWRtaW46cGFzc3dvcmQ="
```

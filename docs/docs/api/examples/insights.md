---
sidebar_position: 1
---

# Примеры работы с API Insights

В этом разделе приведены практические примеры использования API для работы с аналитическими инсайтами, полученными с помощью нейросетевых моделей Ollama.

## Получение списка инсайтов

### Пример запроса

```bash
curl -X GET "http://localhost:8080/api/insights" \
     -H "Accept: application/json" \
     -H "Authorization: Bearer YOUR_TOKEN"
```

### Пример ответа

```json
{
  "content": [
    {
      "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
      "text": "Анализ рыночной динамики за Q1 2025",
      "result": "На основе предоставленных данных о рынке за Q1 2025 можно сделать следующие выводы...",
      "timestamp": "2025-07-15T10:30:45.123Z",
      "modelUsed": "llama2",
      "processingTimeMs": 1250
    },
    {
      "id": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
      "text": "Прогноз роста продаж в следующем квартале",
      "result": "Анализ временных рядов показывает вероятный рост на 15-20% в следующих категориях...",
      "timestamp": "2025-07-14T15:20:30.456Z",
      "modelUsed": "llama2",
      "processingTimeMs": 1840
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "pageNumber": 0,
    "pageSize": 20,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 2,
  "totalPages": 1,
  "last": true,
  "first": true,
  "number": 0,
  "size": 20,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 2,
  "empty": false
}
```

## Получение инсайта по ID

### Пример запроса

```bash
curl -X GET "http://localhost:8080/api/insights/a1b2c3d4-e5f6-7890-abcd-ef1234567890" \
     -H "Accept: application/json" \
     -H "Authorization: Bearer YOUR_TOKEN"
```

### Пример ответа

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "text": "Анализ рыночной динамики за Q1 2025",
  "result": "На основе предоставленных данных о рынке за Q1 2025 можно сделать следующие выводы:\n\n1. Наблюдается устойчивый рост в сегменте B2B на 12.3% по сравнению с предыдущим кварталом\n2. Ключевые показатели производительности превышают прогнозируемые значения на 5-8%\n3. Наибольший рост показали следующие регионы: Москва (+18%), Санкт-Петербург (+15%), Казань (+14%)\n4. В сегменте технологических решений наблюдается смещение спроса в сторону облачных сервисов\n5. Рекомендуется увеличить маркетинговые инвестиции в наиболее перспективные направления",
  "timestamp": "2025-07-15T10:30:45.123Z",
  "modelUsed": "llama2",
  "processingTimeMs": 1250
}
```

## Создание нового инсайта

### Пример запроса

```bash
curl -X POST "http://localhost:8080/api/insights" \
     -H "Content-Type: application/json" \
     -H "Accept: application/json" \
     -H "Authorization: Bearer YOUR_TOKEN" \
     -d '{
         "text": "Проанализируй последние данные по продажам и составь прогноз на следующий квартал",
         "parameters": {
           "temperature": 0.7,
           "max_tokens": 500
         }
       }'
```

### Пример ответа

```json
{
  "id": "c3d4e5f6-a7b8-9012-cdef-123456789012",
  "text": "Проанализируй последние данные по продажам и составь прогноз на следующий квартал",
  "result": "Анализ данных о продажах за последний квартал указывает на следующие тренды:\n\n1. Общий объем продаж вырос на 8.5% относительно прошлого квартала\n2. Наиболее успешные продуктовые линейки: \n   - Облачные решения (+23%)\n   - Системы аналитики данных (+17%)\n   - Мобильные приложения (+12%)\n3. Слабый рост или падение наблюдается в следующих сегментах:\n   - Локальное ПО (-3%)\n   - Аппаратные решения (+2%)\n\nПрогноз на следующий квартал:\n\n1. Общий рост продаж ожидается в диапазоне 10-15%\n2. Наибольший потенциал роста:\n   - Облачные решения (+25-30%)\n   - AI/ML решения (+20-25%)\n3. Рекомендуемые стратегии:\n   - Усилить фокус на корпоративный сегмент\n   - Расширить портфолио облачных сервисов\n   - Интегрировать AI-функции в существующие продукты",
  "timestamp": "2025-07-17T12:45:30.789Z",
  "modelUsed": "llama2",
  "processingTimeMs": 2150
}
```

## Обновление существующего инсайта

### Пример запроса

```bash
curl -X PUT "http://localhost:8080/api/insights/c3d4e5f6-a7b8-9012-cdef-123456789012" \
     -H "Content-Type: application/json" \
     -H "Accept: application/json" \
     -H "Authorization: Bearer YOUR_TOKEN" \
     -d '{
         "text": "Проанализируй последние данные по продажам и составь детальный прогноз на следующий квартал с разбивкой по регионам",
         "parameters": {
           "temperature": 0.7,
           "max_tokens": 800,
           "model": "llama2:13b"
         }
       }'
```

### Пример ответа

```json
{
  "id": "c3d4e5f6-a7b8-9012-cdef-123456789012",
  "text": "Проанализируй последние данные по продажам и составь детальный прогноз на следующий квартал с разбивкой по регионам",
  "result": "На основе анализа продаж за последний квартал, представляю прогноз на следующий квартал с региональной разбивкой:\n\n## Общие показатели\n- Общий рост продаж: +12.8% (прогноз)\n- Наиболее перспективные продуктовые направления: облачные сервисы, аналитика данных, AI-решения\n\n## Региональная разбивка\n\n### Москва и область\n- Прогнозируемый рост: +18.5%\n- Ключевые факторы роста: корпоративный сектор, госзаказы\n- Топ-продукты: облачная инфраструктура, интеграционные решения\n- Потенциальные риски: высокая конкуренция в премиум-сегменте\n\n### Санкт-Петербург\n- Прогнозируемый рост: +15.2%\n- Ключевые факторы роста: технологические стартапы, научные организации\n- Топ-продукты: AI-инструменты, аналитические платформы\n- Потенциальные возможности: расширение в образовательном секторе\n\n### Поволжье (Казань, Нижний Новгород)\n- Прогнозируемый рост: +14.7%\n- Ключевые факторы роста: программы цифровизации, региональные инициативы\n- Топ-продукты: бизнес-аналитика, CRM-решения\n- Рекомендации: усилить локальные партнерства\n\n### Урал (Екатеринбург, Челябинск)\n- Прогнозируемый рост: +11.3%\n- Ключевые факторы роста: промышленная автоматизация, модернизация IT-инфраструктуры\n- Топ-продукты: промышленные IoT-решения, облачное хранение данных\n- Потенциальные возможности: интеграция с индустриальными системами\n\n### Сибирь (Новосибирск, Красноярск)\n- Прогнозируемый рост: +10.8%\n- Ключевые факторы роста: научные центры, модернизация телеком-инфраструктуры\n- Топ-продукты: высокопроизводительные вычисления, системы кластерного анализа\n- Рекомендации: фокус на образовательные и научные организации\n\n### Дальний Восток\n- Прогнозируемый рост: +9.5%\n- Ключевые факторы роста: логистика, международная торговля\n- Топ-продукты: системы управления логистикой, аналитика международной торговли\n- Потенциальные возможности: развитие трансграничных сервисов\n\n## Стратегические рекомендации\n1. Адаптировать маркетинговую стратегию под региональную специфику\n2. Развивать локальные партнерства в регионах с высоким потенциалом\n3. Инвестировать в развитие облачных сервисов и AI-инструментов\n4. Разработать специализированные отраслевые решения для ключевых регионов",
  "timestamp": "2025-07-17T13:20:15.456Z",
  "modelUsed": "llama2:13b",
  "processingTimeMs": 3450
}
```

## Удаление инсайта

### Пример запроса

```bash
curl -X DELETE "http://localhost:8080/api/insights/c3d4e5f6-a7b8-9012-cdef-123456789012" \
     -H "Authorization: Bearer YOUR_TOKEN"
```

### Пример ответа

```
204 No Content
```

## Получение статистики использования моделей

### Пример запроса

```bash
curl -X GET "http://localhost:8080/api/insights/stats" \
     -H "Accept: application/json" \
     -H "Authorization: Bearer YOUR_TOKEN"
```

### Пример ответа

```json
{
  "totalRequests": 1250,
  "averageProcessingTimeMs": 1850.75,
  "modelUsage": {
    "llama2": 850,
    "llama2:13b": 320,
    "mistral": 80
  },
  "requestsPerDay": [
    {
      "date": "2025-07-10",
      "count": 120
    },
    {
      "date": "2025-07-11",
      "count": 145
    },
    {
      "date": "2025-07-12",
      "count": 95
    },
    {
      "date": "2025-07-13",
      "count": 105
    },
    {
      "date": "2025-07-14",
      "count": 180
    },
    {
      "date": "2025-07-15",
      "count": 210
    },
    {
      "date": "2025-07-16",
      "count": 195
    },
    {
      "date": "2025-07-17",
      "count": 200
    }
  ]
}
```

## Доступные параметры запросов

### Параметры пагинации

Для запросов, возвращающих список элементов, доступны следующие параметры пагинации:

| Параметр | Тип | По умолчанию | Описание |
|----------|-----|--------------|----------|
| page | Integer | 0 | Номер страницы (нумерация с 0) |
| size | Integer | 20 | Количество элементов на странице |
| sort | String | timestamp,desc | Поле и направление сортировки (asc/desc) |

Пример:
```bash
curl -X GET "http://localhost:8080/api/insights?page=1&size=10&sort=timestamp,asc" \
     -H "Accept: application/json" \
     -H "Authorization: Bearer YOUR_TOKEN"
```

### Параметры фильтрации

| Параметр | Тип | Описание |
|----------|-----|----------|
| modelUsed | String | Фильтрация по используемой модели |
| fromDate | String (ISO 8601) | Минимальная дата создания инсайта |
| toDate | String (ISO 8601) | Максимальная дата создания инсайта |
| searchTerm | String | Поиск по тексту запроса или результата |

Пример:
```bash
curl -X GET "http://localhost:8080/api/insights?modelUsed=llama2:13b&fromDate=2025-07-10T00:00:00Z&toDate=2025-07-17T23:59:59Z&searchTerm=прогноз" \
     -H "Accept: application/json" \
     -H "Authorization: Bearer YOUR_TOKEN"
```

## Работа с параметрами нейросетевых моделей

При создании или обновлении инсайта можно передавать дополнительные параметры для управления поведением нейросетевой модели:

| Параметр | Тип | По умолчанию | Описание |
|----------|-----|--------------|----------|
| temperature | Float | 0.8 | Температура сэмплирования (0.0-1.0). Более высокие значения делают вывод более случайным, низкие - более детерминированным |
| max_tokens | Integer | 2048 | Максимальное количество токенов в выводе |
| model | String | из конфигурации | Модель для использования. Если не указана, используется значение из конфигурации |
| top_p | Float | 0.9 | Nucleus sampling, альтернатива temperature |
| top_k | Integer | 40 | Ограничение выбора следующего токена top-k вариантами |
| stop | Array[String] | [] | Последовательности, при которых генерация должна остановиться |

Пример запроса с параметрами модели:
```bash
curl -X POST "http://localhost:8080/api/insights" \
     -H "Content-Type: application/json" \
     -H "Accept: application/json" \
     -H "Authorization: Bearer YOUR_TOKEN" \
     -d '{
         "text": "Проанализируй тренды технологического рынка на 2025 год",
         "parameters": {
           "temperature": 0.7,
           "max_tokens": 1000,
           "model": "mistral",
           "top_p": 0.95,
           "top_k": 50,
           "stop": ["##"]
         }
       }'
```

## Обработка ошибок

### Стандартные коды ошибок

| Код | Описание | Возможная причина |
|-----|----------|-------------------|
| 400 | Bad Request | Неверный формат запроса или параметров |
| 401 | Unauthorized | Отсутствующий или некорректный токен |
| 403 | Forbidden | Недостаточно прав для выполнения операции |
| 404 | Not Found | Запрашиваемый ресурс не найден |
| 429 | Too Many Requests | Превышен лимит запросов |
| 500 | Internal Server Error | Внутренняя ошибка сервера |
| 503 | Service Unavailable | Ollama API недоступно |

### Пример ответа с ошибкой

```json
{
  "timestamp": "2025-07-17T14:30:22.456Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Недопустимое значение параметра temperature. Допустимый диапазон: 0.0-1.0",
  "path": "/api/insights"
}
```

## Примеры с использованием различных инструментов

### Пример с использованием Python и requests

```python
import requests
import json

API_URL = "http://localhost:8080/api/insights"
TOKEN = "YOUR_TOKEN"

headers = {
    "Content-Type": "application/json",
    "Accept": "application/json",
    "Authorization": f"Bearer {TOKEN}"
}

# Создание нового инсайта
payload = {
    "text": "Анализ конкурентов на рынке облачных решений",
    "parameters": {
        "temperature": 0.7,
        "max_tokens": 800
    }
}

response = requests.post(API_URL, headers=headers, data=json.dumps(payload))
print(f"Status Code: {response.status_code}")
print(json.dumps(response.json(), indent=2, ensure_ascii=False))
```

### Пример с использованием JavaScript и fetch

```javascript
const apiUrl = 'http://localhost:8080/api/insights';
const token = 'YOUR_TOKEN';

// Получение списка инсайтов
fetch(apiUrl, {
  method: 'GET',
  headers: {
    'Accept': 'application/json',
    'Authorization': `Bearer ${token}`
  }
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));

// Создание нового инсайта
const createInsight = async () => {
  const payload = {
    text: 'Стратегический анализ рынка на следующие 5 лет',
    parameters: {
      temperature: 0.8,
      max_tokens: 1000
    }
  };
  
  try {
    const response = await fetch(apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(payload)
    });
    
    const data = await response.json();
    console.log(data);
  } catch (error) {
    console.error('Error:', error);
  }
};

createInsight();
```

### Пример с использованием Java и RestTemplate

```java
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class InsightsApiExample {
    
    private static final String API_URL = "http://localhost:8080/api/insights";
    private static final String TOKEN = "YOUR_TOKEN";
    
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        
        // Настраиваем заголовки
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + TOKEN);
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        
        // Создаем новый инсайт
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("temperature", 0.7);
        parameters.put("max_tokens", 800);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("text", "Анализ потенциала выхода на международные рынки");
        requestBody.put("parameters", parameters);
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<Map> responseEntity = restTemplate.exchange(
            API_URL,
            HttpMethod.POST,
            requestEntity,
            Map.class
        );
        
        System.out.println("Status code: " + responseEntity.getStatusCode());
        System.out.println("Response: " + responseEntity.getBody());
    }
}
```

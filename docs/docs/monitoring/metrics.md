---
sidebar_position: 1
---

# Мониторинг и метрики

## Spring Boot Actuator

Spring Boot Ollama Integration использует Spring Boot Actuator для предоставления мониторинга и управления приложением. Actuator предоставляет ряд готовых эндпоинтов, которые позволяют получить информацию о состоянии приложения, метриках производительности, логировании и других аспектах работы.

### Доступные эндпоинты

По умолчанию в нашем приложении включены следующие эндпоинты Actuator:

| Эндпоинт | URL | Описание |
|----------|-----|----------|
| Health | `/actuator/health` | Проверка состояния приложения и зависимостей |
| Info | `/actuator/info` | Информация о приложении |
| Metrics | `/actuator/metrics` | Доступные метрики приложения |
| Prometheus | `/actuator/prometheus` | Метрики в формате Prometheus |

### Использование эндпоинта Health

Эндпоинт Health предоставляет информацию о состоянии приложения и его зависимостей (база данных, Ollama API и т.д.). Пример ответа:

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 500000000000,
        "free": 220000000000,
        "threshold": 10000000000
      }
    },
    "ollama": {
      "status": "UP",
      "details": {
        "version": "0.1.14",
        "availableModels": ["llama2", "mistral", "llama2:13b"]
      }
    }
  }
}
```

### Использование эндпоинта Metrics

Эндпоинт Metrics предоставляет список всех доступных метрик приложения. Для получения значения конкретной метрики необходимо добавить её имя к URL, например: `/actuator/metrics/jvm.memory.used`.

Пример ответа от `/actuator/metrics`:

```json
{
  "names": [
    "jvm.memory.used",
    "jvm.memory.max",
    "http.server.requests",
    "system.cpu.usage",
    "ollama.api.requests",
    "ollama.api.response.time",
    "ollama.model.usage",
    "insights.created",
    "insights.processing.time"
  ]
}
```

Пример ответа для конкретной метрики (`/actuator/metrics/ollama.api.requests`):

```json
{
  "name": "ollama.api.requests",
  "description": "Количество запросов к Ollama API",
  "baseUnit": "requests",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 1520
    }
  ],
  "availableTags": [
    {
      "tag": "model",
      "values": [
        "llama2",
        "mistral",
        "llama2:13b"
      ]
    },
    {
      "tag": "status",
      "values": [
        "success",
        "error"
      ]
    }
  ]
}
```

## Prometheus и Grafana

Приложение настроено для экспорта метрик в формате Prometheus через эндпоинт `/actuator/prometheus`. Это позволяет легко интегрировать приложение с системами мониторинга на базе Prometheus и Grafana.

### Настройка Prometheus

Пример конфигурации Prometheus для сбора метрик (`prometheus.yml`):

```yaml
scrape_configs:
  - job_name: 'spring-boot-ollama'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 15s
    static_configs:
      - targets: ['your-app-host:8080']
```

### Ключевые метрики для мониторинга

#### Системные метрики

- `system.cpu.usage` - Использование CPU системой
- `system.cpu.count` - Количество доступных процессоров
- `jvm.memory.used` - Использование памяти JVM
- `jvm.memory.max` - Максимально доступная память для JVM
- `jvm.gc.pause` - Время паузы на сборку мусора
- `jvm.threads.states` - Состояния потоков в JVM

#### HTTP метрики

- `http.server.requests` - Количество HTTP запросов с разбивкой по статусам, методам, URI
- `http.server.requests.seconds` - Время выполнения запросов

#### Метрики приложения

- `ollama.api.requests` - Количество запросов к Ollama API
- `ollama.api.response.time` - Время ответа от Ollama API
- `ollama.model.usage` - Использование различных моделей
- `insights.created` - Количество созданных инсайтов
- `insights.processing.time` - Время обработки инсайтов

### Дашборды Grafana

Для визуализации метрик можно использовать готовые дашборды Grafana или создать собственные. Ниже приведен пример конфигурации дашборда для мониторинга основных показателей приложения.

#### JVM Metrics Dashboard

```json
{
  "annotations": {
    "list": [
      {
        "builtIn": 1,
        "datasource": "-- Grafana --",
        "enable": true,
        "hide": true,
        "iconColor": "rgba(0, 211, 255, 1)",
        "name": "Annotations & Alerts",
        "type": "dashboard"
      }
    ]
  },
  "editable": true,
  "gnetId": null,
  "graphTooltip": 0,
  "id": 1,
  "links": [],
  "panels": [
    {
      "aliasColors": {},
      "bars": false,
      "dashLength": 10,
      "dashes": false,
      "datasource": "Prometheus",
      "fill": 1,
      "fillGradient": 0,
      "gridPos": {
        "h": 9,
        "w": 12,
        "x": 0,
        "y": 0
      },
      "hiddenSeries": false,
      "id": 2,
      "legend": {
        "alignAsTable": true,
        "avg": true,
        "current": true,
        "max": true,
        "min": true,
        "show": true,
        "total": false,
        "values": true
      },
      "lines": true,
      "linewidth": 1,
      "nullPointMode": "null",
      "options": {
        "dataLinks": []
      },
      "percentage": false,
      "pointradius": 2,
      "points": false,
      "renderer": "flot",
      "seriesOverrides": [],
      "spaceLength": 10,
      "stack": false,
      "steppedLine": false,
      "targets": [
        {
          "expr": "jvm_memory_used_bytes{application=\"spring-boot-ollama-integration\",area=\"heap\"}",
          "legendFormat": "{{id}}",
          "refId": "A"
        }
      ],
      "thresholds": [],
      "timeFrom": null,
      "timeRegions": [],
      "timeShift": null,
      "title": "JVM Heap Memory Used",
      "tooltip": {
        "shared": true,
        "sort": 0,
        "value_type": "individual"
      },
      "type": "graph",
      "xaxis": {
        "buckets": null,
        "mode": "time",
        "name": null,
        "show": true,
        "values": []
      },
      "yaxes": [
        {
          "format": "bytes",
          "label": null,
          "logBase": 1,
          "max": null,
          "min": null,
          "show": true
        },
        {
          "format": "short",
          "label": null,
          "logBase": 1,
          "max": null,
          "min": null,
          "show": true
        }
      ],
      "yaxis": {
        "align": false,
        "alignLevel": null
      }
    },
    {
      "aliasColors": {},
      "bars": false,
      "dashLength": 10,
      "dashes": false,
      "datasource": "Prometheus",
      "fill": 1,
      "fillGradient": 0,
      "gridPos": {
        "h": 9,
        "w": 12,
        "x": 12,
        "y": 0
      },
      "hiddenSeries": false,
      "id": 4,
      "legend": {
        "alignAsTable": true,
        "avg": true,
        "current": true,
        "max": true,
        "min": true,
        "show": true,
        "total": false,
        "values": true
      },
      "lines": true,
      "linewidth": 1,
      "nullPointMode": "null",
      "options": {
        "dataLinks": []
      },
      "percentage": false,
      "pointradius": 2,
      "points": false,
      "renderer": "flot",
      "seriesOverrides": [],
      "spaceLength": 10,
      "stack": false,
      "steppedLine": false,
      "targets": [
        {
          "expr": "system_cpu_usage{application=\"spring-boot-ollama-integration\"}",
          "legendFormat": "CPU Usage",
          "refId": "A"
        }
      ],
      "thresholds": [],
      "timeFrom": null,
      "timeRegions": [],
      "timeShift": null,
      "title": "System CPU Usage",
      "tooltip": {
        "shared": true,
        "sort": 0,
        "value_type": "individual"
      },
      "type": "graph",
      "xaxis": {
        "buckets": null,
        "mode": "time",
        "name": null,
        "show": true,
        "values": []
      },
      "yaxes": [
        {
          "format": "percentunit",
          "label": null,
          "logBase": 1,
          "max": "1",
          "min": "0",
          "show": true
        },
        {
          "format": "short",
          "label": null,
          "logBase": 1,
          "max": null,
          "min": null,
          "show": true
        }
      ],
      "yaxis": {
        "align": false,
        "alignLevel": null
      }
    }
  ],
  "refresh": "10s",
  "schemaVersion": 22,
  "style": "dark",
  "tags": [
    "spring-boot",
    "jvm"
  ],
  "templating": {
    "list": []
  },
  "time": {
    "from": "now-1h",
    "to": "now"
  },
  "timepicker": {
    "refresh_intervals": [
      "5s",
      "10s",
      "30s",
      "1m",
      "5m",
      "15m",
      "30m",
      "1h",
      "2h",
      "1d"
    ]
  },
  "timezone": "",
  "title": "JVM Metrics",
  "uid": "jvm-metrics",
  "version": 1
}
```

## Логирование и мониторинг логов

Приложение настроено на использование SLF4J и Logback для логирования. Логи могут быть настроены для вывода в консоль или в файл.

### Настройка уровней логирования

В файле `application.yml` можно настроить уровни логирования для разных пакетов приложения:

```yaml
logging:
  level:
    root: INFO
    com.example.springbootapp: DEBUG
    org.springframework: INFO
    org.hibernate: INFO
```

### Мониторинг логов с использованием ELK Stack

Для централизованного сбора и анализа логов можно интегрировать приложение с ELK Stack (Elasticsearch, Logstash, Kibana).

#### Настройка Logback для Logstash

Добавьте в `pom.xml`:

```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.3</version>
</dependency>
```

Создайте файл `logback-spring.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/base.xml"/>
    
    <appender name="LOGSTASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <destination>logstash-host:5000</destination>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <customFields>{"application":"spring-boot-ollama-integration"}</customFields>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="LOGSTASH"/>
    </root>
</configuration>
```

## Мониторинг производительности Ollama API

Приложение включает специальные метрики для мониторинга производительности Ollama API:

### Ключевые метрики для Ollama API

- `ollama.api.requests.count` - Общее количество запросов к Ollama API
- `ollama.api.response.time` - Время ответа Ollama API
- `ollama.api.errors.count` - Количество ошибок при запросах к Ollama API
- `ollama.model.usage.count` - Статистика использования каждой модели

### Пример реализации метрик для Ollama API

```java
@Service
public class OllamaApiService {

    private final MeterRegistry meterRegistry;
    
    public OllamaApiService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    public NeuralApiResponse generateResponse(NeuralApiRequest request) {
        String model = request.getModel() != null ? request.getModel() : "default";
        
        // Создаем таймер для измерения времени выполнения запроса
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            // Логика выполнения запроса к Ollama API
            NeuralApiResponse response = callOllamaApi(request);
            
            // Записываем метрику успешного запроса
            meterRegistry.counter("ollama.api.requests", 
                "model", model, 
                "status", "success").increment();
            
            // Останавливаем таймер и записываем время выполнения
            sample.stop(meterRegistry.timer("ollama.api.response.time", 
                "model", model));
            
            return response;
        } catch (Exception e) {
            // Записываем метрику ошибки
            meterRegistry.counter("ollama.api.requests", 
                "model", model, 
                "status", "error").increment();
            
            throw e;
        }
    }
}
```

## Настройка уведомлений и алертов

### Настройка алертов в Prometheus

Для настройки алертов в Prometheus можно использовать AlertManager. Пример правил алертов (`alerts.yml`):

```yaml
groups:
- name: spring-boot-ollama-alerts
  rules:
  - alert: HighMemoryUsage
    expr: jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} > 0.9
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: "High Memory Usage"
      description: "JVM memory usage is above 90% for more than 5 minutes on {{ $labels.instance }}"
  
  - alert: HighCpuUsage
    expr: system_cpu_usage > 0.8
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: "High CPU Usage"
      description: "CPU usage is above 80% for more than 5 minutes on {{ $labels.instance }}"
  
  - alert: OllamaApiErrors
    expr: rate(ollama_api_requests_total{status="error"}[5m]) > 0.1
    for: 2m
    labels:
      severity: critical
    annotations:
      summary: "High Ollama API Error Rate"
      description: "Ollama API error rate is above 10% for more than 2 minutes"
```

### Настройка алертов по email

AlertManager можно настроить для отправки уведомлений по email, в Slack или другие системы. Пример конфигурации AlertManager для отправки email уведомлений:

```yaml
global:
  resolve_timeout: 5m
  smtp_smarthost: 'smtp.example.com:587'
  smtp_from: 'alertmanager@example.com'
  smtp_auth_username: 'username'
  smtp_auth_password: 'password'

route:
  group_by: ['alertname', 'instance']
  group_wait: 30s
  group_interval: 5m
  repeat_interval: 4h
  receiver: 'email-notifications'
  
receivers:
- name: 'email-notifications'
  email_configs:
  - to: 'team@example.com'
    send_resolved: true
```

## Рекомендуемые настройки для производственной среды

### JVM настройки для оптимальной производительности

```
-Xms2g
-Xmx4g
-XX:+UseG1GC
-XX:+UseStringDeduplication
-XX:+ParallelRefProcEnabled
-XX:+DisableExplicitGC
-XX:MaxGCPauseMillis=200
```

### Настройки безопасности

- Используйте HTTPS для всех эндпоинтов
- Ограничьте доступ к эндпоинтам Actuator только для админов или систем мониторинга
- Используйте аутентификацию для всех API эндпоинтов
- Включите CSRF защиту для веб-интерфейса

### Настройки доступности

- Используйте несколько экземпляров приложения за балансировщиком нагрузки
- Настройте health checks для проверки доступности приложения
- Используйте контейнеры с правильно настроенными health checks для автоматического перезапуска

---
sidebar_position: 1
---

# Установка и запуск

## Требования к системе

Перед установкой и запуском приложения убедитесь, что у вас установлено следующее программное обеспечение:

- **Java 17+** - для запуска Spring Boot приложения
- **PostgreSQL 14+** - для хранения данных (опционально)
- **Ollama** - локальный сервис для запуска нейросетевых моделей
- **Maven 3.8+** - для сборки проекта

### Рекомендуемые системные требования

- **CPU**: 4+ ядер
- **RAM**: минимум 8 ГБ (16+ ГБ рекомендуется для запуска больших нейросетевых моделей)
- **Диск**: 2+ ГБ для приложения, 10+ ГБ для моделей Ollama

## Установка Java 17

### Windows

1. Скачайте Java 17 JDK с [официального сайта Oracle](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) или используйте [Eclipse Adoptium](https://adoptium.net/)
2. Запустите установщик и следуйте инструкциям
3. Настройте переменную окружения `JAVA_HOME`, указав путь к директории JDK
4. Добавьте `%JAVA_HOME%\bin` в переменную окружения `PATH`
5. Проверьте установку командой:
   ```
   java -version
   ```

### Linux (Ubuntu/Debian)

```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

### macOS

```bash
brew install openjdk@17
```

## Установка PostgreSQL

### Windows

1. Скачайте PostgreSQL с [официального сайта](https://www.postgresql.org/download/windows/)
2. Запустите установщик и следуйте инструкциям
3. Запомните пароль пользователя `postgres` и порт (по умолчанию 5432)

### Linux (Ubuntu/Debian)

```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
```

### macOS

```bash
brew install postgresql
```

### Создание базы данных

```bash
sudo -u postgres psql
CREATE DATABASE ollama_insights;
CREATE USER ollama_user WITH ENCRYPTED PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE ollama_insights TO ollama_user;
\q
```

## Установка Ollama

### Windows

1. Скачайте установщик Ollama с [официального сайта](https://ollama.com/download)
2. Запустите установщик и следуйте инструкциям
3. После установки Ollama будет доступен через командную строку и как системный сервис

### Linux

```bash
curl -fsSL https://ollama.com/install.sh | sh
```

### macOS

```bash
brew install ollama
```

## Загрузка нейросетевой модели

После установки Ollama необходимо загрузить модель нейросети:

```bash
ollama pull llama2
```

Для более качественных результатов можно использовать другие модели:

```bash
ollama pull mistral
ollama pull llama2:13b
```

## Клонирование и настройка проекта

1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/your-username/spring-boot-ollama-integration.git
   cd spring-boot-ollama-integration
   ```

2. Настройте соединение с базой данных в `application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/ollama_insights
       username: ollama_user
       password: your_password
   ```

3. Настройте подключение к Ollama API в том же файле:
   ```yaml
   ollama:
     api:
       url: http://localhost:11434
     model: llama2
   ```

## Сборка проекта

Используйте Maven для сборки проекта:

```bash
./mvnw clean install
```

или для Windows:

```bash
mvnw.cmd clean install
```

Если вы хотите пропустить тесты при сборке:

```bash
./mvnw clean install -DskipTests
```

## Запуск приложения

### Запуск Ollama API

Перед запуском приложения убедитесь, что Ollama API запущен:

```bash
ollama serve
```

### Запуск Spring Boot приложения

Запустите приложение с помощью Maven:

```bash
./mvnw spring-boot:run
```

или для Windows:

```bash
mvnw.cmd spring-boot:run
```

Если вы хотите указать профиль:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Запуск с помощью JAR файла

После сборки проекта вы можете запустить приложение как JAR-файл:

```bash
java -jar target/spring-boot-app-0.0.1-SNAPSHOT.jar
```

## Проверка работы приложения

После запуска приложение будет доступно по следующим URL:

- **Веб-интерфейс**: [http://localhost:8080](http://localhost:8080)
- **API**: [http://localhost:8080/api/insights](http://localhost:8080/api/insights)
- **Документация Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **Документация**: [http://localhost:8080/documentation](http://localhost:8080/documentation)
- **Spring Boot Actuator**: [http://localhost:8080/actuator](http://localhost:8080/actuator)

## Настройка для продакшн-окружения

Для использования приложения в продакшн-окружении рекомендуется:

1. Настроить HTTPS через прокси-сервер (например, Nginx)
2. Настроить строгие параметры безопасности для Spring Security
3. Использовать внешний сервис для хранения конфиденциальных данных (например, Spring Cloud Config Server)
4. Настроить логирование в файл или централизованную систему логирования
5. Использовать переменные окружения вместо хардкода значений в `application.yml`

### Пример конфигурации Nginx

```nginx
server {
    listen 443 ssl;
    server_name your-domain.com;
    
    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## Развертывание с Docker

### Сборка Docker-образа

Проект содержит Dockerfile для создания контейнера:

```bash
docker build -t spring-boot-ollama-integration .
```

### Запуск с Docker Compose

Для запуска приложения вместе с PostgreSQL можно использовать Docker Compose:

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/ollama_insights
      - SPRING_DATASOURCE_USERNAME=ollama_user
      - SPRING_DATASOURCE_PASSWORD=your_password
      - OLLAMA_API_URL=http://host.docker.internal:11434

  postgres:
    image: postgres:14
    ports:
      - "5432:5432"
    environment:
      - POSTGRES_DB=ollama_insights
      - POSTGRES_USER=ollama_user
      - POSTGRES_PASSWORD=your_password
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

Запуск:

```bash
docker-compose up -d
```

## Устранение неполадок

### Проблема: Недостаточно места на диске

Если вы столкнулись с ошибкой "Недостаточно места на диске", попробуйте:
1. Очистить кэш Maven: `mvn dependency:purge-local-repository`
2. Удалить неиспользуемые Docker-образы: `docker system prune`
3. Освободить место на диске

### Проблема: Ошибки при подключении к Ollama API

1. Убедитесь, что Ollama API запущен: `ollama serve`
2. Проверьте, доступен ли API по URL: `curl http://localhost:11434/api/tags`
3. Убедитесь, что нужная модель загружена: `ollama list`

### Проблема: Ошибки доступа к PostgreSQL

1. Проверьте, запущен ли сервер PostgreSQL
2. Проверьте правильность учетных данных
3. Убедитесь, что база данных создана и пользователь имеет к ней доступ

## Полезные команды

### Maven

```bash
# Запуск конкретного теста
./mvnw test -Dtest=InsightServiceTest

# Создание отчета о покрытии кода тестами
./mvnw jacoco:report

# Проверка зависимостей на обновления
./mvnw versions:display-dependency-updates
```

### Ollama

```bash
# Просмотр доступных моделей
ollama list

# Удаление модели
ollama rm llama2

# Получение информации о модели
ollama show llama2
```

### Spring Boot

```bash
# Запуск с удаленной отладкой
java -jar -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 target/spring-boot-app-0.0.1-SNAPSHOT.jar
```

## Дополнительные ресурсы

- [Документация Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Документация Ollama API](https://ollama.com/docs)
- [Руководство по Spring Security](https://docs.spring.io/spring-security/reference/index.html)
- [Руководство по Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)

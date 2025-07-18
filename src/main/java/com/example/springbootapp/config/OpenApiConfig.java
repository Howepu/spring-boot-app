package com.example.springbootapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Конфигурация OpenAPI для генерации документации API
 * Интегрируется с SpringDoc для создания Swagger UI
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:Spring Boot Ollama Integration}")
    private String applicationName;

    /**
     * Определяет основную информацию для OpenAPI документации
     * Включает название проекта, описание, версию, контакты и лицензию
     * @return сконфигурированный объект OpenAPI
     */
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

package com.example.springbootapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Конфигурация для создания бина кодировщика паролей.
 * Вынесено в отдельный класс для избежания циклической зависимости.
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Создает бин для кодирования паролей
     * @return экземпляр BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

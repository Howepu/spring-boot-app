package com.example.springbootapp.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация метрик приложения
 */
@Configuration
public class MetricsConfig {

    /**
     * Настраивает аспект для аннотации @Timed
     * @param registry реестр метрик
     * @return сконфигурированный TimedAspect
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}

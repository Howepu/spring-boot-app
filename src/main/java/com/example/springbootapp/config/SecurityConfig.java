package com.example.springbootapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфигурация безопасности приложения
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Настраивает фильтры безопасности для HTTP-запросов
     * @param http объект конфигурации безопасности
     * @return сконфигурированная цепочка фильтров безопасности
     * @throws Exception если возникли проблемы при настройке
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Отключаем CSRF для API
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    // Публичные ресурсы
                    .requestMatchers("/css/**", "/js/**", "/error", "/login", "/register").permitAll()
                    // API для аналитических данных требует авторизации
                    .requestMatchers("/api/insights/**").authenticated()
                    // Все остальные запросы требуют аутентификации
                    .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults()) // Включаем HTTP Basic Auth
            .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .defaultSuccessUrl("/insights", true) // Перенаправление на страницу генерации после входа
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        
        return http.build();
    }

    /**
     * Настраивает DaoAuthenticationProvider с нашим UserDetailsService и PasswordEncoder
     * @return сконфигурированный провайдер аутентификации
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}

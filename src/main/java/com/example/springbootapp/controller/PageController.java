package com.example.springbootapp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер для отображения веб-страниц приложения
 */
@Controller
public class PageController {

    /**
     * Отображает страницу генерации инсайтов
     * @param model модель данных для шаблона
     * @param authentication информация об аутентифицированном пользователе
     * @return имя шаблона для отображения
     */
    @GetMapping("/insights")
    public String insightsPage(Model model, Authentication authentication) {
        // Добавление данных пользователя в модель если требуется
        // authentication.getPrincipal() содержит объект User
        return "insights";
    }

    /**
     * Перенаправление с корневого URL на страницу инсайтов
     * @return перенаправление на /insights
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/insights";
    }
}

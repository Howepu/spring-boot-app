package com.example.springbootapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер для обработки запросов к документации
 */
@Controller
@RequestMapping("/documentation")
public class DocumentationController {

    /**
     * Перенаправляет запрос на главную страницу документации
     * 
     * @return перенаправление на страницу документации
     */
    @GetMapping
    public String documentation() {
        return "redirect:/docs/index.html";
    }
}

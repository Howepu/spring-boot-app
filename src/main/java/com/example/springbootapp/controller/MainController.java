package com.example.springbootapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Главный контроллер приложения для обслуживания React SPA
 */
@Controller
public class MainController {

    /**
     * Отображает главную страницу React SPA
     * @return имя шаблона для отображения
     */
    @GetMapping({"/", "/users", "/users/**"})
    public String index() {
        return "index";
    }
    
    /**
     * Отображает страницу для тестирования API
     * @return имя шаблона для отображения
     */
    @GetMapping("/api-test")
    public String apiTest() {
        return "api-test";
    }
}

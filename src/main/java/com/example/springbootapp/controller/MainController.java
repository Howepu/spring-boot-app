package com.example.springbootapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Главный контроллер приложения
 */
@Controller
public class MainController {

    /**
     * Отображает главную страницу
     * @return имя шаблона для отображения
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }
}

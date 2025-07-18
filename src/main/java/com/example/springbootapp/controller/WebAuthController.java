package com.example.springbootapp.controller;

import com.example.springbootapp.model.User;
import com.example.springbootapp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Контроллер для веб-страниц аутентификации и регистрации
 */
@Controller
public class WebAuthController {

    private final UserService userService;

    public WebAuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Страница входа
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * Страница регистрации
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    /**
     * Обработка формы регистрации
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        // Проверяем, что пользователь с таким именем не существует
        if (userService.getUserByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("errorMessage", "Пользователь с таким именем уже существует");
            return "register";
        }
        
        // Проверяем, что email не занят
        if (userService.getUserByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("errorMessage", "Email " + user.getEmail() + " уже используется");
            return "register";
        }

        try {
            // Сохраняем нового пользователя
            userService.createUser(user);
            // Перенаправляем на страницу входа с сообщением об успешной регистрации
            return "redirect:/login?registered";
        } catch (IllegalArgumentException e) {
            // Дополнительная обработка других возможных ошибок валидации
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }
}

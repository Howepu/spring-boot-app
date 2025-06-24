package com.example.springbootapp.controller;

import com.example.springbootapp.model.User;
import com.example.springbootapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для работы с пользователями
 */
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Отображает список всех пользователей
     * @param model модель представления
     * @return имя шаблона для отображения
     */
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users/list";
    }

    /**
     * Отображает форму создания пользователя
     * @param model модель представления
     * @return имя шаблона для отображения
     */
    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        return "users/form";
    }

    /**
     * Обрабатывает создание нового пользователя
     * @param user объект пользователя
     * @return перенаправление на список пользователей
     */
    @PostMapping
    public String createUser(@ModelAttribute User user) {
        userService.createUser(user);
        return "redirect:/users";
    }

    /**
     * Отображает информацию о пользователе
     * @param id идентификатор пользователя
     * @param model модель представления
     * @return имя шаблона для отображения
     */
    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден")));
        return "users/view";
    }
}

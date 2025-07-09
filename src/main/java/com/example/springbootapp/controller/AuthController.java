package com.example.springbootapp.controller;

import com.example.springbootapp.model.User;
import com.example.springbootapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер для обработки запросов аутентификации и управления пользователями
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Получение информации о текущем аутентифицированном пользователе
     * @return информация о пользователе или сообщение о том, что пользователь не аутентифицирован
     */
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
        if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getPrincipal().equals("anonymousUser")) {
            User user = (User) authentication.getPrincipal();
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("roles", user.getRoles());
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Пользователь не аутентифицирован");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Регистрация нового пользователя
     * @param userMap данные нового пользователя
     * @return созданный пользователь или сообщение об ошибке
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Map<String, String> userMap) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Проверка обязательных полей
            if (!userMap.containsKey("username") || !userMap.containsKey("password") || !userMap.containsKey("email")) {
                response.put("error", true);
                response.put("message", "Необходимо указать имя пользователя, пароль и email");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Проверка, что пользователь с таким именем не существует
            String username = userMap.get("username");
            if (userService.getUserByUsername(username).isPresent()) {
                response.put("error", true);
                response.put("message", "Пользователь с именем " + username + " уже существует");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Создание нового пользователя
            User user = new User();
            user.setUsername(username);
            user.setPassword(userMap.get("password")); // UserService зашифрует пароль
            user.setEmail(userMap.get("email"));
            
            User savedUser = userService.createUser(user);
            
            // Формирование ответа
            response.put("success", true);
            response.put("message", "Пользователь успешно зарегистрирован");
            response.put("username", savedUser.getUsername());
            response.put("email", savedUser.getEmail());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", true);
            response.put("message", "Ошибка при регистрации пользователя: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

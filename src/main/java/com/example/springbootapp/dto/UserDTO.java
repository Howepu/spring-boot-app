package com.example.springbootapp.dto;

import com.example.springbootapp.model.Role;
import com.example.springbootapp.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO для передачи данных пользователя
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    
    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    private String username;
    
    private String password;
    
    @NotBlank(message = "Email обязателен")
    @Email(message = "Email должен быть корректным")
    private String email;
    
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private boolean enabled = true;
    private Set<String> roles;
    
    /**
     * Конвертирует User в UserDTO
     * @param user объект пользователя
     * @return объект UserDTO
     */
    public static UserDTO fromUser(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLogin(user.getLastLogin());
        dto.setEnabled(user.isEnabled());
        
        // Преобразуем Role в набор строк с названиями ролей
        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet()));
        }
        
        return dto;
    }
    
    /**
     * Конвертирует UserDTO в User
     * @param dto объект UserDTO
     * @return объект User
     */
    public static User toUser(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setEnabled(dto.isEnabled());
        
        return user;
    }
}

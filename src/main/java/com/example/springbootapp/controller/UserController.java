package com.example.springbootapp.controller;

import com.example.springbootapp.dto.UserDTO;
import com.example.springbootapp.model.Role;
import com.example.springbootapp.model.User;
import com.example.springbootapp.service.RoleService;
import com.example.springbootapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST-контроллер для управления пользователями
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    /**
     * Получить список всех пользователей
     * @return список DTO пользователей
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers().stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /**
     * Получить пользователя по ID
     * @param id идентификатор пользователя
     * @return DTO пользователя или 404, если пользователь не найден
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isCurrentUser(#id)")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(UserDTO::fromUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Получить пользователя по имени
     * @param username имя пользователя
     * @return DTO пользователя или 404, если пользователь не найден
     */
    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(UserDTO::fromUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.<UserDTO>notFound().build());
    }

    /**
     * Создать нового пользователя
     * @param userDTO данные нового пользователя
     * @return DTO созданного пользователя
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            User user = UserDTO.toUser(userDTO);
            
            // Устанавливаем роли если они указаны
            if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
                Set<Role> roles = new HashSet<>();
                for (String roleName : userDTO.getRoles()) {
                    roleService.getRoleByName(roleName)
                            .ifPresent(roles::add);
                }
                user.setRoles(roles);
            }
            
            User createdUser = userService.createUser(user);
            return new ResponseEntity<>(UserDTO.fromUser(createdUser), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.<UserDTO>badRequest().build();
        }
    }

    /**
     * Обновить данные пользователя
     * @param id идентификатор пользователя
     * @param userDTO обновленные данные пользователя
     * @return DTO обновленного пользователя или 404, если пользователь не найден
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isCurrentUser(#id)")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        try {
            // Проверяем существование пользователя
            if (!userService.getUserById(id).isPresent()) {
                return ResponseEntity.<UserDTO>notFound().build();
            }
            
            User user = UserDTO.toUser(userDTO);
            user.setId(id); // Убеждаемся, что ID правильный
            
            // Устанавливаем роли если они указаны
            if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
                Set<Role> roles = new HashSet<>();
                for (String roleName : userDTO.getRoles()) {
                    roleService.getRoleByName(roleName)
                            .ifPresent(roles::add);
                }
                user.setRoles(roles);
            }
            
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(UserDTO.fromUser(updatedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.<UserDTO>badRequest().build();
        }
    }

    /**
     * Частично обновить данные пользователя
     * @param id идентификатор пользователя
     * @param updates частичные обновления данных пользователя
     * @return DTO обновленного пользователя или 404, если пользователь не найден
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isCurrentUser(#id)")
    public ResponseEntity<UserDTO> partialUpdateUser(@PathVariable Long id, @RequestBody UserDTO updates) {
        return userService.getUserById(id)
                .map(existingUser -> {
                    // Обновляем только указанные поля
                    if (updates.getUsername() != null) {
                        existingUser.setUsername(updates.getUsername());
                    }
                    if (updates.getPassword() != null) {
                        existingUser.setPassword(updates.getPassword());
                    }
                    if (updates.getEmail() != null) {
                        existingUser.setEmail(updates.getEmail());
                    }
                    if (updates.getRoles() != null && !updates.getRoles().isEmpty()) {
                        Set<Role> roles = new HashSet<>();
                        for (String roleName : updates.getRoles()) {
                            roleService.getRoleByName(roleName)
                                    .ifPresent(roles::add);
                        }
                        existingUser.setRoles(roles);
                    }
                    
                    try {
                        User updatedUser = userService.updateUser(existingUser);
                        return ResponseEntity.ok(UserDTO.fromUser(updatedUser));
                    } catch (IllegalArgumentException e) {
                        return new ResponseEntity<UserDTO>(HttpStatus.BAD_REQUEST);
                    }
                })
                .orElse(new ResponseEntity<UserDTO>(HttpStatus.NOT_FOUND));
    }

    /**
     * Удалить пользователя
     * @param id идентификатор пользователя
     * @return 204 No Content при успешном удалении или 404, если пользователь не найден
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.getUserById(id).isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

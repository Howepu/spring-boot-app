package com.example.springbootapp.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Расширенная модель пользователя с поддержкой Spring Security
 * Версия для in-memory хранения без JPA-аннотаций
 */
@Data
@NoArgsConstructor
public class User implements UserDetails {
    private Long id;
    
    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 3, max = 50, message = "Имя пользователя должно быть от 3 до 50 символов")
    private String username;
    
    @NotBlank(message = "Пароль обязателен")
    private String password;
    
    @NotBlank(message = "Email обязателен")
    @Email(message = "Email должен быть корректным")
    private String email;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime lastLogin;
    
    private boolean accountNonExpired = true;
    
    private boolean accountNonLocked = true;
    
    private boolean credentialsNonExpired = true;
    
    private boolean enabled = true;
    
    private Set<Role> roles = new HashSet<>();
    
    public User(Long id, String username, String password, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Автоматически устанавливает дату создания при создании объекта
     */
    public void initCreatedAtIfNull() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
    
    /**
     * Возвращает список ролей пользователя в виде коллекции GrantedAuthority
     * @return коллекция ролей
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    public void addRole(Role role) {
        this.roles.add(role);
    }
}

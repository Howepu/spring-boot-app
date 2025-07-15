package com.example.springbootapp.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Утилитарный класс для работы с безопасностью
 */
@Component("securityUtils")
public class SecurityUtils {

    /**
     * Проверяет, является ли текущий аутентифицированный пользователь пользователем с указанным ID
     * @param userId идентификатор пользователя
     * @return true, если текущий пользователь имеет указанный ID
     */
    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            return ((org.springframework.security.core.userdetails.User) principal).getUsername().equals(userId.toString());
        } else if (principal instanceof com.example.springbootapp.model.User) {
            return ((com.example.springbootapp.model.User) principal).getId().equals(userId);
        }
        
        return false;
    }
}

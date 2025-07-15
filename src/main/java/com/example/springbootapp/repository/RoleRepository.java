package com.example.springbootapp.repository;

import com.example.springbootapp.model.Role;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory реализация репозитория для работы с ролями пользователей
 */
@Repository
public class RoleRepository {
    private final Map<Long, Role> roles = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);
    
    /**
     * Находит роль по имени
     * @param name название роли
     * @return Optional с ролью или пустой, если роль не найдена
     */
    public Optional<Role> findByName(String name) {
        return roles.values().stream()
                .filter(role -> name.equals(role.getName()))
                .findFirst();
    }
    
    /**
     * Проверяет существование роли по имени
     * @param name название роли
     * @return true если роль существует, иначе false
     */
    public boolean existsByName(String name) {
        return roles.values().stream()
                .anyMatch(role -> name.equals(role.getName()));
    }
    
    /**
     * Сохраняет роль
     * @param role объект роли
     * @return сохраненная роль с присвоенным ID
     */
    public Role save(Role role) {
        if (role.getId() == null) {
            role.setId(idCounter.getAndIncrement());
        }
        roles.put(role.getId(), role);
        return role;
    }
    
    /**
     * Находит роль по ID
     * @param id идентификатор роли
     * @return объект Optional с ролью
     */
    public Optional<Role> findById(Long id) {
        return Optional.ofNullable(roles.get(id));
    }
    
    /**
     * Получает все роли
     * @return список ролей
     */
    public List<Role> findAll() {
        return new ArrayList<>(roles.values());
    }
    
    /**
     * Удаляет роль по ID
     * @param id идентификатор роли
     */
    public void deleteById(Long id) {
        roles.remove(id);
    }
}

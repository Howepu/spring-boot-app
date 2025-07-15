package com.example.springbootapp.service;

import com.example.springbootapp.model.Role;
import com.example.springbootapp.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления ролями пользователей
 */
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Инициализация базовых ролей при запуске
     */
    @PostConstruct
    public void init() {
        // Создаем базовые роли в системе, если они отсутствуют
        createRoleIfNotExists("USER");
        createRoleIfNotExists("ADMIN");
        createRoleIfNotExists("MANAGER");
    }

    /**
     * Создает роль, если она не существует
     * @param name название роли
     * @return роль
     */
    public Role createRoleIfNotExists(String name) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role role = new Role(name);
                    return roleRepository.save(role);
                });
    }

    /**
     * Находит роль по имени
     * @param name название роли
     * @return объект Optional с ролью
     */
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }
    
    /**
     * Находит роль по ID
     * @param id идентификатор роли
     * @return объект Optional с ролью
     */
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }
    
    /**
     * Получает все роли
     * @return список ролей
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    
    /**
     * Удаляет роль по ID
     * @param id идентификатор роли
     */
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
    
    /**
     * Создает новую роль
     * @param role объект роли
     * @return созданная роль
     */
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    /**
     * Обновляет роль
     * @param role объект роли
     * @return обновленная роль
     */
    public Role updateRole(Role role) {
        return roleRepository.save(role);
    }
}

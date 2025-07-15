package com.example.springbootapp.repository;

import com.example.springbootapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA-репозиторий для работы с ролями пользователей
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Находит роль по имени
     * @param name название роли
     * @return Optional с ролью или пустой, если роль не найдена
     */
    Optional<Role> findByName(String name);
    
    /**
     * Проверяет существование роли по имени
     * @param name название роли
     * @return true если роль существует, иначе false
     */
    boolean existsByName(String name);
}

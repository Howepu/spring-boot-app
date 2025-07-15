package com.example.springbootapp.repository;

import com.example.springbootapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA-репозиторий для работы с пользователями
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Находит пользователя по имени (логину)
     * @param username имя пользователя
     * @return объект Optional с пользователем
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Проверяет, существует ли пользователь с таким именем
     * @param username имя пользователя
     * @return true если пользователь существует, иначе false
     */
    boolean existsByUsername(String username);
    
    /**
     * Находит пользователя по email
     * @param email электронная почта пользователя
     * @return объект Optional с пользователем
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Проверяет, существует ли пользователь с таким email
     * @param email электронная почта пользователя
     * @return true если пользователь существует, иначе false
     */
    boolean existsByEmail(String email);
}

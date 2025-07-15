package com.example.springbootapp.repository;

import com.example.springbootapp.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory реализация репозитория для работы с пользователями
 */
@Repository
public class UserRepository {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);
    
    /**
     * Находит пользователя по имени (логину)
     * @param username имя пользователя
     * @return объект Optional с пользователем
     */
    public Optional<User> findByUsername(String username) {
        return users.values().stream()
                .filter(user -> username.equals(user.getUsername()))
                .findFirst();
    }
    
    /**
     * Проверяет, существует ли пользователь с таким именем
     * @param username имя пользователя
     * @return true если пользователь существует, иначе false
     */
    public boolean existsByUsername(String username) {
        return users.values().stream()
                .anyMatch(user -> username.equals(user.getUsername()));
    }
    
    /**
     * Находит пользователя по email
     * @param email электронная почта пользователя
     * @return объект Optional с пользователем
     */
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
    }
    
    /**
     * Проверяет, существует ли пользователь с таким email
     * @param email электронная почта пользователя
     * @return true если пользователь существует, иначе false
     */
    public boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> email.equals(user.getEmail()));
    }
    
    /**
     * Сохраняет пользователя
     * @param user объект пользователя
     * @return сохраненный пользователь с присвоенным ID
     */
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idCounter.getAndIncrement());
        }
        users.put(user.getId(), user);
        return user;
    }
    
    /**
     * Находит пользователя по ID
     * @param id идентификатор пользователя
     * @return объект Optional с пользователем
     */
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
    
    /**
     * Получает всех пользователей
     * @return список пользователей
     */
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
    
    /**
     * Удаляет пользователя по ID
     * @param id идентификатор пользователя
     */
    public void deleteById(Long id) {
        users.remove(id);
    }
}

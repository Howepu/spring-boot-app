package com.example.springbootapp.repository;

import com.example.springbootapp.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с пользователями
 */
@Repository
public class UserRepository {
    
    private final List<User> users = new ArrayList<>();
    
    /**
     * Находит всех пользователей
     * @return список пользователей
     */
    public List<User> findAll() {
        return new ArrayList<>(users);
    }
    
    /**
     * Находит пользователя по ID
     * @param id идентификатор пользователя
     * @return объект Optional с пользователем
     */
    public Optional<User> findById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }
    
    /**
     * Находит пользователя по имени (логину)
     * @param username имя пользователя
     * @return объект Optional с пользователем
     */
    public Optional<User> findByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }
    
    /**
     * Проверяет, существует ли пользователь с таким именем
     * @param username имя пользователя
     * @return true, если пользователь существует
     */
    public boolean existsByUsername(String username) {
        return users.stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }
    
    /**
     * Сохраняет пользователя
     * @param user объект пользователя
     * @return сохраненный пользователь
     */
    public User save(User user) {
        // Если пользователь новый - добавляем его, иначе обновляем существующего
        if (user.getId() == null) {
            user.setId((long) (users.size() + 1));
            users.add(user);
        } else {
            // Удаляем старую версию пользователя и добавляем обновленную
            users.removeIf(existingUser -> existingUser.getId().equals(user.getId()));
            users.add(user);
        }
        return user;
    }
    
    /**
     * Удаляет пользователя по ID
     * @param id идентификатор пользователя
     */
    public void deleteById(Long id) {
        users.removeIf(user -> user.getId().equals(id));
    }
}

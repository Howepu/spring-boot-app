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
     * Сохраняет пользователя
     * @param user объект пользователя
     * @return сохраненный пользователь
     */
    public User save(User user) {
        if (user.getId() == null) {
            user.setId((long) (users.size() + 1));
        }
        users.add(user);
        return user;
    }
}

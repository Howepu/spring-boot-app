package com.example.springbootapp.service;

import com.example.springbootapp.model.User;
import com.example.springbootapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с пользователями
 */
@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Получить всех пользователей
     * @return список пользователей
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Найти пользователя по ID
     * @param id идентификатор пользователя
     * @return объект Optional с пользователем
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    
    /**
     * Создать нового пользователя
     * @param user объект пользователя
     * @return созданный пользователь
     */
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    /**
     * Обновить данные пользователя
     * @param user объект пользователя с обновленными данными
     * @return обновленный пользователь
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }
}

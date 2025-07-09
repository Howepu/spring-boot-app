package com.example.springbootapp.service;

import com.example.springbootapp.model.Role;
import com.example.springbootapp.model.User;
import com.example.springbootapp.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Сервис для работы с пользователями, реализующий UserDetailsService для Spring Security
 */
@Service
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Инициализация пользователя-администратора по умолчанию
     */
    @PostConstruct
    public void init() {
        // Создаем администратора только если он не существует
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin")); // В реальной системе нужен более сложный пароль
            admin.setEmail("admin@example.com");
            
            Set<Role> roles = new HashSet<>();
            roles.add(new Role("ADMIN"));
            roles.add(new Role("USER"));
            admin.setRoles(roles);
            
            userRepository.save(admin);
        }
        
        // Создаем обычного пользователя
        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user"));
            user.setEmail("user@example.com");
            
            Set<Role> roles = new HashSet<>();
            roles.add(new Role("USER"));
            user.setRoles(roles);
            
            userRepository.save(user);
        }
    }
    
    /**
     * Реализация метода UserDetailsService для загрузки пользователя по имени
     * @param username имя пользователя
     * @return объект UserDetails с данными пользователя
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
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
     * Найти пользователя по имени
     * @param username имя пользователя
     * @return объект Optional с пользователем
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Создать нового пользователя
     * @param user объект пользователя
     * @return созданный пользователь
     */
    public User createUser(User user) {
        // Шифруем пароль перед сохранением
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Если роли не указаны, добавляем роль USER по умолчанию
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            roles.add(new Role("USER"));
            user.setRoles(roles);
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Обновить данные пользователя
     * @param user объект пользователя с обновленными данными
     * @return обновленный пользователь
     */
    public User updateUser(User user) {
        // Если пароль изменен (не начинается с $2a$), шифруем его
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }
    
    /**
     * Удалить пользователя по ID
     * @param id идентификатор пользователя
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

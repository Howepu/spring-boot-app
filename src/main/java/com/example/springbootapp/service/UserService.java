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

import java.time.LocalDateTime;
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
    private final RoleService roleService;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
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
            admin.initCreatedAtIfNull();
            
            Set<Role> roles = new HashSet<>();
            roles.add(roleService.createRoleIfNotExists("ADMIN"));
            roles.add(roleService.createRoleIfNotExists("USER"));
            admin.setRoles(roles);
            
            userRepository.save(admin);
        }
        
        // Создаем обычного пользователя
        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user"));
            user.setEmail("user@example.com");
            user.initCreatedAtIfNull();
            
            Set<Role> roles = new HashSet<>();
            roles.add(roleService.createRoleIfNotExists("USER"));
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
     * @throws IllegalArgumentException если пользователь с таким именем или email уже существует
     */
    public User createUser(User user) {
        // Проверяем, что пользователь с таким именем или email не существует
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Пользователь с именем " + user.getUsername() + " уже существует");
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Пользователь с email " + user.getEmail() + " уже существует");
        }
        
        // Шифруем пароль перед сохранением
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Устанавливаем дату создания
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        
        // Если роли не указаны, добавляем роль USER по умолчанию
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            roles.add(roleService.createRoleIfNotExists("USER"));
            user.setRoles(roles);
        } else {
            // Преобразуем строковые имена ролей в объекты Role из базы данных
            Set<Role> persistedRoles = new HashSet<>();
            for (Role role : user.getRoles()) {
                persistedRoles.add(roleService.createRoleIfNotExists(role.getName()));
            }
            user.setRoles(persistedRoles);
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Обновить данные пользователя
     * @param user объект пользователя с обновленными данными
     * @return обновленный пользователь
     */
    public User updateUser(User user) {
        // Проверяем существование пользователя
        User existingUser = userRepository.findById(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + user.getId() + " не найден"));
        
        // Проверяем, не занято ли имя пользователя другим пользователем
        if (!existingUser.getUsername().equals(user.getUsername()) && 
            userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Имя пользователя " + user.getUsername() + " уже занято");
        }
        
        // Проверяем, не занят ли email другим пользователем
        if (!existingUser.getEmail().equals(user.getEmail()) && 
            userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email " + user.getEmail() + " уже занят");
        }
        
        // Если пароль изменен (не начинается с $2a$), шифруем его
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // Если пароль не менялся, используем существующий
            user.setPassword(existingUser.getPassword());
        }
        
        // Обновляем поле lastLogin если оно было изменено
        if (user.getLastLogin() != null) {
            existingUser.setLastLogin(user.getLastLogin());
        }
        
        // Если роли были изменены, обновляем их
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            Set<Role> persistedRoles = new HashSet<>();
            for (Role role : user.getRoles()) {
                persistedRoles.add(roleService.createRoleIfNotExists(role.getName()));
            }
            existingUser.setRoles(persistedRoles);
        }
        
        // Обновляем остальные поля
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setEnabled(user.isEnabled());
        existingUser.setAccountNonLocked(user.isAccountNonLocked());
        existingUser.setAccountNonExpired(user.isAccountNonExpired());
        existingUser.setCredentialsNonExpired(user.isCredentialsNonExpired());
        
        return userRepository.save(existingUser);
    }
    
    /**
     * Обновить последнюю дату входа пользователя
     * @param username имя пользователя
     */
    public void updateLastLogin(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        });
    }
    
    /**
     * Удалить пользователя по ID
     * @param id идентификатор пользователя
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

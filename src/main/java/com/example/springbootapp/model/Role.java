package com.example.springbootapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс для ролей пользователей (in-memory версия)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    private Long id;
    
    private String name;
    
    public Role(String name) {
        this.name = name;
    }
}

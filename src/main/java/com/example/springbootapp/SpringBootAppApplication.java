package com.example.springbootapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.example.springbootapp.config.OllamaConfig;

@SpringBootApplication
@EnableConfigurationProperties(OllamaConfig.class)
public class SpringBootAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootAppApplication.class, args);
	}

}

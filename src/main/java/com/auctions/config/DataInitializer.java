package com.auctions.config;

import com.auctions.persistence.entity.UserEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.auctions.persistence.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeData() {

        return args -> {
            if (userRepository.count() == 0) {

                UserEntity admin = new UserEntity();

                admin.setUsername("admin");
                admin.setEmail("admin@crm.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setAdmin(true);
                admin.setActive(true);

                userRepository.save(admin);

                log.info("Default admin user created with username: admin and password: admin123");

            } else {

                log.info("Skipping default admin user creation as users already exist");
            }
        };
    }
} 
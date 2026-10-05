package com.grocery.system.security;

import com.grocery.system.entity.Role;
import com.grocery.system.entity.User;
import com.grocery.system.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initData() {
        // Clear or seed fresh users on startup
        if (userRepository.findByUsername("admin1").isEmpty()) {
            User admin1 = new User();
            admin1.setUsername("admin1");
            admin1.setPassword(passwordEncoder.encode("password123"));
            admin1.setRole(Role.ROLE_ADMIN);
            userRepository.save(admin1);
        }

        if (userRepository.findByUsername("admin2").isEmpty()) {
            User admin2 = new User();
            admin2.setUsername("admin2");
            admin2.setPassword(passwordEncoder.encode("password123"));
            admin2.setRole(Role.ROLE_ADMIN);
            userRepository.save(admin2);
        }

        if (userRepository.findByUsername("admin3").isEmpty()) {
            User admin3 = new User();
            admin3.setUsername("admin3");
            admin3.setPassword(passwordEncoder.encode("password123"));
            admin3.setRole(Role.ROLE_ADMIN);
            userRepository.save(admin3);
        }
    }
}
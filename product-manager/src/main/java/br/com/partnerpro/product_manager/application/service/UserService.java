package br.com.partnerpro.product_manager.application.service;

import br.com.partnerpro.product_manager.domain.entity.User;
import br.com.partnerpro.product_manager.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public User registerUser(String username, String password, String email, String fullName) {
        log.info("Registering new user: {}", username);
        
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .fullName(fullName)
                .enabled(true)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", username);
        
        return savedUser;
    }
}

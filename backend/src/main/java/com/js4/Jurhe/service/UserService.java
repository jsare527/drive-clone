package com.js4.Jurhe.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.js4.Jurhe.model.User;
import com.js4.Jurhe.repo.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerNewUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username is already taken");
        }

        User user = new User();
        user.setUsername(username);
        final String encodedPass = passwordEncoder.encode(password);
        user.setPassword(encodedPass);

        return userRepository.save(user);
    }

    public User findByUserName(String username) {
        return userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}

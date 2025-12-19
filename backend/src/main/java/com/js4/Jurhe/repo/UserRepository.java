package com.js4.Jurhe.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.js4.Jurhe.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

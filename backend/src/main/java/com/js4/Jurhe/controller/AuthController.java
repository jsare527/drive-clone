package com.js4.Jurhe.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.js4.Jurhe.dto.UserDTO;
import com.js4.Jurhe.model.User;
import com.js4.Jurhe.service.JwtService;
import com.js4.Jurhe.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        try {
            final User user = userService.registerNewUser(userDTO.getUsername(), userDTO.getPassword());
            final String token = jwtService.generateToken(user.getUsername());
            final Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "User registered and logged in");
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword())
        );

        if (!authentication.isAuthenticated()) {
            return ResponseEntity.badRequest().body("Invalid user request");
        }

        final String token = jwtService.generateToken(userDTO.getUsername());
        final Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/tokenValid")
    public ResponseEntity<?> tokenValid(@RequestBody String token) {
        final boolean tokenValid = jwtService.isTokenValid(token);
        if (tokenValid) return ResponseEntity.ok("");
        return ResponseEntity.badRequest().body("Unauthorized");
    }
}

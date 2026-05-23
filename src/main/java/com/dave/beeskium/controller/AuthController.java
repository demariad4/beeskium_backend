package com.dave.beeskium.controller;

import com.dave.beeskium.model.User;
import com.dave.beeskium.security.JwtUtil;
import com.dave.beeskium.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        if (userService.verifyUser(email, password)) {
            User user = userService.findByEmail(email);
            String token = jwtUtil.generateToken(email, user.getRole());
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenziali errate");
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody User user) {
        User saved = userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
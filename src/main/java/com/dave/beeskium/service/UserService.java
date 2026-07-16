package com.dave.beeskium.service;

import com.dave.beeskium.dto.RegisterRequest;
import com.dave.beeskium.dto.UpdateProfileRequest;
import com.dave.beeskium.dto.UserResponse;
import com.dave.beeskium.model.User;
import com.dave.beeskium.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email già in uso.");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public boolean verifyUser(String email, String password) {
        return userRepository.findByEmail(email)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato."));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserProfile(String email) {
        return UserResponse.from(findByEmail(email));
    }

    @Transactional
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = findByEmail(email);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public User makeAdmin(String email) {
        User user = findByEmail(email);
        String role = user.getRole() == null ? "" : user.getRole().trim().toUpperCase();

        if (!"ROLE_ADMIN".equals(role)) {
            user.setRole("ROLE_ADMIN");
            userRepository.save(user);
        }

        return user;
    }

    @Transactional(readOnly = true)
    public boolean isAdmin(String email) {
        User user = findByEmail(email);
        return "ROLE_ADMIN".equals(normalizeRole(user.getRole()));
    }

    private String normalizeRole(String role) {
        if (role == null) {
            return "USER";
        }

        String normalizedRole = role.trim().toUpperCase();
        if (normalizedRole.isBlank()) {
            return "USER";
        }

        return normalizedRole.startsWith("ROLE_") ? normalizedRole : "ROLE_" + normalizedRole;
    }
}

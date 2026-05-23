package com.dave.beeskium.service;

import com.dave.beeskium.model.User;
import com.dave.beeskium.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User registerUser(User user) {
        user.setRole("ROLE_USER");
        user.setId(null);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email già in uso.");
        }

        return userRepository.save(user);
    }

    @Transactional
    public boolean verifyUser(String email, String password) {
        return userRepository.existsByEmailAndPassword(email, password);
    }

    @Transactional
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
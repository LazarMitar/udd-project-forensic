package com.example.ddmdemo.service.impl;

import com.example.ddmdemo.dto.RegisterRequest;
import com.example.ddmdemo.model.User;
import com.example.ddmdemo.respository.UserRepository;
import com.example.ddmdemo.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Korisničko ime je već zauzeto");
        }
        var user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null && !request.getRole().isBlank() ? request.getRole() : "USER");
        return userRepository.save(user);
    }
}

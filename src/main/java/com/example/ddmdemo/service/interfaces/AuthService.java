package com.example.ddmdemo.service.interfaces;

import com.example.ddmdemo.dto.RegisterRequest;
import com.example.ddmdemo.model.User;

public interface AuthService {
    User register(RegisterRequest request);
}

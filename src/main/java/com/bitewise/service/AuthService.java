package com.bitewise.service;

import com.bitewise.dto.request.LoginRequest;
import com.bitewise.dto.request.RegisterRequest;
import com.bitewise.dto.response.AuthResponse;
import com.bitewise.dto.response.UserResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    void logout(String refreshToken);
    UserResponse getCurrentUser(Long userId);
}

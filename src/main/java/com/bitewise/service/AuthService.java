package com.bitewise.service;

import com.bitewise.dto.request.LoginRequest;
import com.bitewise.dto.request.RegisterRequest;
import com.bitewise.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

}
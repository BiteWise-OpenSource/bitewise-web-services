package com.bitewise.service;

import com.bitewise.dto.request.UpdateUserRequest;
import com.bitewise.dto.response.UserResponse;

public interface UserService {

    UserResponse getCurrentUser(String email);

    UserResponse getUserById(Long id);

    UserResponse updateUser(String email, UpdateUserRequest request);

    void deleteUser(String email);
}

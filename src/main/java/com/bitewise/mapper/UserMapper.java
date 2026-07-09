package com.bitewise.mapper;

import com.bitewise.dto.request.RegisterRequest;
import com.bitewise.dto.response.UserResponse;
import com.bitewise.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .age(user.getAge())
                .weight(user.getWeight())
                .height(user.getHeight())
                .gender(user.getGender())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public User toEntity(RegisterRequest request) {
        if (request == null) {
            return null;
        }

        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .age(request.getAge())
                .weight(request.getWeight())
                .height(request.getHeight())
                .gender(request.getGender())
                .build();
    }
}

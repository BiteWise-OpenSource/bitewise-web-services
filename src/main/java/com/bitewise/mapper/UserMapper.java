package com.bitewise.mapper;

import com.bitewise.dto.response.UserResponse;
import com.bitewise.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .age(user.getAge())
                .weight(user.getWeight())
                .height(user.getHeight())
                .gender(user.getGender())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

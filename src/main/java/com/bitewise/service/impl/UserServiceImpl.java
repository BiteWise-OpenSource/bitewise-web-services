package com.bitewise.service.impl;

import com.bitewise.dto.request.UpdateUserRequest;
import com.bitewise.dto.response.UserResponse;
import com.bitewise.entity.User;
import com.bitewise.exception.ResourceNotFoundException;
import com.bitewise.mapper.UserMapper;
import com.bitewise.repository.UserRepository;
import com.bitewise.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse getCurrentUser(String email) {
        return userMapper.toResponse(findByEmail(email));
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(String email, UpdateUserRequest request) {
        User user = findByEmail(email);

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getAge() != null) user.setAge(request.getAge());
        if (request.getWeight() != null) user.setWeight(request.getWeight());
        if (request.getHeight() != null) user.setHeight(request.getHeight());
        if (request.getGender() != null) user.setGender(request.getGender());

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(String email) {
        userRepository.delete(findByEmail(email));
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }
}

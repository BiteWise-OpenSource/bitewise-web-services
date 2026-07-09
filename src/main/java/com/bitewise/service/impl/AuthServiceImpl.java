package com.bitewise.service.impl;

import com.bitewise.dto.request.LoginRequest;
import com.bitewise.dto.request.RegisterRequest;
import com.bitewise.dto.response.AuthResponse;
import com.bitewise.dto.response.UserResponse;
import com.bitewise.entity.Session;
import com.bitewise.entity.User;
import com.bitewise.entity.UserRole;
import com.bitewise.exception.BadCredentialsException;
import com.bitewise.exception.EmailAlreadyExistsException;
import com.bitewise.exception.ResourceNotFoundException;
import com.bitewise.mapper.UserMapper;
import com.bitewise.repository.SessionRepository;
import com.bitewise.repository.UserRepository;
import com.bitewise.service.AuthService;
import com.bitewise.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Validate email doesn't exist
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            throw new EmailAlreadyExistsException("Email already registered");
        }

        // Create new user
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        // Generate tokens
        return generateAuthResponse(savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", request.getEmail());
                    return new BadCredentialsException("Invalid email or password");
                });

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password for user: {}", request.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }

        log.info("User logged in successfully: {}", request.getEmail());
        return generateAuthResponse(user);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Refreshing token");

        if (!jwtUtil.validateToken(refreshToken)) {
            log.warn("Invalid refresh token");
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        Session session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> {
                    log.warn("Session not found for refresh token");
                    return new ResourceNotFoundException("Session not found");
                });

        if (session.isExpired() || !session.getIsActive()) {
            log.warn("Session expired or inactive");
            session.setIsActive(false);
            sessionRepository.save(session);
            throw new BadCredentialsException("Refresh token expired");
        }

        User user = session.getUser();
        return generateAuthResponse(user);
    }

    @Override
    public void logout(String refreshToken) {
        log.info("Logging out user");

        Session session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        session.setIsActive(false);
        sessionRepository.save(session);
        log.info("User logged out successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toDTO(user);
    }

    private AuthResponse generateAuthResponse(User user) {
        // Generate access token
        String accessToken = jwtUtil.generateAccessToken(
                user.getEmail(),
                user.getRole().toString(),
                user.getId()
        );

        // Generate refresh token
        String refreshToken = jwtUtil.generateRefreshToken(
                user.getEmail(),
                user.getId()
        );

        // Create session
        Session session = Session.builder()
                .user(user)
                .refreshToken(refreshToken)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .isActive(true)
                .build();

        sessionRepository.save(session);

        // Build response
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getTokenExpirationTime())
                .user(AuthResponse.UserDto.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .role(user.getRole().toString())
                        .build())
                .build();
    }
}

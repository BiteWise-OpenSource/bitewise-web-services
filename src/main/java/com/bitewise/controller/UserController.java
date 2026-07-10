package com.bitewise.controller;

import com.bitewise.dto.request.UpdateUserRequest;
import com.bitewise.dto.response.UserResponse;
import com.bitewise.security.UserPrincipal;
import com.bitewise.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.getCurrentUser(principal.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(@AuthenticationPrincipal UserPrincipal principal,
                                                            @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(principal.getUsername(), request));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        userService.deleteUser(principal.getUsername());
        return ResponseEntity.noContent().build();
    }
}

package com.bitewise.controller;

import com.bitewise.dto.request.NutritionProfileRequest;
import com.bitewise.dto.response.NutritionProfileResponse;
import com.bitewise.security.UserPrincipal;
import com.bitewise.service.NutritionProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nutrition-profile")
@RequiredArgsConstructor
public class NutritionProfileController {

    private final NutritionProfileService nutritionProfileService;

    @PostMapping
    public ResponseEntity<NutritionProfileResponse> createOrUpdate(@AuthenticationPrincipal UserPrincipal principal,
                                                                     @Valid @RequestBody NutritionProfileRequest request) {
        return ResponseEntity.ok(nutritionProfileService.createOrUpdate(principal.getUsername(), request));
    }

    @GetMapping
    public ResponseEntity<NutritionProfileResponse> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(nutritionProfileService.getByUser(principal.getUsername()));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProfile(@AuthenticationPrincipal UserPrincipal principal) {
        nutritionProfileService.delete(principal.getUsername());
        return ResponseEntity.noContent().build();
    }
}

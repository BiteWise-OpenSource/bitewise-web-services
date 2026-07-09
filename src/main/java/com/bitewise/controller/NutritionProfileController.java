package com.bitewise.controller;

import com.bitewise.dto.request.CreateNutritionProfileRequest;
import com.bitewise.dto.request.UpdateNutritionProfileRequest;
import com.bitewise.dto.response.NutritionProfileResponse;
import com.bitewise.service.NutritionProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/nutrition-profiles")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class NutritionProfileController {

    private final NutritionProfileService profileService;

    @PostMapping
    public ResponseEntity<NutritionProfileResponse> createProfile(
            @Valid @RequestBody CreateNutritionProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getDetails();
        log.info("Creating nutrition profile for userId: {}", userId);
        NutritionProfileResponse response = profileService.createProfile(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<NutritionProfileResponse> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getDetails();
        log.info("Fetching nutrition profile for userId: {}", userId);
        NutritionProfileResponse response = profileService.getProfileByUserId(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<NutritionProfileResponse> getProfileByUserId(@PathVariable Long userId) {
        log.info("Fetching nutrition profile for userId: {}", userId);
        NutritionProfileResponse response = profileService.getProfileByUserId(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<NutritionProfileResponse> updateProfile(
            @Valid @RequestBody UpdateNutritionProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getDetails();
        log.info("Updating nutrition profile for userId: {}", userId);
        NutritionProfileResponse response = profileService.updateProfile(userId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{profileId}/allergies/{allergyId}")
    public ResponseEntity<Void> addAllergy(
            @PathVariable Long profileId,
            @PathVariable Long allergyId) {
        log.info("Adding allergy {} to profile {}", allergyId, profileId);
        profileService.addAllergy(profileId, allergyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{profileId}/allergies/{allergyId}")
    public ResponseEntity<Void> removeAllergy(
            @PathVariable Long profileId,
            @PathVariable Long allergyId) {
        log.info("Removing allergy {} from profile {}", allergyId, profileId);
        profileService.removeAllergy(profileId, allergyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{profileId}/dietary-preferences/{preferenceId}")
    public ResponseEntity<Void> addDietaryPreference(
            @PathVariable Long profileId,
            @PathVariable Long preferenceId) {
        log.info("Adding dietary preference {} to profile {}", preferenceId, profileId);
        profileService.addDietaryPreference(profileId, preferenceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{profileId}/dietary-preferences/{preferenceId}")
    public ResponseEntity<Void> removeDietaryPreference(
            @PathVariable Long profileId,
            @PathVariable Long preferenceId) {
        log.info("Removing dietary preference {} from profile {}", preferenceId, profileId);
        profileService.removeDietaryPreference(profileId, preferenceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

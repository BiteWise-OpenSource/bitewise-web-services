package com.bitewise.service.impl;

import com.bitewise.dto.request.CreateNutritionProfileRequest;
import com.bitewise.dto.request.UpdateNutritionProfileRequest;
import com.bitewise.dto.response.NutritionProfileResponse;
import com.bitewise.entity.*;
import com.bitewise.exception.ResourceNotFoundException;
import com.bitewise.mapper.NutritionProfileMapper;
import com.bitewise.repository.*;
import com.bitewise.service.NutritionProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NutritionProfileServiceImpl implements NutritionProfileService {

    private final NutritionProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final AllergyRepository allergyRepository;
    private final DietaryPreferenceRepository preferenceRepository;
    private final NutritionProfileMapper mapper;

    @Override
    public NutritionProfileResponse createProfile(Long userId, CreateNutritionProfileRequest request) {
        log.info("Creating nutrition profile for userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if profile already exists
        if (profileRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("User already has a nutrition profile");
        }

        NutritionProfile profile = NutritionProfile.builder()
                .user(user)
                .currentWeight(request.getCurrentWeight())
                .targetWeight(request.getTargetWeight())
                .height(request.getHeight())
                .age(request.getAge())
                .gender(request.getGender())
                .activityLevel(request.getActivityLevel())
                .primaryGoal(request.getPrimaryGoal())
                .dietType(request.getDietType())
                .build();

        // Set allergies
        if (request.getAllergyIds() != null && !request.getAllergyIds().isEmpty()) {
            request.getAllergyIds().forEach(allergyId ->
                    allergyRepository.findById(allergyId).ifPresent(profile.getAllergies()::add)
            );
        }

        // Set dietary preferences
        if (request.getDietaryPreferenceIds() != null && !request.getDietaryPreferenceIds().isEmpty()) {
            request.getDietaryPreferenceIds().forEach(prefId ->
                    preferenceRepository.findById(prefId).ifPresent(profile.getDietaryPreferences()::add)
            );
        }

        // Calculate daily calories
        profile.setDailyCalorieTarget(profile.calculateDailyCalories());

        NutritionProfile savedProfile = profileRepository.save(profile);
        log.info("Nutrition profile created with id: {}", savedProfile.getId());

        return mapper.toDTO(savedProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public NutritionProfileResponse getProfileByUserId(Long userId) {
        log.info("Fetching nutrition profile for userId: {}", userId);

        NutritionProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Nutrition profile not found for user"));

        return mapper.toDTO(profile);
    }

    @Override
    public NutritionProfileResponse updateProfile(Long userId, UpdateNutritionProfileRequest request) {
        log.info("Updating nutrition profile for userId: {}", userId);

        NutritionProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Nutrition profile not found"));

        if (request.getCurrentWeight() != null) {
            profile.setCurrentWeight(request.getCurrentWeight());
        }
        if (request.getTargetWeight() != null) {
            profile.setTargetWeight(request.getTargetWeight());
        }
        if (request.getHeight() != null) {
            profile.setHeight(request.getHeight());
        }
        if (request.getAge() != null) {
            profile.setAge(request.getAge());
        }
        if (request.getGender() != null) {
            profile.setGender(request.getGender());
        }
        if (request.getActivityLevel() != null) {
            profile.setActivityLevel(request.getActivityLevel());
        }
        if (request.getPrimaryGoal() != null) {
            profile.setPrimaryGoal(request.getPrimaryGoal());
        }
        if (request.getDietType() != null) {
            profile.setDietType(request.getDietType());
        }

        // Update allergies if provided
        if (request.getAllergyIds() != null) {
            profile.getAllergies().clear();
            request.getAllergyIds().forEach(allergyId ->
                    allergyRepository.findById(allergyId).ifPresent(profile.getAllergies()::add)
            );
        }

        // Update dietary preferences if provided
        if (request.getDietaryPreferenceIds() != null) {
            profile.getDietaryPreferences().clear();
            request.getDietaryPreferenceIds().forEach(prefId ->
                    preferenceRepository.findById(prefId).ifPresent(profile.getDietaryPreferences()::add)
            );
        }

        // Recalculate daily calories
        profile.setDailyCalorieTarget(profile.calculateDailyCalories());

        NutritionProfile updatedProfile = profileRepository.save(profile);
        log.info("Nutrition profile updated for userId: {}", userId);

        return mapper.toDTO(updatedProfile);
    }

    @Override
    public void addAllergy(Long profileId, Long allergyId) {
        log.info("Adding allergy {} to profile {}", allergyId, profileId);

        NutritionProfile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Nutrition profile not found"));

        Allergy allergy = allergyRepository.findById(allergyId)
                .orElseThrow(() -> new ResourceNotFoundException("Allergy not found"));

        profile.getAllergies().add(allergy);
        profileRepository.save(profile);

        log.info("Allergy added successfully");
    }

    @Override
    public void removeAllergy(Long profileId, Long allergyId) {
        log.info("Removing allergy {} from profile {}", allergyId, profileId);

        NutritionProfile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Nutrition profile not found"));

        Allergy allergy = allergyRepository.findById(allergyId)
                .orElseThrow(() -> new ResourceNotFoundException("Allergy not found"));

        profile.getAllergies().remove(allergy);
        profileRepository.save(profile);

        log.info("Allergy removed successfully");
    }

    @Override
    public void addDietaryPreference(Long profileId, Long preferenceId) {
        log.info("Adding dietary preference {} to profile {}", preferenceId, profileId);

        NutritionProfile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Nutrition profile not found"));

        DietaryPreference preference = preferenceRepository.findById(preferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Dietary preference not found"));

        profile.getDietaryPreferences().add(preference);
        profileRepository.save(profile);

        log.info("Dietary preference added successfully");
    }

    @Override
    public void removeDietaryPreference(Long profileId, Long preferenceId) {
        log.info("Removing dietary preference {} from profile {}", preferenceId, profileId);

        NutritionProfile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Nutrition profile not found"));

        DietaryPreference preference = preferenceRepository.findById(preferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Dietary preference not found"));

        profile.getDietaryPreferences().remove(preference);
        profileRepository.save(profile);

        log.info("Dietary preference removed successfully");
    }
}

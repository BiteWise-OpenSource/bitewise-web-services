package com.bitewise.service.impl;

import com.bitewise.dto.request.NutritionProfileRequest;
import com.bitewise.dto.response.NutritionProfileResponse;
import com.bitewise.entity.Allergy;
import com.bitewise.entity.DietaryPreference;
import com.bitewise.entity.NutritionProfile;
import com.bitewise.entity.User;
import com.bitewise.exception.ResourceNotFoundException;
import com.bitewise.mapper.NutritionProfileMapper;
import com.bitewise.repository.AllergyRepository;
import com.bitewise.repository.DietaryPreferenceRepository;
import com.bitewise.repository.NutritionProfileRepository;
import com.bitewise.repository.UserRepository;
import com.bitewise.service.NutritionProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NutritionProfileServiceImpl implements NutritionProfileService {

    private final NutritionProfileRepository nutritionProfileRepository;
    private final UserRepository userRepository;
    private final AllergyRepository allergyRepository;
    private final DietaryPreferenceRepository dietaryPreferenceRepository;
    private final NutritionProfileMapper nutritionProfileMapper;

    @Override
    @Transactional
    public NutritionProfileResponse createOrUpdate(String email, NutritionProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        NutritionProfile profile = nutritionProfileRepository.findByUserId(user.getId())
                .orElse(NutritionProfile.builder().user(user).build());

        profile.setCurrentWeight(request.getCurrentWeight());
        profile.setTargetWeight(request.getTargetWeight());
        profile.setHeight(request.getHeight());
        profile.setAge(request.getAge());
        profile.setGender(request.getGender());
        profile.setActivityLevel(request.getActivityLevel());
        profile.setPrimaryGoal(request.getPrimaryGoal());
        profile.setDietType(request.getDietType());

        if (request.getAllergyIds() != null) {
            Set<Allergy> allergies = new HashSet<>(allergyRepository.findAllById(request.getAllergyIds()));
            profile.setAllergies(allergies);
        }

        if (request.getDietaryPreferenceIds() != null) {
            Set<DietaryPreference> preferences = new HashSet<>(dietaryPreferenceRepository.findAllById(request.getDietaryPreferenceIds()));
            profile.setDietaryPreferences(preferences);
        }

        calculateNutritionMetrics(profile);

        return nutritionProfileMapper.toResponse(nutritionProfileRepository.save(profile));
    }

    @Override
    public NutritionProfileResponse getByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        NutritionProfile profile = nutritionProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("El usuario aún no tiene un perfil nutricional"));

        return nutritionProfileMapper.toResponse(profile);
    }

    @Override
    @Transactional
    public void delete(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        NutritionProfile profile = nutritionProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("El usuario aún no tiene un perfil nutricional"));

        nutritionProfileRepository.delete(profile);
    }

    private void calculateNutritionMetrics(NutritionProfile profile) {
        double heightMeters = profile.getHeight() / 100.0;
        double bmi = profile.getCurrentWeight() / (heightMeters * heightMeters);
        profile.setCalculatedBMI(Math.round(bmi * 100.0) / 100.0);

        double bmr;
        if ("MALE".equalsIgnoreCase(profile.getGender())) {
            bmr = (10 * profile.getCurrentWeight()) + (6.25 * profile.getHeight()) - (5 * profile.getAge()) + 5;
        } else {
            bmr = (10 * profile.getCurrentWeight()) + (6.25 * profile.getHeight()) - (5 * profile.getAge()) - 161;
        }

        double activityFactor = switch (profile.getActivityLevel()) {
            case SEDENTARY -> 1.2;
            case LIGHT_EXERCISE -> 1.375;
            case MODERATE_EXERCISE -> 1.55;
            case ACTIVE -> 1.725;
            case VERY_ACTIVE -> 1.9;
        };

        double tdee = bmr * activityFactor;

        double calorieTarget = switch (profile.getPrimaryGoal()) {
            case WEIGHT_LOSS -> tdee - 500;
            case WEIGHT_GAIN, MUSCLE_BUILD -> tdee + 300;
            case MAINTAIN_WEIGHT -> tdee;
        };

        profile.setDailyCalorieTarget((int) Math.round(calorieTarget));

        switch (profile.getPrimaryGoal()) {
            case MUSCLE_BUILD -> {
                profile.setProteinPercentage(30.0);
                profile.setCarbohydratePercentage(40.0);
                profile.setFatPercentage(30.0);
            }
            case WEIGHT_LOSS -> {
                profile.setProteinPercentage(35.0);
                profile.setCarbohydratePercentage(35.0);
                profile.setFatPercentage(30.0);
            }
            default -> {
                profile.setProteinPercentage(25.0);
                profile.setCarbohydratePercentage(45.0);
                profile.setFatPercentage(30.0);
            }
        }
    }
}

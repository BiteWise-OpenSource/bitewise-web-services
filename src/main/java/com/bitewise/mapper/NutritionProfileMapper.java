package com.bitewise.mapper;

import com.bitewise.dto.response.NutritionProfileResponse;
import com.bitewise.entity.Allergy;
import com.bitewise.entity.DietaryPreference;
import com.bitewise.entity.NutritionProfile;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NutritionProfileMapper {

    public NutritionProfileResponse toDTO(NutritionProfile profile) {
        if (profile == null) {
            return null;
        }

        Set<NutritionProfileResponse.AllergyResponse> allergies = profile.getAllergies().stream()
                .map(this::mapAllergy)
                .collect(Collectors.toSet());

        Set<NutritionProfileResponse.DietaryPreferenceResponse> preferences = profile.getDietaryPreferences().stream()
                .map(this::mapPreference)
                .collect(Collectors.toSet());

        return NutritionProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .currentWeight(profile.getCurrentWeight())
                .targetWeight(profile.getTargetWeight())
                .height(profile.getHeight())
                .age(profile.getAge())
                .gender(profile.getGender())
                .activityLevel(profile.getActivityLevel().toString())
                .primaryGoal(profile.getPrimaryGoal().toString())
                .dietType(profile.getDietType().toString())
                .calculatedBMI(profile.getCalculatedBMI())
                .dailyCalorieTarget(profile.getDailyCalorieTarget())
                .proteinPercentage(profile.getProteinPercentage())
                .carbohydratePercentage(profile.getCarbohydratePercentage())
                .fatPercentage(profile.getFatPercentage())
                .allergies(allergies)
                .dietaryPreferences(preferences)
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    private NutritionProfileResponse.AllergyResponse mapAllergy(Allergy allergy) {
        return NutritionProfileResponse.AllergyResponse.builder()
                .id(allergy.getId())
                .name(allergy.getName())
                .description(allergy.getDescription())
                .build();
    }

    private NutritionProfileResponse.DietaryPreferenceResponse mapPreference(DietaryPreference preference) {
        return NutritionProfileResponse.DietaryPreferenceResponse.builder()
                .id(preference.getId())
                .name(preference.getName())
                .description(preference.getDescription())
                .dietType(preference.getDietType().toString())
                .build();
    }
}

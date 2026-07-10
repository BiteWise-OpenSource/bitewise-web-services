package com.bitewise.mapper;

import com.bitewise.dto.response.NutritionProfileResponse;
import com.bitewise.entity.Allergy;
import com.bitewise.entity.DietaryPreference;
import com.bitewise.entity.NutritionProfile;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class NutritionProfileMapper {

    public NutritionProfileResponse toResponse(NutritionProfile profile) {
        if (profile == null) {
            return null;
        }

        return NutritionProfileResponse.builder()
                .id(profile.getId())
                .currentWeight(profile.getCurrentWeight())
                .targetWeight(profile.getTargetWeight())
                .height(profile.getHeight())
                .age(profile.getAge())
                .gender(profile.getGender())
                .activityLevel(profile.getActivityLevel() != null ? profile.getActivityLevel().name() : null)
                .primaryGoal(profile.getPrimaryGoal() != null ? profile.getPrimaryGoal().name() : null)
                .dietType(profile.getDietType() != null ? profile.getDietType().name() : null)
                .calculatedBMI(profile.getCalculatedBMI())
                .dailyCalorieTarget(profile.getDailyCalorieTarget())
                .proteinPercentage(profile.getProteinPercentage())
                .carbohydratePercentage(profile.getCarbohydratePercentage())
                .fatPercentage(profile.getFatPercentage())
                .allergies(profile.getAllergies().stream().map(Allergy::getName).collect(Collectors.toSet()))
                .dietaryPreferences(profile.getDietaryPreferences().stream().map(DietaryPreference::getName).collect(Collectors.toSet()))
                .build();
    }
}

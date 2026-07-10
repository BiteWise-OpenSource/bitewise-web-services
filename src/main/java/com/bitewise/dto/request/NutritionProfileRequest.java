package com.bitewise.dto.request;

import com.bitewise.entity.ActivityLevel;
import com.bitewise.entity.DietType;
import com.bitewise.entity.HealthGoal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionProfileRequest {

    @NotNull
    @Positive
    private Double currentWeight;

    @NotNull
    @Positive
    private Double targetWeight;

    @NotNull
    @Positive
    private Double height;

    @NotNull
    @Positive
    private Integer age;

    @NotBlank
    private String gender;

    @NotNull
    private ActivityLevel activityLevel;

    @NotNull
    private HealthGoal primaryGoal;

    @NotNull
    private DietType dietType;

    private Set<Long> allergyIds;

    private Set<Long> dietaryPreferenceIds;

}

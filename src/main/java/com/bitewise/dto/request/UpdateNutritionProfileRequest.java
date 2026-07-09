package com.bitewise.dto.request;

import com.bitewise.entity.ActivityLevel;
import com.bitewise.entity.DietType;
import com.bitewise.entity.HealthGoal;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateNutritionProfileRequest {

    @DecimalMin(value = "20.0", message = "Current weight must be at least 20 kg")
    @DecimalMax(value = "500.0", message = "Current weight must be at most 500 kg")
    private Double currentWeight;

    @DecimalMin(value = "20.0", message = "Target weight must be at least 20 kg")
    @DecimalMax(value = "500.0", message = "Target weight must be at most 500 kg")
    private Double targetWeight;

    @DecimalMin(value = "100.0", message = "Height must be at least 100 cm")
    @DecimalMax(value = "250.0", message = "Height must be at most 250 cm")
    private Double height;

    @Min(value = 13, message = "Age must be at least 13 years")
    @Max(value = 120, message = "Age must be at most 120 years")
    private Integer age;

    @Pattern(regexp = "male|female|other", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Gender must be male, female, or other")
    private String gender;

    private ActivityLevel activityLevel;
    private HealthGoal primaryGoal;
    private DietType dietType;

    private Set<Long> allergyIds;
    private Set<Long> dietaryPreferenceIds;
}

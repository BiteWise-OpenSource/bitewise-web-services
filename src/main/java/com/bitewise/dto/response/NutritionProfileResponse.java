package com.bitewise.dto.response;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionProfileResponse {

    private Long id;

    private Double currentWeight;

    private Double targetWeight;

    private Double height;

    private Integer age;

    private String gender;

    private String activityLevel;

    private String primaryGoal;

    private String dietType;

    private Double calculatedBMI;

    private Integer dailyCalorieTarget;

    private Double proteinPercentage;

    private Double carbohydratePercentage;

    private Double fatPercentage;

    private Set<String> allergies;

    private Set<String> dietaryPreferences;

}
package com.bitewise.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionProfileResponse {

    private Long id;
    private Long userId;
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
    private Set<AllergyResponse> allergies;
    private Set<DietaryPreferenceResponse> dietaryPreferences;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AllergyResponse {
        private Long id;
        private String name;
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DietaryPreferenceResponse {
        private Long id;
        private String name;
        private String description;
        private String dietType;
    }
}

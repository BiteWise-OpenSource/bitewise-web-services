package com.bitewise.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeResponse {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Integer servings;
    private Integer cookingTimeMinutes;
    private Integer preparationTimeMinutes;
    private Integer totalCalories;
    private Double proteinGrams;
    private Double carbohydratesGrams;
    private Double fatGrams;
    private Double fiberGrams;
    private String mealType;
    private String cuisineType;
    private Boolean isVegetarian;
    private Boolean isVegan;
    private Boolean isGlutenFree;
    private Boolean isDairyFree;
    private Boolean isKeto;
    private Boolean isFavorite;
    private List<IngredientResponse> ingredients;
    private List<AllergyResponse> containsAllergies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IngredientResponse {
        private Long id;
        private String name;
        private String unit;
        private Double quantity;
        private String notes;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AllergyResponse {
        private Long id;
        private String name;
    }
}

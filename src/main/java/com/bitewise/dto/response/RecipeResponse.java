package com.bitewise.dto.response;

import lombok.*;

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

    private Integer totalCalories;

    private Integer servings;

    private Integer cookingTimeMinutes;

    private Double proteinGrams;

    private Double carbohydratesGrams;

    private Double fatGrams;

    private Double fiberGrams;

    private String mealType;

    private String cuisineType;

    private Boolean vegetarian;

    private Boolean vegan;

    private Boolean glutenFree;

    private Boolean dairyFree;

    private Boolean keto;

    private Boolean favorite;

    private List<IngredientResponse> ingredients;

    private List<String> allergies;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IngredientResponse {

        private String name;

        private Double quantity;

        private String unit;

    }

}
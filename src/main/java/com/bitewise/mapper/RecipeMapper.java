package com.bitewise.mapper;

import com.bitewise.dto.response.RecipeResponse;
import com.bitewise.entity.Allergy;
import com.bitewise.entity.Ingredient;
import com.bitewise.entity.Recipe;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RecipeMapper {

    public RecipeResponse toDTO(Recipe recipe) {
        if (recipe == null) {
            return null;
        }

        Set<RecipeResponse.IngredientResponse> ingredients = recipe.getIngredients().stream()
                .map(this::mapIngredient)
                .collect(Collectors.toSet());

        Set<RecipeResponse.AllergyResponse> allergies = recipe.getContainsAllergies().stream()
                .map(this::mapAllergy)
                .collect(Collectors.toSet());

        return RecipeResponse.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .description(recipe.getDescription())
                .imageUrl(recipe.getImageUrl())
                .servings(recipe.getServings())
                .cookingTimeMinutes(recipe.getCookingTimeMinutes())
                .preparationTimeMinutes(recipe.getPreparationTimeMinutes())
                .totalCalories(recipe.getTotalCalories())
                .proteinGrams(recipe.getProteinGrams())
                .carbohydratesGrams(recipe.getCarbohydratesGrams())
                .fatGrams(recipe.getFatGrams())
                .fiberGrams(recipe.getFiberGrams())
                .mealType(recipe.getMealType() != null ? recipe.getMealType().toString() : null)
                .cuisineType(recipe.getCuisineType() != null ? recipe.getCuisineType().toString() : null)
                .isVegetarian(recipe.getIsVegetarian())
                .isVegan(recipe.getIsVegan())
                .isGlutenFree(recipe.getIsGlutenFree())
                .isDairyFree(recipe.getIsDairyFree())
                .isKeto(recipe.getIsKeto())
                .isFavorite(recipe.getIsFavorite())
                .ingredients(ingredients)
                .containsAllergies(allergies)
                .createdAt(recipe.getCreatedAt())
                .updatedAt(recipe.getUpdatedAt())
                .build();
    }

    private RecipeResponse.IngredientResponse mapIngredient(Ingredient ingredient) {
        return RecipeResponse.IngredientResponse.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .unit(ingredient.getUnit())
                .quantity(ingredient.getQuantity())
                .notes(ingredient.getNotes())
                .build();
    }

    private RecipeResponse.AllergyResponse mapAllergy(Allergy allergy) {
        return RecipeResponse.AllergyResponse.builder()
                .id(allergy.getId())
                .name(allergy.getName())
                .build();
    }
}

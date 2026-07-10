package com.bitewise.mapper;

import com.bitewise.dto.response.RecipeResponse;
import com.bitewise.entity.Allergy;
import com.bitewise.entity.Ingredient;
import com.bitewise.entity.Recipe;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecipeMapper {

    public RecipeResponse toResponse(Recipe recipe) {
        if (recipe == null) {
            return null;
        }

        List<RecipeResponse.IngredientResponse> ingredients = recipe.getIngredients().stream()
                .map(this::toIngredientResponse)
                .collect(Collectors.toList());

        List<String> allergies = recipe.getAllergies().stream()
                .map(Allergy::getName)
                .collect(Collectors.toList());

        return RecipeResponse.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .description(recipe.getDescription())
                .imageUrl(recipe.getImageUrl())
                .totalCalories(recipe.getTotalCalories())
                .servings(recipe.getServings())
                .cookingTimeMinutes(recipe.getCookingTimeMinutes())
                .proteinGrams(recipe.getProteinGrams())
                .carbohydratesGrams(recipe.getCarbohydratesGrams())
                .fatGrams(recipe.getFatGrams())
                .fiberGrams(recipe.getFiberGrams())
                .mealType(recipe.getMealType() != null ? recipe.getMealType().name() : null)
                .cuisineType(recipe.getCuisineType() != null ? recipe.getCuisineType().name() : null)
                .vegetarian(recipe.getVegetarian())
                .vegan(recipe.getVegan())
                .glutenFree(recipe.getGlutenFree())
                .dairyFree(recipe.getDairyFree())
                .keto(recipe.getKeto())
                .favorite(recipe.getFavorite())
                .ingredients(ingredients)
                .allergies(allergies)
                .build();
    }

    private RecipeResponse.IngredientResponse toIngredientResponse(Ingredient ingredient) {
        return RecipeResponse.IngredientResponse.builder()
                .name(ingredient.getName())
                .quantity(ingredient.getQuantity())
                .unit(ingredient.getUnit())
                .build();
    }
}

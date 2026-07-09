package com.bitewise.external;

import com.bitewise.dto.external.EdamamRecipeResponse;
import com.bitewise.dto.external.EdamamSearchResponse;
import com.bitewise.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class EdamamRecipeAdapter {

    @Value("${edamam.api.url}")
    private String edamamApiUrl;

    @Value("${edamam.api.app-id}")
    private String appId;

    @Value("${edamam.api.app-key}")
    private String appKey;

    private final RestTemplate restTemplate;

    public EdamamSearchResponse searchRecipes(String query, Integer from, Integer to, String diet, String health) {
        log.info("Searching recipes on Edamam with query: {}", query);

        StringBuilder urlBuilder = new StringBuilder(edamamApiUrl)
                .append("?type=public")
                .append("&q=").append(query)
                .append("&app_id=").append(appId)
                .append("&app_key=").append(appKey);

        if (from != null) {
            urlBuilder.append("&from=").append(from);
        }
        if (to != null) {
            urlBuilder.append("&to=").append(to);
        }
        if (diet != null) {
            urlBuilder.append("&diet=").append(diet);
        }
        if (health != null) {
            urlBuilder.append("&health=").append(health);
        }

        try {
            EdamamSearchResponse response = restTemplate.getForObject(urlBuilder.toString(), EdamamSearchResponse.class);
            log.info("Successfully retrieved {} recipes from Edamam", response != null ? response.getCount() : 0);
            return response;
        } catch (Exception e) {
            log.error("Error searching recipes on Edamam: {}", e.getMessage());
            throw new RuntimeException("Failed to search recipes from external API", e);
        }
    }

    public Recipe translateExternalRecipeToInternal(EdamamRecipeResponse externalRecipe) {
        log.info("Translating external recipe: {}", externalRecipe.getRecipe().getLabel());

        EdamamRecipeResponse.Recipe recipe = externalRecipe.getRecipe();

        // Extract nutritional data
        double totalCalories = recipe.getCalories() != null ? recipe.getCalories() : 0;
        double protein = extractNutrient(recipe.getTotalNutrients(), "PROCNT");
        double carbs = extractNutrient(recipe.getTotalNutrients(), "CHOCDF");
        double fat = extractNutrient(recipe.getTotalNutrients(), "FAT");
        double fiber = extractNutrient(recipe.getTotalNutrients(), "FIBTG");

        // Detect dietary attributes
        boolean isVegetarian = detectVegetarian(recipe.getHealthLabels());
        boolean isVegan = detectVegan(recipe.getHealthLabels());
        boolean isGlutenFree = recipe.getHealthLabels().contains("gluten-free");
        boolean isDairyFree = recipe.getHealthLabels().contains("dairy-free");
        boolean isKeto = recipe.getHealthLabels().contains("keto") || recipe.getHealthLabels().contains("paleo");

        // Extract meal type and cuisine
        String mealType = extractMealType(recipe.getMealType());
        String cuisineType = extractCuisineType(recipe.getCuisineType());

        // Create internal recipe entity
        Recipe internalRecipe = Recipe.builder()
                .name(recipe.getLabel())
                .description("Recipe from " + recipe.getSource())
                .imageUrl(recipe.getImage())
                .externalRecipeId(extractRecipeId(recipe.getUri()))
                .externalSource("Edamam")
                .servings(recipe.getYield() != null ? recipe.getYield().intValue() : 1)
                .cookingTimeMinutes(recipe.getTotalTime() != null ? recipe.getTotalTime() : 30)
                .preparationTimeMinutes(15)
                .totalCalories((int) totalCalories)
                .proteinGrams(Math.round(protein * 100.0) / 100.0)
                .carbohydratesGrams(Math.round(carbs * 100.0) / 100.0)
                .fatGrams(Math.round(fat * 100.0) / 100.0)
                .fiberGrams(Math.round(fiber * 100.0) / 100.0)
                .mealType(parseMealType(mealType))
                .cuisineType(parseCuisineType(cuisineType))
                .isVegetarian(isVegetarian)
                .isVegan(isVegan)
                .isGlutenFree(isGlutenFree)
                .isDairyFree(isDairyFree)
                .isKeto(isKeto)
                .isFavorite(false)
                .build();

        // Map ingredients
        if (recipe.getIngredientLines() != null && !recipe.getIngredientLines().isEmpty()) {
            Set<Ingredient> ingredients = new HashSet<>();
            recipe.getIngredientLines().forEach(line -> {
                Ingredient ingredient = Ingredient.builder()
                        .name(line.getFood().getLabel())
                        .quantity(line.getWeight() != null ? line.getWeight() : line.getQuantity())
                        .unit(line.getMeasure() != null ? line.getMeasure() : "g")
                        .build();
                ingredients.add(ingredient);
            });
            internalRecipe.setIngredients(ingredients);
        }

        log.info("Recipe successfully translated: {}", internalRecipe.getName());
        return internalRecipe;
    }

    private double extractNutrient(java.util.Map<String, EdamamRecipeResponse.Recipe.Nutrient> nutrients, String nutrientKey) {
        if (nutrients != null && nutrients.containsKey(nutrientKey)) {
            return nutrients.get(nutrientKey).getQuantity();
        }
        return 0.0;
    }

    private boolean detectVegetarian(java.util.List<String> healthLabels) {
        if (healthLabels == null) return false;
        return healthLabels.stream()
                .anyMatch(label -> label.toLowerCase().contains("vegetarian"));
    }

    private boolean detectVegan(java.util.List<String> healthLabels) {
        if (healthLabels == null) return false;
        return healthLabels.stream()
                .anyMatch(label -> label.toLowerCase().contains("vegan"));
    }

    private String extractRecipeId(String uri) {
        if (uri != null && uri.contains("#recipe_")) {
            return uri.substring(uri.indexOf("#recipe_") + 8);
        }
        return uri;
    }

    private String extractMealType(java.util.List<String> mealTypes) {
        if (mealTypes != null && !mealTypes.isEmpty()) {
            return mealTypes.get(0);
        }
        return "SNACK";
    }

    private String extractCuisineType(java.util.List<String> cuisineTypes) {
        if (cuisineTypes != null && !cuisineTypes.isEmpty()) {
            return cuisineTypes.get(0);
        }
        return "AMERICAN";
    }

    private MealType parseMealType(String mealType) {
        if (mealType == null) return MealType.SNACK;
        try {
            return MealType.valueOf(mealType.toUpperCase().replace(" ", "_").replace("BRUNCH", "LUNCH"));
        } catch (IllegalArgumentException e) {
            return MealType.SNACK;
        }
    }

    private CuisineType parseCuisineType(String cuisineType) {
        if (cuisineType == null) return CuisineType.AMERICAN;
        try {
            return CuisineType.valueOf(cuisineType.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return CuisineType.AMERICAN;
        }
    }
}


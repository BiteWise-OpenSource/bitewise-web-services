package com.bitewise.service.domain;

import com.bitewise.dto.external.EdamamRecipeResponse;
import com.bitewise.dto.external.EdamamSearchResponse;
import com.bitewise.entity.Allergy;
import com.bitewise.entity.DietaryPreference;
import com.bitewise.entity.Ingredient;
import com.bitewise.entity.Recipe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalRecipeAdapter {

    @Value("${edamam.api.url}")
    private String edamamApiUrl;

    @Value("${edamam.api.app-id}")
    private String appId;

    @Value("${edamam.api.app-key}")
    private String appKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Recipe> searchRecipes(String query, Set<Allergy> allergies, Set<DietaryPreference> preferences) {
        try {
            String url = buildSearchUrl(query, allergies, preferences);
            EdamamSearchResponse response = restTemplate.getForObject(url, EdamamSearchResponse.class);
            
            if (response != null && response.getHits() != null) {
                return response.getHits().stream()
                        .map(hit -> adaptToRecipe(hit.getRecipe()))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error fetching recipes from Edamam API", e);
        }
        
        return new ArrayList<>();
    }

    private String buildSearchUrl(String query, Set<Allergy> allergies, Set<DietaryPreference> preferences) {
        StringBuilder urlBuilder = new StringBuilder(edamamApiUrl);
        urlBuilder.append("?type=public");
        urlBuilder.append("&app_id=").append(appId);
        urlBuilder.append("&app_key=").append(appKey);
        urlBuilder.append("&q=").append(query.replace(" ", "+"));
        
        // Add dietary restrictions
        if (preferences != null) {
            for (DietaryPreference pref : preferences) {
                switch (pref.getName()) {
                    case "Vegetarian":
                        urlBuilder.append("&diet=vegetarian");
                        break;
                    case "Vegan":
                        urlBuilder.append("&health=vegan");
                        break;
                    case "Gluten Free":
                        urlBuilder.append("&health=gluten-free");
                        break;
                }
            }
        }
        
        // Add allergy exclusions
        if (allergies != null) {
            for (Allergy allergy : allergies) {
                urlBuilder.append("&excluded=").append(allergy.getName().replace(" ", "+"));
            }
        }
        
        return urlBuilder.toString();
    }

    private Recipe adaptToRecipe(EdamamRecipeResponse edamamRecipe) {
        return Recipe.builder()
                .name(edamamRecipe.getLabel())
                .description(edamamRecipe.getLabel())
                .imageUrl(edamamRecipe.getImage())
                .externalRecipeId(edamamRecipe.getUri())
                .externalSource("edamam")
                .servings(edamamRecipe.getYield() != null ? edamamRecipe.getYield().intValue() : 1)
                .cookingTimeMinutes(0) // Edamam doesn't provide this separately
                .preparationTimeMinutes(edamamRecipe.getTotalTime() != null ? edamamRecipe.getTotalTime().intValue() : 0)
                .totalCalories(edamamRecipe.getCalories() != null ? edamamRecipe.getCalories().intValue() : 0)
                .proteinGrams(extractNutrient(edamamRecipe, "PROCNT"))
                .carbohydratesGrams(extractNutrient(edamamRecipe, "CHOCDF"))
                .fatGrams(extractNutrient(edamamRecipe, "FAT"))
                .fiberGrams(extractNutrient(edamamRecipe, "FIBTG"))
                .vegetarian(edamamRecipe.getHealthLabels() != null && 
                           edamamRecipe.getHealthLabels().contains("Vegetarian"))
                .vegan(edamamRecipe.getHealthLabels() != null && 
                       edamamRecipe.getHealthLabels().contains("Vegan"))
                .glutenFree(edamamRecipe.getHealthLabels() != null && 
                           edamamRecipe.getHealthLabels().contains("Gluten-Free"))
                .dairyFree(edamamRecipe.getHealthLabels() != null && 
                           edamamRecipe.getHealthLabels().contains("Dairy-Free"))
                .keto(edamamRecipe.getHealthLabels() != null && 
                      edamamRecipe.getHealthLabels().contains("Keto"))
                .ingredients(new HashSet<>(adaptIngredients(edamamRecipe)))
                .allergies(new HashSet<>())
                .build();
    }

    private double extractNutrient(EdamamRecipeResponse recipe, String nutrientName) {
        if (recipe.getTotalNutrients() != null) {
            Object nutrient = recipe.getTotalNutrients().get(nutrientName);
            if (nutrient instanceof java.util.Map) {
                Object quantity = ((java.util.Map<?, ?>) nutrient).get("quantity");
                if (quantity instanceof Number) {
                    return ((Number) quantity).doubleValue();
                }
            }
        }
        return 0.0;
    }

    private List<Ingredient> adaptIngredients(EdamamRecipeResponse edamamRecipe) {
        List<Ingredient> ingredients = new ArrayList<>();
        
        // Placeholder - adapt based on actual EdamamRecipeResponse structure
        // The actual implementation depends on the DTO structure
        // For now, return empty list to avoid compilation errors
        
        return ingredients;
    }
}

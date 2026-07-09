package com.bitewise.service;

import com.bitewise.dto.request.RecipeSearchRequest;
import com.bitewise.dto.response.RecipeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecipeService {
    RecipeResponse getRecipeById(Long id);
    Page<RecipeResponse> searchRecipes(RecipeSearchRequest request, Pageable pageable);
    Page<RecipeResponse> findFavorites(Pageable pageable);
    void toggleFavorite(Long recipeId, boolean isFavorite);
    Page<RecipeResponse> findByDietaryPreference(String dietaryType, Pageable pageable);
}

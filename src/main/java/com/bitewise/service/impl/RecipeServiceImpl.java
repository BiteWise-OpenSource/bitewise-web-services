package com.bitewise.service.impl;

import com.bitewise.dto.external.EdamamRecipeResponse;
import com.bitewise.dto.external.EdamamSearchResponse;
import com.bitewise.dto.request.RecipeSearchRequest;
import com.bitewise.dto.response.RecipeResponse;
import com.bitewise.entity.Recipe;
import com.bitewise.exception.ResourceNotFoundException;
import com.bitewise.external.EdamamRecipeAdapter;
import com.bitewise.mapper.RecipeMapper;
import com.bitewise.repository.RecipeRepository;
import com.bitewise.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final EdamamRecipeAdapter edamamRecipeAdapter;
    private final RecipeMapper recipeMapper;

    @Override
    @Transactional(readOnly = true)
    public RecipeResponse getRecipeById(Long id) {
        log.info("Fetching recipe with ID: {}", id);
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));
        return recipeMapper.toDTO(recipe);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecipeResponse> searchRecipes(RecipeSearchRequest request, Pageable pageable) {
        log.info("Searching recipes with query: {}", request.getQuery());

        // First try to find cached recipes locally
        Page<Recipe> cachedRecipes = recipeRepository.searchByName(request.getQuery(), pageable);
        if (cachedRecipes.hasContent()) {
            log.info("Found {} cached recipes", cachedRecipes.getNumberOfElements());
            return cachedRecipes.map(recipeMapper::toDTO);
        }

        // If no cached results, search from Edamam API
        try {
            EdamamSearchResponse edamamResponse = edamamRecipeAdapter.searchRecipes(
                    request.getQuery(),
                    pageable.getPageNumber() * pageable.getPageSize(),
                    (pageable.getPageNumber() + 1) * pageable.getPageSize(),
                    request.getDiet(),
                    request.getHealth()
            );

            if (edamamResponse == null || edamamResponse.getHits() == null || edamamResponse.getHits().isEmpty()) {
                log.info("No recipes found for query: {}", request.getQuery());
                return Page.empty(pageable);
            }

            // Translate external recipes to internal domain and save
            List<Recipe> recipes = edamamResponse.getHits().stream()
                    .map(EdamamSearchResponse.Hit::getRecipe)
                    .map(edamamRecipeAdapter::translateExternalRecipeToInternal)
                    .collect(Collectors.toList());

            // Save to database for caching
            List<Recipe> savedRecipes = recipes.stream()
                    .filter(recipe -> !recipeRepository.existsByExternalRecipeId(recipe.getExternalRecipeId()))
                    .peek(recipe -> log.debug("Saving new recipe: {}", recipe.getName()))
                    .map(recipeRepository::save)
                    .collect(Collectors.toList());

            log.info("Saved {} new recipes to database", savedRecipes.size());

            // Map to DTOs
            List<RecipeResponse> responses = recipes.stream()
                    .map(recipeMapper::toDTO)
                    .collect(Collectors.toList());

            return new org.springframework.data.domain.PageImpl<>(responses, pageable, edamamResponse.getCount());
        } catch (Exception e) {
            log.error("Error searching recipes from Edamam: {}", e.getMessage());
            throw new RuntimeException("Failed to search recipes: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecipeResponse> findFavorites(Pageable pageable) {
        log.info("Fetching favorite recipes");
        Page<Recipe> favorites = recipeRepository.findFavorites(pageable);
        return favorites.map(recipeMapper::toDTO);
    }

    @Override
    public void toggleFavorite(Long recipeId, boolean isFavorite) {
        log.info("Toggling favorite status for recipe ID: {} to: {}", recipeId, isFavorite);
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));
        recipe.setIsFavorite(isFavorite);
        recipeRepository.save(recipe);
        log.debug("Recipe favorite status updated");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecipeResponse> findByDietaryPreference(String dietaryType, Pageable pageable) {
        log.info("Fetching recipes by dietary preference: {}", dietaryType);
        Page<Recipe> recipes = switch (dietaryType.toLowerCase()) {
            case "vegetarian" -> recipeRepository.findVegetarian(pageable);
            case "vegan" -> recipeRepository.findVegan(pageable);
            case "gluten-free" -> recipeRepository.findGlutenFree(pageable);
            case "dairy-free" -> recipeRepository.findDairyFree(pageable);
            case "keto" -> recipeRepository.findKeto(pageable);
            default -> {
                log.warn("Unknown dietary preference: {}", dietaryType);
                yield Page.empty(pageable);
            }
        };
        return recipes.map(recipeMapper::toDTO);
    }
}

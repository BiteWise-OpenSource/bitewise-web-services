package com.bitewise.service.impl;

import com.bitewise.dto.external.EdamamRecipeResponse;
import com.bitewise.dto.request.RecipeSearchRequest;
import com.bitewise.dto.response.RecipeResponse;
import com.bitewise.entity.Ingredient;
import com.bitewise.entity.Recipe;
import com.bitewise.exception.ResourceNotFoundException;
import com.bitewise.external.EdamamRecipeAdapter;
import com.bitewise.mapper.RecipeMapper;
import com.bitewise.repository.RecipeRepository;
import com.bitewise.service.RecipeService;
import com.bitewise.specification.RecipeSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;
    private final EdamamRecipeAdapter edamamRecipeAdapter;

    @Override
    @Transactional
    public List<RecipeResponse> searchExternalRecipes(RecipeSearchRequest request) {
        List<EdamamRecipeResponse> externalRecipes = edamamRecipeAdapter.searchRecipes(request.getQuery());

        return externalRecipes.stream()
                .map(this::saveIfNotExists)
                .map(recipeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RecipeResponse> getRecipes(String name, Boolean vegetarian, Boolean vegan, Boolean glutenFree,
                                            Boolean dairyFree, Boolean keto, Boolean favorite, Pageable pageable) {

        Specification<Recipe> spec = Specification.where(RecipeSpecification.hasNameLike(name))
                .and(RecipeSpecification.isVegetarian(vegetarian))
                .and(RecipeSpecification.isVegan(vegan))
                .and(RecipeSpecification.isGlutenFree(glutenFree))
                .and(RecipeSpecification.isDairyFree(dairyFree))
                .and(RecipeSpecification.isKeto(keto))
                .and(RecipeSpecification.isFavorite(favorite));

        return recipeRepository.findAll(spec, pageable).map(recipeMapper::toResponse);
    }

    @Override
    public RecipeResponse getById(Long id) {
        return recipeMapper.toResponse(findById(id));
    }

    @Override
    @Transactional
    public RecipeResponse toggleFavorite(Long id) {
        Recipe recipe = findById(id);
        recipe.setFavorite(!recipe.getFavorite());
        return recipeMapper.toResponse(recipeRepository.save(recipe));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        recipeRepository.delete(findById(id));
    }

    private Recipe findById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con id: " + id));
    }

    private Recipe saveIfNotExists(EdamamRecipeResponse external) {
        return recipeRepository.findByExternalRecipeId(external.getUri())
                .orElseGet(() -> recipeRepository.save(buildRecipeFromExternal(external)));
    }

    private Recipe buildRecipeFromExternal(EdamamRecipeResponse external) {
        Set<Ingredient> ingredients = new HashSet<>();
        if (external.getIngredientLines() != null) {
            ingredients = external.getIngredientLines().stream()
                    .map(line -> Ingredient.builder().name(line).build())
                    .collect(Collectors.toSet());
        }

        return Recipe.builder()
                .name(external.getLabel())
                .imageUrl(external.getImage())
                .externalRecipeId(external.getUri())
                .externalSource(external.getSource())
                .servings(external.getYield() != null ? external.getYield().intValue() : 1)
                .cookingTimeMinutes(external.getTotalTime() != null ? external.getTotalTime().intValue() : 0)
                .preparationTimeMinutes(0)
                .totalCalories(external.getCalories() != null ? external.getCalories().intValue() : 0)
                .proteinGrams(getNutrient(external, "PROCNT"))
                .carbohydratesGrams(getNutrient(external, "CHOCDF"))
                .fatGrams(getNutrient(external, "FAT"))
                .fiberGrams(getNutrient(external, "FIBTG"))
                .vegetarian(hasLabel(external, "Vegetarian"))
                .vegan(hasLabel(external, "Vegan"))
                .glutenFree(hasLabel(external, "Gluten-Free"))
                .dairyFree(hasLabel(external, "Dairy-Free"))
                .keto(hasLabel(external, "Keto-Friendly"))
                .ingredients(ingredients)
                .build();
    }

    private Double getNutrient(EdamamRecipeResponse external, String key) {
        if (external.getTotalNutrients() == null || !external.getTotalNutrients().containsKey(key)) {
            return 0.0;
        }
        return external.getTotalNutrients().get(key).getQuantity();
    }

    private boolean hasLabel(EdamamRecipeResponse external, String label) {
        return external.getHealthLabels() != null && external.getHealthLabels().contains(label);
    }
}

package com.bitewise.controller;

import com.bitewise.dto.request.RecipeSearchRequest;
import com.bitewise.dto.response.RecipeResponse;
import com.bitewise.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
@Slf4j
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getRecipeById(@PathVariable Long id) {
        log.info("GET /api/v1/recipes/{}", id);
        RecipeResponse response = recipeService.getRecipeById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<RecipeResponse>> searchRecipes(
            @Valid @RequestBody RecipeSearchRequest request,
            Pageable pageable) {
        log.info("POST /api/v1/recipes/search with query: {}", request.getQuery());
        Page<RecipeResponse> recipes = recipeService.searchRecipes(request, pageable);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/favorites")
    public ResponseEntity<Page<RecipeResponse>> getFavorites(Pageable pageable) {
        log.info("GET /api/v1/recipes/favorites");
        Page<RecipeResponse> favorites = recipeService.findFavorites(pageable);
        return ResponseEntity.ok(favorites);
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<Void> addFavorite(@PathVariable Long id) {
        log.info("POST /api/v1/recipes/{}/favorite", id);
        recipeService.toggleFavorite(id, true);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long id) {
        log.info("DELETE /api/v1/recipes/{}/favorite", id);
        recipeService.toggleFavorite(id, false);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter/dietary")
    public ResponseEntity<Page<RecipeResponse>> getByDietaryPreference(
            @RequestParam String type,
            Pageable pageable) {
        log.info("GET /api/v1/recipes/filter/dietary?type={}", type);
        Page<RecipeResponse> recipes = recipeService.findByDietaryPreference(type, pageable);
        return ResponseEntity.ok(recipes);
    }
}

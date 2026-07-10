package com.bitewise.controller;

import com.bitewise.dto.request.RecipeSearchRequest;
import com.bitewise.dto.response.RecipeResponse;
import com.bitewise.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping("/search-external")
    public ResponseEntity<List<RecipeResponse>> searchExternal(@Valid @RequestBody RecipeSearchRequest request) {
        return ResponseEntity.ok(recipeService.searchExternalRecipes(request));
    }

    @GetMapping
    public ResponseEntity<Page<RecipeResponse>> getRecipes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean vegetarian,
            @RequestParam(required = false) Boolean vegan,
            @RequestParam(required = false) Boolean glutenFree,
            @RequestParam(required = false) Boolean dairyFree,
            @RequestParam(required = false) Boolean keto,
            @RequestParam(required = false) Boolean favorite,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(recipeService.getRecipes(name, vegetarian, vegan, glutenFree, dairyFree, keto, favorite, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.getById(id));
    }

    @PatchMapping("/{id}/favorite")
    public ResponseEntity<RecipeResponse> toggleFavorite(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.toggleFavorite(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recipeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

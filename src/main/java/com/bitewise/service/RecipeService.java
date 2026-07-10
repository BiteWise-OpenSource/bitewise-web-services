package com.bitewise.service;

import com.bitewise.dto.request.RecipeSearchRequest;
import com.bitewise.dto.response.RecipeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecipeService {

    List<RecipeResponse> searchExternalRecipes(RecipeSearchRequest request);

    Page<RecipeResponse> getRecipes(String name, Boolean vegetarian, Boolean vegan, Boolean glutenFree,
                                     Boolean dairyFree, Boolean keto, Boolean favorite, Pageable pageable);

    RecipeResponse getById(Long id);

    RecipeResponse toggleFavorite(Long id);

    void delete(Long id);
}

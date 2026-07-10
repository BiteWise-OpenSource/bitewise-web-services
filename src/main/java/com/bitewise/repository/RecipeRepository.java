package com.bitewise.repository;

import com.bitewise.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {

    Page<Recipe> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Recipe> findByFavoriteTrue(Pageable pageable);

    Page<Recipe> findByVegetarianTrue(Pageable pageable);

    Page<Recipe> findByVeganTrue(Pageable pageable);

    Page<Recipe> findByGlutenFreeTrue(Pageable pageable);

    Page<Recipe> findByDairyFreeTrue(Pageable pageable);

    Page<Recipe> findByKetoTrue(Pageable pageable);

    boolean existsByExternalRecipeId(String externalRecipeId);

    Optional<Recipe> findByExternalRecipeId(String externalRecipeId);

}

package com.bitewise.repository;

import com.bitewise.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Optional<Recipe> findByExternalRecipeId(String externalRecipeId);
    boolean existsByExternalRecipeId(String externalRecipeId);

    @Query("SELECT r FROM Recipe r WHERE r.name ILIKE CONCAT('%', :query, '%')")
    Page<Recipe> searchByName(String query, Pageable pageable);

    @Query("SELECT r FROM Recipe r WHERE r.isVegetarian = true")
    Page<Recipe> findVegetarian(Pageable pageable);

    @Query("SELECT r FROM Recipe r WHERE r.isVegan = true")
    Page<Recipe> findVegan(Pageable pageable);

    @Query("SELECT r FROM Recipe r WHERE r.isGlutenFree = true")
    Page<Recipe> findGlutenFree(Pageable pageable);

    @Query("SELECT r FROM Recipe r WHERE r.isDairyFree = true")
    Page<Recipe> findDairyFree(Pageable pageable);

    @Query("SELECT r FROM Recipe r WHERE r.isKeto = true")
    Page<Recipe> findKeto(Pageable pageable);

    @Query("SELECT r FROM Recipe r WHERE r.isFavorite = true ORDER BY r.updatedAt DESC")
    Page<Recipe> findFavorites(Pageable pageable);
}

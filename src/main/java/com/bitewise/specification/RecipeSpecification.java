package com.bitewise.specification;

import com.bitewise.entity.Recipe;
import org.springframework.data.jpa.domain.Specification;

public class RecipeSpecification {

    private RecipeSpecification() {
    }

    public static Specification<Recipe> hasNameLike(String name) {
        return (root, query, cb) -> (name == null || name.isBlank())
                ? cb.conjunction()
                : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Recipe> isVegetarian(Boolean vegetarian) {
        return (root, query, cb) -> (vegetarian == null || !vegetarian)
                ? cb.conjunction()
                : cb.isTrue(root.get("vegetarian"));
    }

    public static Specification<Recipe> isVegan(Boolean vegan) {
        return (root, query, cb) -> (vegan == null || !vegan)
                ? cb.conjunction()
                : cb.isTrue(root.get("vegan"));
    }

    public static Specification<Recipe> isGlutenFree(Boolean glutenFree) {
        return (root, query, cb) -> (glutenFree == null || !glutenFree)
                ? cb.conjunction()
                : cb.isTrue(root.get("glutenFree"));
    }

    public static Specification<Recipe> isDairyFree(Boolean dairyFree) {
        return (root, query, cb) -> (dairyFree == null || !dairyFree)
                ? cb.conjunction()
                : cb.isTrue(root.get("dairyFree"));
    }

    public static Specification<Recipe> isKeto(Boolean keto) {
        return (root, query, cb) -> (keto == null || !keto)
                ? cb.conjunction()
                : cb.isTrue(root.get("keto"));
    }

    public static Specification<Recipe> isFavorite(Boolean favorite) {
        return (root, query, cb) -> (favorite == null || !favorite)
                ? cb.conjunction()
                : cb.isTrue(root.get("favorite"));
    }
}

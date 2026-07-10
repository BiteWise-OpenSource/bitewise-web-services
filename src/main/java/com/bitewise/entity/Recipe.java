package com.bitewise.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "recipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(length = 500)
    private String imageUrl;

    @Column(nullable = false, unique = true)
    private String externalRecipeId;

    @Column(nullable = false)
    private String externalSource;

    @Column(nullable = false)
    private Integer servings;

    @Column(nullable = false)
    private Integer cookingTimeMinutes;

    @Column(nullable = false)
    private Integer preparationTimeMinutes;

    @Column(nullable = false)
    private Integer totalCalories;

    @Column(nullable = false)
    private Double proteinGrams;

    @Column(nullable = false)
    private Double carbohydratesGrams;

    @Column(nullable = false)
    private Double fatGrams;

    @Column(nullable = false)
    private Double fiberGrams;

    @Enumerated(EnumType.STRING)
    private MealType mealType;

    @Enumerated(EnumType.STRING)
    private CuisineType cuisineType;

    @Builder.Default
    @Column(nullable = false)
    private Boolean vegetarian = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean vegan = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean glutenFree = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean dairyFree = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean keto = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean favorite = false;

    @Builder.Default
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "recipe_id")
    private Set<Ingredient> ingredients = new HashSet<>();

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "recipe_allergies",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "allergy_id")
    )
    private Set<Allergy> allergies = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
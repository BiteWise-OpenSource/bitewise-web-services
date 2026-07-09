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

    @Column(nullable = false)
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

    @Column(nullable = false)
    private Boolean isVegetarian;

    @Column(nullable = false)
    private Boolean isVegan;

    @Column(nullable = false)
    private Boolean isGlutenFree;

    @Column(nullable = false)
    private Boolean isDairyFree;

    @Column(nullable = false)
    private Boolean isKeto;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "recipe_id")
    @Builder.Default
    private Set<Ingredient> ingredients = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "recipe_allergies",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "allergy_id")
    )
    @Builder.Default
    private Set<Allergy> containsAllergies = new HashSet<>();

    @Column(nullable = false)
    private Boolean isFavorite;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isFavorite == null) {
            isFavorite = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

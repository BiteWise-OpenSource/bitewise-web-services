package com.bitewise.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "nutrition_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Double currentWeight;

    @Column(nullable = false)
    private Double targetWeight;

    @Column(nullable = false)
    private Double height;

    @Column(nullable = false)
    private Integer age;

    @Column(length = 20)
    private String gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityLevel activityLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HealthGoal primaryGoal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DietType dietType;

    @Column(nullable = false)
    private Double calculatedBMI;

    @Column(nullable = false)
    private Integer dailyCalorieTarget;

    @Column(nullable = false)
    private Double proteinPercentage;

    @Column(nullable = false)
    private Double carbohydratePercentage;

    @Column(nullable = false)
    private Double fatPercentage;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "profile_allergies",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "allergy_id")
    )
    @Builder.Default
    private Set<Allergy> allergies = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "profile_diet_preferences",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "diet_type_id")
    )
    @Builder.Default
    private Set<DietaryPreference> dietaryPreferences = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateBMI();
        calculateMacronutrients();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
        calculateBMI();
        calculateMacronutrients();
    }

    private void calculateBMI() {
        // BMI = weight (kg) / (height (m))^2
        double heightInMeters = height / 100.0;
        this.calculatedBMI = Math.round((currentWeight / (heightInMeters * heightInMeters)) * 100.0) / 100.0;
    }

    private void calculateMacronutrients() {
        // Default macronutrient distribution
        this.proteinPercentage = 30.0;
        this.carbohydratePercentage = 45.0;
        this.fatPercentage = 25.0;

        // Adjust based on goal
        switch (primaryGoal) {
            case MUSCLE_BUILD:
                this.proteinPercentage = 35.0;
                this.carbohydratePercentage = 45.0;
                this.fatPercentage = 20.0;
                break;
            case WEIGHT_LOSS:
                this.proteinPercentage = 35.0;
                this.carbohydratePercentage = 40.0;
                this.fatPercentage = 25.0;
                break;
            case WEIGHT_GAIN:
                this.proteinPercentage = 25.0;
                this.carbohydratePercentage = 55.0;
                this.fatPercentage = 20.0;
                break;
            default:
                break;
        }
    }

    public Integer calculateDailyCalories() {
        // Harris-Benedict formula for BMR
        double bmr;
        if ("male".equalsIgnoreCase(gender)) {
            bmr = 88.362 + (13.397 * currentWeight) + (4.799 * height) - (5.677 * age);
        } else {
            bmr = 447.593 + (9.247 * currentWeight) + (3.098 * height) - (4.330 * age);
        }

        // Apply activity factor
        double activityFactor = switch (activityLevel) {
            case SEDENTARY -> 1.2;
            case LIGHT_EXERCISE -> 1.375;
            case MODERATE_EXERCISE -> 1.55;
            case ACTIVE -> 1.725;
            case VERY_ACTIVE -> 1.9;
        };

        double tdee = bmr * activityFactor;

        // Adjust based on goal
        double goalAdjustment = switch (primaryGoal) {
            case WEIGHT_LOSS -> -500;      // 500 kcal deficit
            case WEIGHT_GAIN -> 500;       // 500 kcal surplus
            case MUSCLE_BUILD -> 300;      // 300 kcal surplus
            default -> 0;
        };

        return (int) (tdee + goalAdjustment);
    }
}

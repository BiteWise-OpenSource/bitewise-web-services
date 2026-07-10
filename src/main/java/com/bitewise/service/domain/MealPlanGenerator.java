package com.bitewise.service.domain;

import com.bitewise.entity.*;
import com.bitewise.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MealPlanGenerator {

    private final RecipeRepository recipeRepository;
    private final SubscriptionGate subscriptionGate;

    public MealPlan generateMealPlan(User user, NutritionProfile profile, LocalDate startDate, LocalDate endDate) {
        boolean isPremium = subscriptionGate.isPremium(user.getId());
        int days = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;

        MealPlan mealPlan = MealPlan.builder()
                .user(user)
                .startDate(startDate)
                .endDate(endDate)
                .dailyCalorieTarget(profile.getDailyCalorieTarget())
                .proteinPercentage(profile.getProteinPercentage())
                .carbohydratePercentage(profile.getCarbohydratePercentage())
                .fatPercentage(profile.getFatPercentage())
                .primaryGoal(profile.getPrimaryGoal())
                .dailyMeals(new HashSet<>())
                .build();

        Set<Allergy> userAllergies = profile.getAllergies();
        Set<DietaryPreference> preferences = profile.getDietaryPreferences();

        for (int i = 0; i < days; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            
            // Generate meals for each meal type
            generateDailyMeals(mealPlan, currentDate, userAllergies, preferences, isPremium);
        }

        return mealPlan;
    }

    private void generateDailyMeals(MealPlan mealPlan, LocalDate date, Set<Allergy> allergies, 
                                   Set<DietaryPreference> preferences, boolean isPremium) {
        MealType[] mealTypes = MealType.values();
        
        for (MealType mealType : mealTypes) {
            Recipe recipe = findSuitableRecipe(mealType, allergies, preferences, isPremium);
            
            if (recipe != null) {
                DailyMeal dailyMeal = DailyMeal.builder()
                        .mealPlan(mealPlan)
                        .mealDate(date)
                        .mealType(mealType)
                        .recipe(recipe)
                        .plannedCalories(recipe.getTotalCalories())
                        .plannedProtein(recipe.getProteinGrams())
                        .plannedCarbs(recipe.getCarbohydratesGrams())
                        .plannedFat(recipe.getFatGrams())
                        .isConsumed(false)
                        .consumptionLogs(new HashSet<>())
                        .build();
                
                mealPlan.getDailyMeals().add(dailyMeal);
            }
        }
    }

    private Recipe findSuitableRecipe(MealType mealType, Set<Allergy> allergies, 
                                      Set<DietaryPreference> preferences, boolean isPremium) {
        List<Recipe> recipes = recipeRepository.findAll();
        
        return recipes.stream()
                .filter(recipe -> recipe.getMealType() == mealType || recipe.getMealType() == null)
                .filter(recipe -> !containsAllergen(recipe, allergies))
                .filter(recipe -> matchesPreferences(recipe, preferences))
                .findFirst()
                .orElse(null);
    }

    private boolean containsAllergen(Recipe recipe, Set<Allergy> allergies) {
        if (allergies == null || allergies.isEmpty()) {
            return false;
        }
        return recipe.getAllergies().stream()
                .anyMatch(allergies::contains);
    }

    private boolean matchesPreferences(Recipe recipe, Set<DietaryPreference> preferences) {
        if (preferences == null || preferences.isEmpty()) {
            return true;
        }
        
        for (DietaryPreference pref : preferences) {
            switch (pref.getName()) {
                case "Vegetarian":
                    if (!recipe.getVegetarian()) return false;
                    break;
                case "Vegan":
                    if (!recipe.getVegan()) return false;
                    break;
                case "Gluten Free":
                    if (!recipe.getGlutenFree()) return false;
                    break;
                case "Dairy Free":
                    if (!recipe.getDairyFree()) return false;
                    break;
                case "Keto":
                    if (!recipe.getKeto()) return false;
                    break;
            }
        }
        return true;
    }
}
